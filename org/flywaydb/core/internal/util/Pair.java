// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util;

public class Pair<L, R>
{
    private L left;
    private R right;
    
    public static <L, R> Pair<L, R> of(final L left, final R right) {
        final Pair<L, R> pair = new Pair<L, R>();
        pair.left = left;
        pair.right = right;
        return pair;
    }
    
    public L getLeft() {
        return this.left;
    }
    
    public R getRight() {
        return this.right;
    }
}
