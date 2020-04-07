// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import java.util.Set;
import com.google.common.collect.Sets;
import java.nio.file.StandardWatchEventKinds;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.Closeable;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.Watchable;
import com.google.common.base.Preconditions;
import java.util.Iterator;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

final class PollingWatchService extends AbstractWatchService
{
    private static final ThreadFactory THREAD_FACTORY;
    private final ScheduledExecutorService pollingService;
    private final ConcurrentMap<Key, Snapshot> snapshots;
    private final FileSystemView view;
    private final PathService pathService;
    private final FileSystemState fileSystemState;
    @VisibleForTesting
    final long interval;
    @VisibleForTesting
    final TimeUnit timeUnit;
    private ScheduledFuture<?> pollingFuture;
    private final Runnable pollingTask;
    
    PollingWatchService(final FileSystemView view, final PathService pathService, final FileSystemState fileSystemState, final long interval, final TimeUnit timeUnit) {
        this.pollingService = Executors.newSingleThreadScheduledExecutor(PollingWatchService.THREAD_FACTORY);
        this.snapshots = new ConcurrentHashMap<Key, Snapshot>();
        this.pollingTask = new Runnable() {
            @Override
            public void run() {
                synchronized (PollingWatchService.this) {
                    for (final Map.Entry<Key, Snapshot> entry : PollingWatchService.this.snapshots.entrySet()) {
                        final Key key = entry.getKey();
                        final Snapshot previousSnapshot = entry.getValue();
                        final JimfsPath path = (JimfsPath)key.watchable();
                        try {
                            final Snapshot newSnapshot = PollingWatchService.this.takeSnapshot(path);
                            final boolean posted = previousSnapshot.postChanges(newSnapshot, key);
                            entry.setValue(newSnapshot);
                            if (!posted) {
                                continue;
                            }
                            key.signal();
                        }
                        catch (IOException e) {
                            key.cancel();
                        }
                    }
                }
            }
        };
        this.view = Preconditions.checkNotNull(view);
        this.pathService = Preconditions.checkNotNull(pathService);
        this.fileSystemState = Preconditions.checkNotNull(fileSystemState);
        Preconditions.checkArgument(interval >= 0L, "interval (%s) may not be negative", interval);
        this.interval = interval;
        this.timeUnit = Preconditions.checkNotNull(timeUnit);
        fileSystemState.register(this);
    }
    
    @Override
    public Key register(final Watchable watchable, final Iterable<? extends WatchEvent.Kind<?>> eventTypes) throws IOException {
        final JimfsPath path = this.checkWatchable(watchable);
        final Key key = super.register(path, eventTypes);
        final Snapshot snapshot = this.takeSnapshot(path);
        synchronized (this) {
            this.snapshots.put(key, snapshot);
            if (this.pollingFuture == null) {
                this.startPolling();
            }
        }
        return key;
    }
    
    private JimfsPath checkWatchable(final Watchable watchable) {
        if (!(watchable instanceof JimfsPath) || !this.isSameFileSystem((Path)watchable)) {
            throw new IllegalArgumentException("watchable (" + watchable + ") must be a Path " + "associated with the same file system as this watch service");
        }
        return (JimfsPath)watchable;
    }
    
    private boolean isSameFileSystem(final Path path) {
        return ((JimfsFileSystem)path.getFileSystem()).getDefaultView() == this.view;
    }
    
    @VisibleForTesting
    synchronized boolean isPolling() {
        return this.pollingFuture != null;
    }
    
    @Override
    public synchronized void cancelled(final Key key) {
        this.snapshots.remove(key);
        if (this.snapshots.isEmpty()) {
            this.stopPolling();
        }
    }
    
    @Override
    public void close() {
        super.close();
        synchronized (this) {
            for (final Key key : this.snapshots.keySet()) {
                key.cancel();
            }
            this.pollingService.shutdown();
            this.fileSystemState.unregister(this);
        }
    }
    
    private void startPolling() {
        this.pollingFuture = this.pollingService.scheduleAtFixedRate(this.pollingTask, this.interval, this.interval, this.timeUnit);
    }
    
    private void stopPolling() {
        this.pollingFuture.cancel(false);
        this.pollingFuture = null;
    }
    
    private Snapshot takeSnapshot(final JimfsPath path) throws IOException {
        return new Snapshot(this.view.snapshotModifiedTimes(path));
    }
    
    static {
        THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat("com.google.common.jimfs.PollingWatchService-thread-%d").setDaemon(true).build();
    }
    
    private final class Snapshot
    {
        private final ImmutableMap<Name, Long> modifiedTimes;
        
        Snapshot(final Map<Name, Long> modifiedTimes) {
            this.modifiedTimes = ImmutableMap.copyOf((Map<? extends Name, ? extends Long>)modifiedTimes);
        }
        
        boolean postChanges(final Snapshot newState, final Key key) {
            boolean changesPosted = false;
            if (key.subscribesTo(StandardWatchEventKinds.ENTRY_CREATE)) {
                final Set<Name> created = Sets.difference(newState.modifiedTimes.keySet(), this.modifiedTimes.keySet());
                for (final Name name : created) {
                    key.post(new Event<Object>(StandardWatchEventKinds.ENTRY_CREATE, 1, PollingWatchService.this.pathService.createFileName(name)));
                    changesPosted = true;
                }
            }
            if (key.subscribesTo(StandardWatchEventKinds.ENTRY_DELETE)) {
                final Set<Name> deleted = Sets.difference(this.modifiedTimes.keySet(), newState.modifiedTimes.keySet());
                for (final Name name : deleted) {
                    key.post(new Event<Object>(StandardWatchEventKinds.ENTRY_DELETE, 1, PollingWatchService.this.pathService.createFileName(name)));
                    changesPosted = true;
                }
            }
            if (key.subscribesTo(StandardWatchEventKinds.ENTRY_MODIFY)) {
                for (final Map.Entry<Name, Long> entry : this.modifiedTimes.entrySet()) {
                    final Name name = entry.getKey();
                    final Long modifiedTime = entry.getValue();
                    final Long newModifiedTime = newState.modifiedTimes.get(name);
                    if (newModifiedTime != null && !modifiedTime.equals(newModifiedTime)) {
                        key.post(new Event<Object>(StandardWatchEventKinds.ENTRY_MODIFY, 1, PollingWatchService.this.pathService.createFileName(name)));
                        changesPosted = true;
                    }
                }
            }
            return changesPosted;
        }
    }
}
