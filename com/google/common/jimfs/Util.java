// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import java.util.Iterator;
import com.google.common.collect.ImmutableCollection;
import com.google.common.base.Preconditions;

final class Util
{
    private static final int C1 = -862048943;
    private static final int C2 = 461845907;
    private static final int ARRAY_LEN = 8192;
    private static final byte[] ZERO_ARRAY;
    private static final byte[][] NULL_ARRAY;
    
    public static int nextPowerOf2(final int n) {
        if (n == 0) {
            return 1;
        }
        final int b = Integer.highestOneBit(n);
        return (b == n) ? n : (b << 1);
    }
    
    static void checkNotNegative(final long n, final String description) {
        Preconditions.checkArgument(n >= 0L, "%s must not be negative: %s", description, n);
    }
    
    static void checkNoneNull(final Iterable<?> objects) {
        if (!(objects instanceof ImmutableCollection)) {
            for (final Object o : objects) {
                Preconditions.checkNotNull(o);
            }
        }
    }
    
    static int smearHash(final int hashCode) {
        return 461845907 * Integer.rotateLeft(hashCode * -862048943, 15);
    }
    
    static void zero(final byte[] bytes, int off, final int len) {
        int remaining;
        for (remaining = len; remaining > 8192; remaining -= 8192) {
            System.arraycopy(Util.ZERO_ARRAY, 0, bytes, off, 8192);
            off += 8192;
        }
        System.arraycopy(Util.ZERO_ARRAY, 0, bytes, off, remaining);
    }
    
    static void clear(final byte[][] blocks, int off, final int len) {
        int remaining;
        for (remaining = len; remaining > 8192; remaining -= 8192) {
            System.arraycopy(Util.NULL_ARRAY, 0, blocks, off, 8192);
            off += 8192;
        }
        System.arraycopy(Util.NULL_ARRAY, 0, blocks, off, remaining);
    }
    
    static {
        ZERO_ARRAY = new byte[8192];
        NULL_ARRAY = new byte[8192][];
    }
}
