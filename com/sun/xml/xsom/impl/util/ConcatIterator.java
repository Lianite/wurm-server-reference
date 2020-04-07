// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl.util;

import java.util.Iterator;

public class ConcatIterator implements Iterator
{
    private Iterator lhs;
    private Iterator rhs;
    
    public ConcatIterator(final Iterator _lhs, final Iterator _rhs) {
        this.lhs = _lhs;
        this.rhs = _rhs;
    }
    
    public boolean hasNext() {
        if (this.lhs != null) {
            if (this.lhs.hasNext()) {
                return true;
            }
            this.lhs = null;
        }
        return this.rhs.hasNext();
    }
    
    public Object next() {
        if (this.lhs != null) {
            return this.lhs.next();
        }
        return this.rhs.next();
    }
    
    public void remove() {
        if (this.lhs != null) {
            this.lhs.remove();
        }
        else {
            this.rhs.remove();
        }
    }
}
