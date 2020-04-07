// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.util;

import java.util.Iterator;

public abstract class FilterIterator implements Iterator
{
    private final Iterator core;
    private boolean ready;
    private boolean noMore;
    private Object obj;
    
    protected abstract boolean test(final Object p0);
    
    public FilterIterator(final Iterator _core) {
        this.ready = false;
        this.noMore = false;
        this.obj = null;
        this.core = _core;
    }
    
    public final Object next() {
        if (!this.hasNext()) {
            throw new IllegalStateException("no more object");
        }
        this.ready = false;
        return this.obj;
    }
    
    public final boolean hasNext() {
        if (this.noMore) {
            return false;
        }
        if (this.ready) {
            return true;
        }
        while (this.core.hasNext()) {
            final Object o = this.core.next();
            if (this.test(o)) {
                this.obj = o;
                return this.ready = true;
            }
        }
        this.noMore = true;
        return false;
    }
    
    public final void remove() {
        throw new UnsupportedOperationException();
    }
}
