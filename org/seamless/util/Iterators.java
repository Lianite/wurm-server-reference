// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.util;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Iterator;

public class Iterators
{
    public static class Empty<E> implements Iterator<E>
    {
        public boolean hasNext() {
            return false;
        }
        
        public E next() {
            throw new NoSuchElementException();
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    public static class Singular<E> implements Iterator<E>
    {
        protected final E element;
        protected int current;
        
        public Singular(final E element) {
            this.element = element;
        }
        
        public boolean hasNext() {
            return this.current == 0;
        }
        
        public E next() {
            ++this.current;
            return this.element;
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    public abstract static class Synchronized<E> implements Iterator<E>
    {
        final Iterator<E> wrapped;
        int nextIndex;
        boolean removedCurrent;
        
        public Synchronized(final Collection<E> collection) {
            this.nextIndex = 0;
            this.removedCurrent = false;
            this.wrapped = new CopyOnWriteArrayList<E>((Collection<? extends E>)collection).iterator();
        }
        
        public boolean hasNext() {
            return this.wrapped.hasNext();
        }
        
        public E next() {
            this.removedCurrent = false;
            ++this.nextIndex;
            return this.wrapped.next();
        }
        
        public void remove() {
            if (this.nextIndex == 0) {
                throw new IllegalStateException("Call next() first");
            }
            if (this.removedCurrent) {
                throw new IllegalStateException("Already removed current, call next()");
            }
            this.synchronizedRemove(this.nextIndex - 1);
            this.removedCurrent = true;
        }
        
        protected abstract void synchronizedRemove(final int p0);
    }
}
