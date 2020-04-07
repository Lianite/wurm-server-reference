// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import java.util.Iterator;
import com.google.common.base.Throwables;
import java.io.IOException;
import java.nio.file.ClosedFileSystemException;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Set;
import java.io.Closeable;

final class FileSystemState implements Closeable
{
    private final Set<Closeable> resources;
    private final Runnable onClose;
    private final AtomicBoolean open;
    private final AtomicInteger registering;
    
    FileSystemState(final Runnable onClose) {
        this.resources = Sets.newConcurrentHashSet();
        this.open = new AtomicBoolean(true);
        this.registering = new AtomicInteger();
        this.onClose = Preconditions.checkNotNull(onClose);
    }
    
    public boolean isOpen() {
        return this.open.get();
    }
    
    public void checkOpen() {
        if (!this.open.get()) {
            throw new ClosedFileSystemException();
        }
    }
    
    public <C extends Closeable> C register(final C resource) {
        this.checkOpen();
        this.registering.incrementAndGet();
        try {
            this.checkOpen();
            this.resources.add(resource);
            return resource;
        }
        finally {
            this.registering.decrementAndGet();
        }
    }
    
    public void unregister(final Closeable resource) {
        this.resources.remove(resource);
    }
    
    @Override
    public void close() throws IOException {
        if (this.open.compareAndSet(true, false)) {
            this.onClose.run();
            Throwable thrown = null;
            do {
                for (final Closeable resource : this.resources) {
                    try {
                        resource.close();
                    }
                    catch (Throwable e) {
                        if (thrown == null) {
                            thrown = e;
                        }
                        else {
                            thrown.addSuppressed(e);
                        }
                    }
                    finally {
                        this.resources.remove(resource);
                    }
                }
            } while (this.registering.get() > 0 || !this.resources.isEmpty());
            Throwables.propagateIfPossible(thrown, IOException.class);
        }
    }
}
