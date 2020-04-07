// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import java.util.concurrent.atomic.AtomicBoolean;
import java.nio.channels.SeekableByteChannel;
import java.util.Iterator;
import java.io.Closeable;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.FileLockInterruptionException;
import java.nio.channels.FileLock;
import java.nio.MappedByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.List;
import java.util.Arrays;
import java.io.IOException;
import com.google.common.base.Preconditions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.NonReadableChannelException;
import java.nio.channels.AsynchronousFileChannel;
import java.util.concurrent.ExecutorService;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.nio.file.OpenOption;
import javax.annotation.concurrent.GuardedBy;
import java.util.Set;
import java.nio.channels.FileChannel;

final class JimfsFileChannel extends FileChannel
{
    @GuardedBy("blockingThreads")
    private final Set<Thread> blockingThreads;
    private final RegularFile file;
    private final FileSystemState fileSystemState;
    private final boolean read;
    private final boolean write;
    private final boolean append;
    @GuardedBy("this")
    private long position;
    
    public JimfsFileChannel(final RegularFile file, final Set<OpenOption> options, final FileSystemState fileSystemState) {
        this.blockingThreads = new HashSet<Thread>();
        this.file = file;
        this.fileSystemState = fileSystemState;
        this.read = options.contains(StandardOpenOption.READ);
        this.write = options.contains(StandardOpenOption.WRITE);
        this.append = options.contains(StandardOpenOption.APPEND);
        fileSystemState.register(this);
    }
    
    public AsynchronousFileChannel asAsynchronousFileChannel(final ExecutorService executor) {
        return new JimfsAsynchronousFileChannel(this, executor);
    }
    
    void checkReadable() {
        if (!this.read) {
            throw new NonReadableChannelException();
        }
    }
    
    void checkWritable() {
        if (!this.write) {
            throw new NonWritableChannelException();
        }
    }
    
    void checkOpen() throws ClosedChannelException {
        if (!this.isOpen()) {
            throw new ClosedChannelException();
        }
    }
    
    private boolean beginBlocking() {
        this.begin();
        synchronized (this.blockingThreads) {
            if (this.isOpen()) {
                this.blockingThreads.add(Thread.currentThread());
                return true;
            }
            return false;
        }
    }
    
    private void endBlocking(final boolean completed) throws AsynchronousCloseException {
        synchronized (this.blockingThreads) {
            this.blockingThreads.remove(Thread.currentThread());
        }
        this.end(completed);
    }
    
    @Override
    public int read(final ByteBuffer dst) throws IOException {
        Preconditions.checkNotNull(dst);
        this.checkOpen();
        this.checkReadable();
        int read = 0;
        synchronized (this) {
            boolean completed = false;
            try {
                if (!this.beginBlocking()) {
                    return 0;
                }
                this.file.readLock().lockInterruptibly();
                try {
                    read = this.file.read(this.position, dst);
                    if (read != -1) {
                        this.position += read;
                    }
                    this.file.updateAccessTime();
                    completed = true;
                }
                finally {
                    this.file.readLock().unlock();
                }
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            finally {
                this.endBlocking(completed);
            }
        }
        return read;
    }
    
    @Override
    public long read(final ByteBuffer[] dsts, final int offset, final int length) throws IOException {
        Preconditions.checkPositionIndexes(offset, offset + length, dsts.length);
        final List<ByteBuffer> buffers = Arrays.asList(dsts).subList(offset, offset + length);
        Util.checkNoneNull(buffers);
        this.checkOpen();
        this.checkReadable();
        long read = 0L;
        synchronized (this) {
            boolean completed = false;
            try {
                if (!this.beginBlocking()) {
                    return 0L;
                }
                this.file.readLock().lockInterruptibly();
                try {
                    read = this.file.read(this.position, buffers);
                    if (read != -1L) {
                        this.position += read;
                    }
                    this.file.updateAccessTime();
                    completed = true;
                }
                finally {
                    this.file.readLock().unlock();
                }
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            finally {
                this.endBlocking(completed);
            }
        }
        return read;
    }
    
    @Override
    public int write(final ByteBuffer src) throws IOException {
        Preconditions.checkNotNull(src);
        this.checkOpen();
        this.checkWritable();
        int written = 0;
        synchronized (this) {
            boolean completed = false;
            try {
                if (!this.beginBlocking()) {
                    return 0;
                }
                this.file.writeLock().lockInterruptibly();
                try {
                    if (this.append) {
                        this.position = this.file.size();
                    }
                    written = this.file.write(this.position, src);
                    this.position += written;
                    this.file.updateModifiedTime();
                    completed = true;
                }
                finally {
                    this.file.writeLock().unlock();
                }
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            finally {
                this.endBlocking(completed);
            }
        }
        return written;
    }
    
    @Override
    public long write(final ByteBuffer[] srcs, final int offset, final int length) throws IOException {
        Preconditions.checkPositionIndexes(offset, offset + length, srcs.length);
        final List<ByteBuffer> buffers = Arrays.asList(srcs).subList(offset, offset + length);
        Util.checkNoneNull(buffers);
        this.checkOpen();
        this.checkWritable();
        long written = 0L;
        synchronized (this) {
            boolean completed = false;
            try {
                if (!this.beginBlocking()) {
                    return 0L;
                }
                this.file.writeLock().lockInterruptibly();
                try {
                    if (this.append) {
                        this.position = this.file.size();
                    }
                    written = this.file.write(this.position, buffers);
                    this.position += written;
                    this.file.updateModifiedTime();
                    completed = true;
                }
                finally {
                    this.file.writeLock().unlock();
                }
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            finally {
                this.endBlocking(completed);
            }
        }
        return written;
    }
    
    @Override
    public long position() throws IOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aload_0         /* this */
        //     1: invokevirtual   com/google/common/jimfs/JimfsFileChannel.checkOpen:()V
        //     4: aload_0         /* this */
        //     5: dup            
        //     6: astore_3       
        //     7: monitorenter   
        //     8: iconst_0       
        //     9: istore          completed
        //    11: aload_0         /* this */
        //    12: invokevirtual   com/google/common/jimfs/JimfsFileChannel.begin:()V
        //    15: aload_0         /* this */
        //    16: invokevirtual   com/google/common/jimfs/JimfsFileChannel.isOpen:()Z
        //    19: ifne            36
        //    22: lconst_0       
        //    23: lstore          5
        //    25: aload_0         /* this */
        //    26: iload           completed
        //    28: invokevirtual   com/google/common/jimfs/JimfsFileChannel.end:(Z)V
        //    31: aload_3        
        //    32: monitorexit    
        //    33: lload           5
        //    35: lreturn        
        //    36: aload_0         /* this */
        //    37: getfield        com/google/common/jimfs/JimfsFileChannel.position:J
        //    40: lstore_1        /* pos */
        //    41: iconst_1       
        //    42: istore          completed
        //    44: aload_0         /* this */
        //    45: iload           completed
        //    47: invokevirtual   com/google/common/jimfs/JimfsFileChannel.end:(Z)V
        //    50: goto            64
        //    53: astore          7
        //    55: aload_0         /* this */
        //    56: iload           completed
        //    58: invokevirtual   com/google/common/jimfs/JimfsFileChannel.end:(Z)V
        //    61: aload           7
        //    63: athrow         
        //    64: aload_3        
        //    65: monitorexit    
        //    66: goto            76
        //    69: astore          8
        //    71: aload_3        
        //    72: monitorexit    
        //    73: aload           8
        //    75: athrow         
        //    76: lload_1         /* pos */
        //    77: lreturn        
        //    Exceptions:
        //  throws java.io.IOException
        //    LocalVariableTable:
        //  Start  Length  Slot  Name       Signature
        //  -----  ------  ----  ---------  ------------------------------------------
        //  11     53      4     completed  Z
        //  0      78      0     this       Lcom/google/common/jimfs/JimfsFileChannel;
        //  41     37      1     pos        J
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  11     25     53     64     Any
        //  36     44     53     64     Any
        //  53     55     53     64     Any
        //  8      33     69     76     Any
        //  36     66     69     76     Any
        //  69     73     69     76     Any
        // 
        // The error that occurred was:
        // 
        // java.lang.ArrayIndexOutOfBoundsException
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    @Override
    public FileChannel position(final long newPosition) throws IOException {
        Util.checkNotNegative(newPosition, "newPosition");
        this.checkOpen();
        synchronized (this) {
            boolean completed = false;
            try {
                this.begin();
                if (!this.isOpen()) {
                    return this;
                }
                this.position = newPosition;
                completed = true;
            }
            finally {
                this.end(completed);
            }
        }
        return this;
    }
    
    @Override
    public long size() throws IOException {
        this.checkOpen();
        long size = 0L;
        boolean completed = false;
        try {
            if (!this.beginBlocking()) {
                return 0L;
            }
            this.file.readLock().lockInterruptibly();
            try {
                size = this.file.sizeWithoutLocking();
                completed = true;
            }
            finally {
                this.file.readLock().unlock();
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        finally {
            this.endBlocking(completed);
        }
        return size;
    }
    
    @Override
    public FileChannel truncate(final long size) throws IOException {
        Util.checkNotNegative(size, "size");
        this.checkOpen();
        this.checkWritable();
        synchronized (this) {
            boolean completed = false;
            try {
                if (!this.beginBlocking()) {
                    return this;
                }
                this.file.writeLock().lockInterruptibly();
                try {
                    this.file.truncate(size);
                    if (this.position > size) {
                        this.position = size;
                    }
                    this.file.updateModifiedTime();
                    completed = true;
                }
                finally {
                    this.file.writeLock().unlock();
                }
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            finally {
                this.endBlocking(completed);
            }
        }
        return this;
    }
    
    @Override
    public void force(final boolean metaData) throws IOException {
        this.checkOpen();
        boolean completed = false;
        try {
            this.begin();
            completed = true;
        }
        finally {
            this.end(completed);
        }
    }
    
    @Override
    public long transferTo(final long position, final long count, final WritableByteChannel target) throws IOException {
        Preconditions.checkNotNull(target);
        Util.checkNotNegative(position, "position");
        Util.checkNotNegative(count, "count");
        this.checkOpen();
        this.checkReadable();
        long transferred = 0L;
        boolean completed = false;
        try {
            if (!this.beginBlocking()) {
                return 0L;
            }
            this.file.readLock().lockInterruptibly();
            try {
                transferred = this.file.transferTo(position, count, target);
                this.file.updateAccessTime();
                completed = true;
            }
            finally {
                this.file.readLock().unlock();
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        finally {
            this.endBlocking(completed);
        }
        return transferred;
    }
    
    @Override
    public long transferFrom(final ReadableByteChannel src, long position, final long count) throws IOException {
        Preconditions.checkNotNull(src);
        Util.checkNotNegative(position, "position");
        Util.checkNotNegative(count, "count");
        this.checkOpen();
        this.checkWritable();
        long transferred = 0L;
        if (this.append) {
            synchronized (this) {
                boolean completed = false;
                try {
                    if (!this.beginBlocking()) {
                        return 0L;
                    }
                    this.file.writeLock().lockInterruptibly();
                    try {
                        position = this.file.sizeWithoutLocking();
                        transferred = this.file.transferFrom(src, position, count);
                        this.position = position + transferred;
                        this.file.updateModifiedTime();
                        completed = true;
                    }
                    finally {
                        this.file.writeLock().unlock();
                    }
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                finally {
                    this.endBlocking(completed);
                }
            }
        }
        else {
            boolean completed2 = false;
            try {
                if (!this.beginBlocking()) {
                    return 0L;
                }
                this.file.writeLock().lockInterruptibly();
                try {
                    transferred = this.file.transferFrom(src, position, count);
                    this.file.updateModifiedTime();
                    completed2 = true;
                }
                finally {
                    this.file.writeLock().unlock();
                }
            }
            catch (InterruptedException e2) {
                Thread.currentThread().interrupt();
            }
            finally {
                this.endBlocking(completed2);
            }
        }
        return transferred;
    }
    
    @Override
    public int read(final ByteBuffer dst, final long position) throws IOException {
        Preconditions.checkNotNull(dst);
        Util.checkNotNegative(position, "position");
        this.checkOpen();
        this.checkReadable();
        int read = 0;
        boolean completed = false;
        try {
            if (!this.beginBlocking()) {
                return 0;
            }
            this.file.readLock().lockInterruptibly();
            try {
                read = this.file.read(position, dst);
                this.file.updateAccessTime();
                completed = true;
            }
            finally {
                this.file.readLock().unlock();
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        finally {
            this.endBlocking(completed);
        }
        return read;
    }
    
    @Override
    public int write(final ByteBuffer src, long position) throws IOException {
        Preconditions.checkNotNull(src);
        Util.checkNotNegative(position, "position");
        this.checkOpen();
        this.checkWritable();
        int written = 0;
        if (this.append) {
            synchronized (this) {
                boolean completed = false;
                try {
                    if (!this.beginBlocking()) {
                        return 0;
                    }
                    this.file.writeLock().lockInterruptibly();
                    try {
                        position = this.file.sizeWithoutLocking();
                        written = this.file.write(position, src);
                        this.position = position + written;
                        this.file.updateModifiedTime();
                        completed = true;
                    }
                    finally {
                        this.file.writeLock().unlock();
                    }
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                finally {
                    this.endBlocking(completed);
                }
            }
        }
        else {
            boolean completed2 = false;
            try {
                if (!this.beginBlocking()) {
                    return 0;
                }
                this.file.writeLock().lockInterruptibly();
                try {
                    written = this.file.write(position, src);
                    this.file.updateModifiedTime();
                    completed2 = true;
                }
                finally {
                    this.file.writeLock().unlock();
                }
            }
            catch (InterruptedException e2) {
                Thread.currentThread().interrupt();
            }
            finally {
                this.endBlocking(completed2);
            }
        }
        return written;
    }
    
    @Override
    public MappedByteBuffer map(final MapMode mode, final long position, final long size) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public FileLock lock(final long position, final long size, final boolean shared) throws IOException {
        this.checkLockArguments(position, size, shared);
        boolean completed = false;
        try {
            this.begin();
            completed = true;
            return new FakeFileLock(this, position, size, shared);
        }
        finally {
            try {
                this.end(completed);
            }
            catch (ClosedByInterruptException e) {
                throw new FileLockInterruptionException();
            }
        }
    }
    
    @Override
    public FileLock tryLock(final long position, final long size, final boolean shared) throws IOException {
        this.checkLockArguments(position, size, shared);
        return new FakeFileLock(this, position, size, shared);
    }
    
    private void checkLockArguments(final long position, final long size, final boolean shared) throws IOException {
        Util.checkNotNegative(position, "position");
        Util.checkNotNegative(size, "size");
        this.checkOpen();
        if (shared) {
            this.checkReadable();
        }
        else {
            this.checkWritable();
        }
    }
    
    @Override
    protected void implCloseChannel() {
        try {
            synchronized (this.blockingThreads) {
                for (final Thread thread : this.blockingThreads) {
                    thread.interrupt();
                }
            }
        }
        finally {
            this.fileSystemState.unregister(this);
            this.file.closed();
        }
    }
    
    static final class FakeFileLock extends FileLock
    {
        private final AtomicBoolean valid;
        
        public FakeFileLock(final FileChannel channel, final long position, final long size, final boolean shared) {
            super(channel, position, size, shared);
            this.valid = new AtomicBoolean(true);
        }
        
        public FakeFileLock(final AsynchronousFileChannel channel, final long position, final long size, final boolean shared) {
            super(channel, position, size, shared);
            this.valid = new AtomicBoolean(true);
        }
        
        @Override
        public boolean isValid() {
            return this.valid.get();
        }
        
        @Override
        public void release() throws IOException {
            this.valid.set(false);
        }
    }
}
