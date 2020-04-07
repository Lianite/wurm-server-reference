// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import java.nio.file.spi.FileSystemProvider;
import java.util.Set;
import java.io.Closeable;
import java.util.concurrent.Executors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.IOException;
import java.nio.file.WatchService;
import java.nio.file.PathMatcher;
import java.nio.file.FileStore;
import com.google.common.collect.ImmutableSet;
import com.google.common.annotations.VisibleForTesting;
import java.util.Iterator;
import java.util.Comparator;
import java.nio.file.Path;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.base.Preconditions;
import javax.annotation.Nullable;
import java.util.concurrent.ExecutorService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.net.URI;
import java.nio.file.FileSystem;

final class JimfsFileSystem extends FileSystem
{
    private final JimfsFileSystemProvider provider;
    private final URI uri;
    private final JimfsFileStore fileStore;
    private final PathService pathService;
    private final UserPrincipalLookupService userLookupService;
    private final FileSystemView defaultView;
    private final WatchServiceConfiguration watchServiceConfig;
    @Nullable
    private ExecutorService defaultThreadPool;
    
    JimfsFileSystem(final JimfsFileSystemProvider provider, final URI uri, final JimfsFileStore fileStore, final PathService pathService, final FileSystemView defaultView, final WatchServiceConfiguration watchServiceConfig) {
        this.userLookupService = new UserLookupService(true);
        this.provider = Preconditions.checkNotNull(provider);
        this.uri = Preconditions.checkNotNull(uri);
        this.fileStore = Preconditions.checkNotNull(fileStore);
        this.pathService = Preconditions.checkNotNull(pathService);
        this.defaultView = Preconditions.checkNotNull(defaultView);
        this.watchServiceConfig = Preconditions.checkNotNull(watchServiceConfig);
    }
    
    @Override
    public JimfsFileSystemProvider provider() {
        return this.provider;
    }
    
    public URI getUri() {
        return this.uri;
    }
    
    public FileSystemView getDefaultView() {
        return this.defaultView;
    }
    
    @Override
    public String getSeparator() {
        return this.pathService.getSeparator();
    }
    
    @Override
    public ImmutableSortedSet<Path> getRootDirectories() {
        final ImmutableSortedSet.Builder<JimfsPath> builder = ImmutableSortedSet.orderedBy((Comparator<JimfsPath>)this.pathService);
        for (final Name name : this.fileStore.getRootDirectoryNames()) {
            builder.add(this.pathService.createRoot(name));
        }
        return (ImmutableSortedSet<Path>)builder.build();
    }
    
    public JimfsPath getWorkingDirectory() {
        return this.defaultView.getWorkingDirectoryPath();
    }
    
    @VisibleForTesting
    PathService getPathService() {
        return this.pathService;
    }
    
    public JimfsFileStore getFileStore() {
        return this.fileStore;
    }
    
    @Override
    public ImmutableSet<FileStore> getFileStores() {
        this.fileStore.state().checkOpen();
        return (ImmutableSet<FileStore>)ImmutableSet.of(this.fileStore);
    }
    
    @Override
    public ImmutableSet<String> supportedFileAttributeViews() {
        return this.fileStore.supportedFileAttributeViews();
    }
    
    @Override
    public JimfsPath getPath(final String first, final String... more) {
        this.fileStore.state().checkOpen();
        return this.pathService.parsePath(first, more);
    }
    
    public URI toUri(final JimfsPath path) {
        this.fileStore.state().checkOpen();
        return this.pathService.toUri(this.uri, path.toAbsolutePath());
    }
    
    public JimfsPath toPath(final URI uri) {
        this.fileStore.state().checkOpen();
        return this.pathService.fromUri(uri);
    }
    
    @Override
    public PathMatcher getPathMatcher(final String syntaxAndPattern) {
        this.fileStore.state().checkOpen();
        return this.pathService.createPathMatcher(syntaxAndPattern);
    }
    
    @Override
    public UserPrincipalLookupService getUserPrincipalLookupService() {
        this.fileStore.state().checkOpen();
        return this.userLookupService;
    }
    
    @Override
    public WatchService newWatchService() throws IOException {
        return this.watchServiceConfig.newWatchService(this.defaultView, this.pathService);
    }
    
    public synchronized ExecutorService getDefaultThreadPool() {
        if (this.defaultThreadPool == null) {
            this.defaultThreadPool = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("JimfsFileSystem-" + this.uri.getHost() + "-defaultThreadPool-%s").build());
            this.fileStore.state().register(new Closeable() {
                @Override
                public void close() {
                    JimfsFileSystem.this.defaultThreadPool.shutdown();
                }
            });
        }
        return this.defaultThreadPool;
    }
    
    @Override
    public boolean isReadOnly() {
        return false;
    }
    
    @Override
    public boolean isOpen() {
        return this.fileStore.state().isOpen();
    }
    
    @Override
    public void close() throws IOException {
        this.fileStore.state().close();
    }
}
