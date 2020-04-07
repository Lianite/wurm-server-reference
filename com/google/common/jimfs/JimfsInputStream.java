// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import java.io.Closeable;
import com.google.common.primitives.Ints;
import java.io.IOException;
import com.google.common.base.Preconditions;
import com.google.common.annotations.VisibleForTesting;
import javax.annotation.concurrent.GuardedBy;
import java.io.InputStream;

final class JimfsInputStream extends InputStream
{
    @GuardedBy("this")
    @VisibleForTesting
    RegularFile file;
    @GuardedBy("this")
    private long pos;
    @GuardedBy("this")
    private boolean finished;
    private final FileSystemState fileSystemState;
    
    public JimfsInputStream(final RegularFile file, final FileSystemState fileSystemState) {
        this.file = Preconditions.checkNotNull(file);
        (this.fileSystemState = fileSystemState).register(this);
    }
    
    @Override
    public synchronized int read() throws IOException {
        this.checkNotClosed();
        if (this.finished) {
            return -1;
        }
        this.file.readLock().lock();
        try {
            final int b = this.file.read(this.pos++);
            if (b == -1) {
                this.finished = true;
            }
            else {
                this.file.updateAccessTime();
            }
            return b;
        }
        finally {
            this.file.readLock().unlock();
        }
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        return this.readInternal(b, 0, b.length);
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        Preconditions.checkPositionIndexes(off, off + len, b.length);
        return this.readInternal(b, off, len);
    }
    
    private synchronized int readInternal(final byte[] b, final int off, final int len) throws IOException {
        this.checkNotClosed();
        if (this.finished) {
            return -1;
        }
        this.file.readLock().lock();
        try {
            final int read = this.file.read(this.pos, b, off, len);
            if (read == -1) {
                this.finished = true;
            }
            else {
                this.pos += read;
            }
            this.file.updateAccessTime();
            return read;
        }
        finally {
            this.file.readLock().unlock();
        }
    }
    
    @Override
    public long skip(final long n) throws IOException {
        if (n <= 0L) {
            return 0L;
        }
        synchronized (this) {
            this.checkNotClosed();
            if (this.finished) {
                return 0L;
            }
            final int skip = (int)Math.min(Math.max(this.file.size() - this.pos, 0L), n);
            this.pos += skip;
            return skip;
        }
    }
    
    @Override
    public synchronized int available() throws IOException {
        this.checkNotClosed();
        if (this.finished) {
            return 0;
        }
        final long available = Math.max(this.file.size() - this.pos, 0L);
        return Ints.saturatedCast(available);
    }
    
    @GuardedBy("this")
    private void checkNotClosed() throws IOException {
        if (this.file == null) {
            throw new IOException("stream is closed");
        }
    }
    
    @Override
    public synchronized void close() throws IOException {
        if (this.isOpen()) {
            this.fileSystemState.unregister(this);
            this.file.closed();
            this.file = null;
        }
    }
    
    @GuardedBy("this")
    private boolean isOpen() {
        return this.file != null;
    }
}
