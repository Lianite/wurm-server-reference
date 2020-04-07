// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.nio.file.StandardWatchEventKinds;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import com.google.common.base.MoreObjects;
import java.util.Objects;
import com.google.common.base.Preconditions;
import java.nio.file.ClosedWatchServiceException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import java.util.Collection;
import com.google.common.collect.ImmutableList;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.nio.file.WatchEvent;
import java.nio.file.Watchable;
import com.google.common.collect.ImmutableSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.nio.file.WatchKey;
import java.util.concurrent.BlockingQueue;
import java.nio.file.WatchService;

abstract class AbstractWatchService implements WatchService
{
    private final BlockingQueue<WatchKey> queue;
    private final WatchKey poison;
    private final AtomicBoolean open;
    
    AbstractWatchService() {
        this.queue = new LinkedBlockingQueue<WatchKey>();
        this.poison = new Key(this, null, (Iterable<? extends WatchEvent.Kind<?>>)ImmutableSet.of());
        this.open = new AtomicBoolean(true);
    }
    
    public Key register(final Watchable watchable, final Iterable<? extends WatchEvent.Kind<?>> eventTypes) throws IOException {
        this.checkOpen();
        return new Key(this, watchable, eventTypes);
    }
    
    @VisibleForTesting
    public boolean isOpen() {
        return this.open.get();
    }
    
    final void enqueue(final Key key) {
        if (this.isOpen()) {
            this.queue.add(key);
        }
    }
    
    public void cancelled(final Key key) {
    }
    
    @VisibleForTesting
    ImmutableList<WatchKey> queuedKeys() {
        return ImmutableList.copyOf((Collection<? extends WatchKey>)this.queue);
    }
    
    @Nullable
    @Override
    public WatchKey poll() {
        this.checkOpen();
        return this.check(this.queue.poll());
    }
    
    @Nullable
    @Override
    public WatchKey poll(final long timeout, final TimeUnit unit) throws InterruptedException {
        this.checkOpen();
        return this.check(this.queue.poll(timeout, unit));
    }
    
    @Override
    public WatchKey take() throws InterruptedException {
        this.checkOpen();
        return this.check(this.queue.take());
    }
    
    @Nullable
    private WatchKey check(@Nullable final WatchKey key) {
        if (key == this.poison) {
            this.queue.offer(this.poison);
            throw new ClosedWatchServiceException();
        }
        return key;
    }
    
    protected final void checkOpen() {
        if (!this.open.get()) {
            throw new ClosedWatchServiceException();
        }
    }
    
    @Override
    public void close() {
        if (this.open.compareAndSet(true, false)) {
            this.queue.clear();
            this.queue.offer(this.poison);
        }
    }
    
    static final class Event<T> implements WatchEvent<T>
    {
        private final Kind<T> kind;
        private final int count;
        @Nullable
        private final T context;
        
        public Event(final Kind<T> kind, final int count, @Nullable final T context) {
            this.kind = Preconditions.checkNotNull(kind);
            Preconditions.checkArgument(count >= 0, "count (%s) must be non-negative", count);
            this.count = count;
            this.context = context;
        }
        
        @Override
        public Kind<T> kind() {
            return this.kind;
        }
        
        @Override
        public int count() {
            return this.count;
        }
        
        @Nullable
        @Override
        public T context() {
            return this.context;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Event) {
                final Event<?> other = (Event<?>)obj;
                return this.kind().equals(other.kind()) && this.count() == other.count() && Objects.equals(this.context(), other.context());
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(this.kind(), this.count(), this.context());
        }
        
        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this).add("kind", this.kind()).add("count", this.count()).add("context", this.context()).toString();
        }
    }
    
    static final class Key implements WatchKey
    {
        @VisibleForTesting
        static final int MAX_QUEUE_SIZE = 256;
        private final AbstractWatchService watcher;
        private final Watchable watchable;
        private final ImmutableSet<WatchEvent.Kind<?>> subscribedTypes;
        private final AtomicReference<State> state;
        private final AtomicBoolean valid;
        private final AtomicInteger overflow;
        private final BlockingQueue<WatchEvent<?>> events;
        
        private static WatchEvent<Object> overflowEvent(final int count) {
            return new Event<Object>(StandardWatchEventKinds.OVERFLOW, count, null);
        }
        
        public Key(final AbstractWatchService watcher, @Nullable final Watchable watchable, final Iterable<? extends WatchEvent.Kind<?>> subscribedTypes) {
            this.state = new AtomicReference<State>(State.READY);
            this.valid = new AtomicBoolean(true);
            this.overflow = new AtomicInteger();
            this.events = new ArrayBlockingQueue<WatchEvent<?>>(256);
            this.watcher = Preconditions.checkNotNull(watcher);
            this.watchable = watchable;
            this.subscribedTypes = ImmutableSet.copyOf(subscribedTypes);
        }
        
        @VisibleForTesting
        State state() {
            return this.state.get();
        }
        
        public boolean subscribesTo(final WatchEvent.Kind<?> eventType) {
            return this.subscribedTypes.contains(eventType);
        }
        
        public void post(final WatchEvent<?> event) {
            if (!this.events.offer(event)) {
                this.overflow.incrementAndGet();
            }
        }
        
        public void signal() {
            if (this.state.getAndSet(State.SIGNALLED) == State.READY) {
                this.watcher.enqueue(this);
            }
        }
        
        @Override
        public boolean isValid() {
            return this.watcher.isOpen() && this.valid.get();
        }
        
        @Override
        public List<WatchEvent<?>> pollEvents() {
            final List<WatchEvent<?>> result = new ArrayList<WatchEvent<?>>(this.events.size());
            this.events.drainTo(result);
            final int overflowCount = this.overflow.getAndSet(0);
            if (overflowCount != 0) {
                result.add(overflowEvent(overflowCount));
            }
            return Collections.unmodifiableList((List<? extends WatchEvent<?>>)result);
        }
        
        @Override
        public boolean reset() {
            if (this.isValid() && this.state.compareAndSet(State.SIGNALLED, State.READY) && !this.events.isEmpty()) {
                this.signal();
            }
            return this.isValid();
        }
        
        @Override
        public void cancel() {
            this.valid.set(false);
            this.watcher.cancelled(this);
        }
        
        @Override
        public Watchable watchable() {
            return this.watchable;
        }
        
        @VisibleForTesting
        enum State
        {
            READY, 
            SIGNALLED;
        }
    }
}
