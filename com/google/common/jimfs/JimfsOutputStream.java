// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import java.io.Closeable;
import java.io.IOException;
import com.google.common.base.Preconditions;
import com.google.common.annotations.VisibleForTesting;
import javax.annotation.concurrent.GuardedBy;
import java.io.OutputStream;

final class JimfsOutputStream extends OutputStream
{
    @GuardedBy("this")
    @VisibleForTesting
    RegularFile file;
    @GuardedBy("this")
    private long pos;
    private final boolean append;
    private final FileSystemState fileSystemState;
    
    JimfsOutputStream(final RegularFile file, final boolean append, final FileSystemState fileSystemState) {
        this.file = Preconditions.checkNotNull(file);
        this.append = append;
        (this.fileSystemState = fileSystemState).register(this);
    }
    
    @Override
    public synchronized void write(final int b) throws IOException {
        this.checkNotClosed();
        this.file.writeLock().lock();
        try {
            if (this.append) {
                this.pos = this.file.sizeWithoutLocking();
            }
            this.file.write(this.pos++, (byte)b);
            this.file.updateModifiedTime();
        }
        finally {
            this.file.writeLock().unlock();
        }
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
        this.writeInternal(b, 0, b.length);
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        Preconditions.checkPositionIndexes(off, off + len, b.length);
        this.writeInternal(b, off, len);
    }
    
    private synchronized void writeInternal(final byte[] b, final int off, final int len) throws IOException {
        this.checkNotClosed();
        this.file.writeLock().lock();
        try {
            if (this.append) {
                this.pos = this.file.sizeWithoutLocking();
            }
            this.pos += this.file.write(this.pos, b, off, len);
            this.file.updateModifiedTime();
        }
        finally {
            this.file.writeLock().unlock();
        }
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
