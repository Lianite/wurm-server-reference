// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import java.nio.channels.WritableByteChannel;
import com.google.common.primitives.UnsignedBytes;
import java.nio.channels.ReadableByteChannel;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.io.IOException;
import com.google.common.annotations.VisibleForTesting;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import com.google.common.base.Preconditions;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReadWriteLock;

final class RegularFile extends File
{
    private final ReadWriteLock lock;
    private final HeapDisk disk;
    private byte[][] blocks;
    private int blockCount;
    private long size;
    private int openCount;
    private boolean deleted;
    
    public static RegularFile create(final int id, final HeapDisk disk) {
        return new RegularFile(id, disk, new byte[32][], 0, 0L);
    }
    
    RegularFile(final int id, final HeapDisk disk, final byte[][] blocks, final int blockCount, final long size) {
        super(id);
        this.lock = new ReentrantReadWriteLock();
        this.openCount = 0;
        this.deleted = false;
        this.disk = Preconditions.checkNotNull(disk);
        this.blocks = Preconditions.checkNotNull(blocks);
        this.blockCount = blockCount;
        Preconditions.checkArgument(size >= 0L);
        this.size = size;
    }
    
    public Lock readLock() {
        return this.lock.readLock();
    }
    
    public Lock writeLock() {
        return this.lock.writeLock();
    }
    
    private void expandIfNecessary(final int minBlockCount) {
        if (minBlockCount > this.blocks.length) {
            this.blocks = Arrays.copyOf(this.blocks, Util.nextPowerOf2(minBlockCount));
        }
    }
    
    int blockCount() {
        return this.blockCount;
    }
    
    void copyBlocksTo(final RegularFile target, final int count) {
        final int start = this.blockCount - count;
        final int targetEnd = target.blockCount + count;
        target.expandIfNecessary(targetEnd);
        System.arraycopy(this.blocks, start, target.blocks, target.blockCount, count);
        target.blockCount = targetEnd;
    }
    
    void transferBlocksTo(final RegularFile target, final int count) {
        this.copyBlocksTo(target, count);
        this.truncateBlocks(this.blockCount - count);
    }
    
    void truncateBlocks(final int count) {
        Util.clear(this.blocks, count, this.blockCount - count);
        this.blockCount = count;
    }
    
    void addBlock(final byte[] block) {
        this.expandIfNecessary(this.blockCount + 1);
        this.blocks[this.blockCount++] = block;
    }
    
    @VisibleForTesting
    byte[] getBlock(final int index) {
        return this.blocks[index];
    }
    
    public long sizeWithoutLocking() {
        return this.size;
    }
    
    @Override
    public long size() {
        this.readLock().lock();
        try {
            return this.size;
        }
        finally {
            this.readLock().unlock();
        }
    }
    
    @Override
    RegularFile copyWithoutContent(final int id) {
        final byte[][] copyBlocks = new byte[Math.max(this.blockCount * 2, 32)][];
        return new RegularFile(id, this.disk, copyBlocks, 0, this.size);
    }
    
    @Override
    void copyContentTo(final File file) throws IOException {
        final RegularFile copy = (RegularFile)file;
        this.disk.allocate(copy, this.blockCount);
        for (int i = 0; i < this.blockCount; ++i) {
            final byte[] block = this.blocks[i];
            final byte[] copyBlock = copy.blocks[i];
            System.arraycopy(block, 0, copyBlock, 0, block.length);
        }
    }
    
    @Override
    ReadWriteLock contentLock() {
        return this.lock;
    }
    
    public synchronized void opened() {
        ++this.openCount;
    }
    
    public synchronized void closed() {
        final int openCount = this.openCount - 1;
        this.openCount = openCount;
        if (openCount == 0 && this.deleted) {
            this.deleteContents();
        }
    }
    
    public synchronized void deleted() {
        if (this.links() == 0) {
            this.deleted = true;
            if (this.openCount == 0) {
                this.deleteContents();
            }
        }
    }
    
    private void deleteContents() {
        this.disk.free(this);
        this.size = 0L;
    }
    
    public boolean truncate(final long size) {
        if (size >= this.size) {
            return false;
        }
        final long lastPosition = size - 1L;
        this.size = size;
        final int newBlockCount = this.blockIndex(lastPosition) + 1;
        final int blocksToRemove = this.blockCount - newBlockCount;
        if (blocksToRemove > 0) {
            this.disk.free(this, blocksToRemove);
        }
        return true;
    }
    
    private void prepareForWrite(final long pos, final long len) throws IOException {
        final long end = pos + len;
        final int lastBlockIndex = this.blockCount - 1;
        final int endBlockIndex = this.blockIndex(end - 1L);
        if (endBlockIndex > lastBlockIndex) {
            final int additionalBlocksNeeded = endBlockIndex - lastBlockIndex;
            this.disk.allocate(this, additionalBlocksNeeded);
        }
        if (pos > this.size) {
            long remaining = pos - this.size;
            int blockIndex = this.blockIndex(this.size);
            byte[] block = this.blocks[blockIndex];
            final int off = this.offsetInBlock(this.size);
            for (remaining -= zero(block, off, this.length(off, remaining)); remaining > 0L; remaining -= zero(block, 0, this.length(remaining))) {
                block = this.blocks[++blockIndex];
            }
            this.size = pos;
        }
    }
    
    public int write(final long pos, final byte b) throws IOException {
        this.prepareForWrite(pos, 1L);
        final byte[] block = this.blocks[this.blockIndex(pos)];
        final int off = this.offsetInBlock(pos);
        block[off] = b;
        if (pos >= this.size) {
            this.size = pos + 1L;
        }
        return 1;
    }
    
    public int write(final long pos, final byte[] b, int off, final int len) throws IOException {
        this.prepareForWrite(pos, len);
        if (len == 0) {
            return 0;
        }
        int blockIndex = this.blockIndex(pos);
        byte[] block = this.blocks[blockIndex];
        final int offInBlock = this.offsetInBlock(pos);
        int written;
        int remaining;
        for (written = put(block, offInBlock, b, off, this.length(offInBlock, len)), remaining = len - written, off += written; remaining > 0; remaining -= written, off += written) {
            block = this.blocks[++blockIndex];
            written = put(block, 0, b, off, this.length(remaining));
        }
        final long endPos = pos + len;
        if (endPos > this.size) {
            this.size = endPos;
        }
        return len;
    }
    
    public int write(final long pos, final ByteBuffer buf) throws IOException {
        final int len = buf.remaining();
        this.prepareForWrite(pos, len);
        if (len == 0) {
            return 0;
        }
        int blockIndex = this.blockIndex(pos);
        byte[] block = this.blocks[blockIndex];
        final int off = this.offsetInBlock(pos);
        put(block, off, buf);
        while (buf.hasRemaining()) {
            block = this.blocks[++blockIndex];
            put(block, 0, buf);
        }
        final long endPos = pos + len;
        if (endPos > this.size) {
            this.size = endPos;
        }
        return len;
    }
    
    public long write(long pos, final Iterable<ByteBuffer> bufs) throws IOException {
        final long start = pos;
        for (final ByteBuffer buf : bufs) {
            pos += this.write(pos, buf);
        }
        return pos - start;
    }
    
    public long transferFrom(final ReadableByteChannel src, final long pos, final long count) throws IOException {
        this.prepareForWrite(pos, 0L);
        if (count == 0L) {
            return 0L;
        }
        long remaining = count;
        int blockIndex = this.blockIndex(pos);
        byte[] block = this.blockForWrite(blockIndex);
        final int off = this.offsetInBlock(pos);
        ByteBuffer buf = ByteBuffer.wrap(block, off, this.length(off, remaining));
        long currentPos = pos;
        int read = 0;
        while (buf.hasRemaining()) {
            read = src.read(buf);
            if (read == -1) {
                break;
            }
            currentPos += read;
            remaining -= read;
        }
        if (currentPos > this.size) {
            this.size = currentPos;
        }
        Label_0229: {
            if (read != -1) {
                while (remaining > 0L) {
                    block = this.blockForWrite(++blockIndex);
                    buf = ByteBuffer.wrap(block, 0, this.length(remaining));
                    while (buf.hasRemaining()) {
                        read = src.read(buf);
                        if (read == -1) {
                            break Label_0229;
                        }
                        currentPos += read;
                        remaining -= read;
                    }
                    if (currentPos > this.size) {
                        this.size = currentPos;
                    }
                }
            }
        }
        if (currentPos > this.size) {
            this.size = currentPos;
        }
        return currentPos - pos;
    }
    
    public int read(final long pos) {
        if (pos >= this.size) {
            return -1;
        }
        final byte[] block = this.blocks[this.blockIndex(pos)];
        final int off = this.offsetInBlock(pos);
        return UnsignedBytes.toInt(block[off]);
    }
    
    public int read(final long pos, final byte[] b, int off, final int len) {
        final int bytesToRead = (int)this.bytesToRead(pos, len);
        if (bytesToRead > 0) {
            int remaining = bytesToRead;
            int blockIndex = this.blockIndex(pos);
            byte[] block = this.blocks[blockIndex];
            final int offsetInBlock = this.offsetInBlock(pos);
            int read;
            int index;
            for (read = get(block, offsetInBlock, b, off, this.length(offsetInBlock, remaining)), remaining -= read, off += read; remaining > 0; remaining -= read, off += read) {
                index = ++blockIndex;
                block = this.blocks[index];
                read = get(block, 0, b, off, this.length(remaining));
            }
        }
        return bytesToRead;
    }
    
    public int read(final long pos, final ByteBuffer buf) {
        final int bytesToRead = (int)this.bytesToRead(pos, buf.remaining());
        if (bytesToRead > 0) {
            int remaining = bytesToRead;
            int blockIndex = this.blockIndex(pos);
            byte[] block = this.blocks[blockIndex];
            final int off = this.offsetInBlock(pos);
            for (remaining -= get(block, off, buf, this.length(off, remaining)); remaining > 0; remaining -= get(block, 0, buf, this.length(remaining))) {
                final int index = ++blockIndex;
                block = this.blocks[index];
            }
        }
        return bytesToRead;
    }
    
    public long read(long pos, final Iterable<ByteBuffer> bufs) {
        if (pos >= this.size()) {
            return -1L;
        }
        final long start = pos;
        for (final ByteBuffer buf : bufs) {
            final int read = this.read(pos, buf);
            if (read == -1) {
                break;
            }
            pos += read;
        }
        return pos - start;
    }
    
    public long transferTo(final long pos, final long count, final WritableByteChannel dest) throws IOException {
        final long bytesToRead = this.bytesToRead(pos, count);
        if (bytesToRead > 0L) {
            long remaining = bytesToRead;
            int blockIndex = this.blockIndex(pos);
            byte[] block = this.blocks[blockIndex];
            final int off = this.offsetInBlock(pos);
            ByteBuffer buf = ByteBuffer.wrap(block, off, this.length(off, remaining));
            while (buf.hasRemaining()) {
                remaining -= dest.write(buf);
            }
            buf.clear();
            while (remaining > 0L) {
                final int index = ++blockIndex;
                block = this.blocks[index];
                buf = ByteBuffer.wrap(block, 0, this.length(remaining));
                while (buf.hasRemaining()) {
                    remaining -= dest.write(buf);
                }
                buf.clear();
            }
        }
        return Math.max(bytesToRead, 0L);
    }
    
    private byte[] blockForWrite(final int index) throws IOException {
        if (index >= this.blockCount) {
            final int additionalBlocksNeeded = index - this.blockCount + 1;
            this.disk.allocate(this, additionalBlocksNeeded);
        }
        return this.blocks[index];
    }
    
    private int blockIndex(final long position) {
        return (int)(position / this.disk.blockSize());
    }
    
    private int offsetInBlock(final long position) {
        return (int)(position % this.disk.blockSize());
    }
    
    private int length(final long max) {
        return (int)Math.min(this.disk.blockSize(), max);
    }
    
    private int length(final int off, final long max) {
        return (int)Math.min(this.disk.blockSize() - off, max);
    }
    
    private long bytesToRead(final long pos, final long max) {
        final long available = this.size - pos;
        if (available <= 0L) {
            return -1L;
        }
        return Math.min(available, max);
    }
    
    private static int zero(final byte[] block, final int offset, final int len) {
        Util.zero(block, offset, len);
        return len;
    }
    
    private static int put(final byte[] block, final int offset, final byte[] b, final int off, final int len) {
        System.arraycopy(b, off, block, offset, len);
        return len;
    }
    
    private static int put(final byte[] block, final int offset, final ByteBuffer buf) {
        final int len = Math.min(block.length - offset, buf.remaining());
        buf.get(block, offset, len);
        return len;
    }
    
    private static int get(final byte[] block, final int offset, final byte[] b, final int off, final int len) {
        System.arraycopy(block, offset, b, off, len);
        return len;
    }
    
    private static int get(final byte[] block, final int offset, final ByteBuffer buf, final int len) {
        buf.put(block, offset, len);
        return len;
    }
}
