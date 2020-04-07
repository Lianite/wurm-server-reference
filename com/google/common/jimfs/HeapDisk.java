// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import java.io.IOException;
import com.google.common.base.Preconditions;
import com.google.common.math.LongMath;
import java.math.RoundingMode;
import com.google.common.annotations.VisibleForTesting;

final class HeapDisk
{
    private final int blockSize;
    private final int maxBlockCount;
    private final int maxCachedBlockCount;
    @VisibleForTesting
    final RegularFile blockCache;
    private int allocatedBlockCount;
    
    public HeapDisk(final Configuration config) {
        this.blockSize = config.blockSize;
        this.maxBlockCount = toBlockCount(config.maxSize, this.blockSize);
        this.maxCachedBlockCount = ((config.maxCacheSize == -1L) ? this.maxBlockCount : toBlockCount(config.maxCacheSize, this.blockSize));
        this.blockCache = this.createBlockCache(this.maxCachedBlockCount);
    }
    
    private static int toBlockCount(final long size, final int blockSize) {
        return (int)LongMath.divide(size, blockSize, RoundingMode.FLOOR);
    }
    
    public HeapDisk(final int blockSize, final int maxBlockCount, final int maxCachedBlockCount) {
        Preconditions.checkArgument(blockSize > 0, "blockSize (%s) must be positive", blockSize);
        Preconditions.checkArgument(maxBlockCount > 0, "maxBlockCount (%s) must be positive", maxBlockCount);
        Preconditions.checkArgument(maxCachedBlockCount >= 0, "maxCachedBlockCount must be non-negative", maxCachedBlockCount);
        this.blockSize = blockSize;
        this.maxBlockCount = maxBlockCount;
        this.maxCachedBlockCount = maxCachedBlockCount;
        this.blockCache = this.createBlockCache(maxCachedBlockCount);
    }
    
    private RegularFile createBlockCache(final int maxCachedBlockCount) {
        return new RegularFile(-1, this, new byte[Math.min(maxCachedBlockCount, 8192)][], 0, 0L);
    }
    
    public int blockSize() {
        return this.blockSize;
    }
    
    public synchronized long getTotalSpace() {
        return this.maxBlockCount * this.blockSize;
    }
    
    public synchronized long getUnallocatedSpace() {
        return (this.maxBlockCount - this.allocatedBlockCount) * this.blockSize;
    }
    
    public synchronized void allocate(final RegularFile file, final int count) throws IOException {
        final int newAllocatedBlockCount = this.allocatedBlockCount + count;
        if (newAllocatedBlockCount > this.maxBlockCount) {
            throw new IOException("out of disk space");
        }
        final int newBlocksNeeded = Math.max(count - this.blockCache.blockCount(), 0);
        for (int i = 0; i < newBlocksNeeded; ++i) {
            file.addBlock(new byte[this.blockSize]);
        }
        if (newBlocksNeeded != count) {
            this.blockCache.transferBlocksTo(file, count - newBlocksNeeded);
        }
        this.allocatedBlockCount = newAllocatedBlockCount;
    }
    
    public void free(final RegularFile file) {
        this.free(file, file.blockCount());
    }
    
    public synchronized void free(final RegularFile file, final int count) {
        final int remainingCacheSpace = this.maxCachedBlockCount - this.blockCache.blockCount();
        if (remainingCacheSpace > 0) {
            file.copyBlocksTo(this.blockCache, Math.min(count, remainingCacheSpace));
        }
        file.truncateBlocks(file.blockCount() - count);
        this.allocatedBlockCount -= count;
    }
}
