// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.digested;

import java.util.Iterator;

public abstract class DContainerPattern extends DPattern implements Iterable<DPattern>
{
    private DPattern head;
    private DPattern tail;
    
    public DPattern firstChild() {
        return this.head;
    }
    
    public DPattern lastChild() {
        return this.tail;
    }
    
    public int countChildren() {
        int i = 0;
        for (DPattern p = this.firstChild(); p != null; p = p.next) {
            ++i;
        }
        return i;
    }
    
    public Iterator<DPattern> iterator() {
        return new Iterator<DPattern>() {
            DPattern next = DContainerPattern.this.head;
            
            public boolean hasNext() {
                return this.next != null;
            }
            
            public DPattern next() {
                final DPattern r = this.next;
                this.next = this.next.next;
                return r;
            }
            
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    void add(final DPattern child) {
        if (this.tail == null) {
            final DPattern dPattern = null;
            child.next = dPattern;
            child.prev = dPattern;
            this.tail = child;
            this.head = child;
        }
        else {
            child.prev = this.tail;
            this.tail.next = child;
            child.next = null;
            this.tail = child;
        }
    }
}
