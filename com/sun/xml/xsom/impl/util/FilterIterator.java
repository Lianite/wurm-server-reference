// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl.util;

import java.util.Iterator;

public abstract class FilterIterator implements Iterator
{
    private final Iterator core;
    private Object next;
    
    protected FilterIterator(final Iterator core) {
        this.core = core;
    }
    
    protected abstract boolean allows(final Object p0);
    
    public boolean hasNext() {
        while (this.next == null && this.core.hasNext()) {
            final Object o = this.core.next();
            if (this.allows(o)) {
                this.next = o;
            }
        }
        return this.next != null;
    }
    
    public Object next() {
        final Object r = this.next;
        this.next = null;
        return r;
    }
    
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
