// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl.scd;

import java.util.HashSet;
import java.util.Set;
import java.util.NoSuchElementException;
import java.util.Collections;
import java.util.Iterator;

public class Iterators
{
    private static final Iterator EMPTY;
    
    public static <T> Iterator<T> empty() {
        return (Iterator<T>)Iterators.EMPTY;
    }
    
    public static <T> Iterator<T> singleton(final T value) {
        return new Singleton<T>(value);
    }
    
    static {
        EMPTY = Collections.EMPTY_LIST.iterator();
    }
    
    abstract static class ReadOnly<T> implements Iterator<T>
    {
        public final void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    static final class Singleton<T> extends ReadOnly<T>
    {
        private T next;
        
        Singleton(final T next) {
            this.next = next;
        }
        
        public boolean hasNext() {
            return this.next != null;
        }
        
        public T next() {
            final T r = this.next;
            this.next = null;
            return r;
        }
    }
    
    public abstract static class Adapter<T, U> extends ReadOnly<T>
    {
        private final Iterator<? extends U> core;
        
        public Adapter(final Iterator<? extends U> core) {
            this.core = core;
        }
        
        public boolean hasNext() {
            return this.core.hasNext();
        }
        
        public T next() {
            return this.filter(this.core.next());
        }
        
        protected abstract T filter(final U p0);
    }
    
    public abstract static class Map<T, U> extends ReadOnly<T>
    {
        private final Iterator<? extends U> core;
        private Iterator<? extends T> current;
        
        protected Map(final Iterator<? extends U> core) {
            this.core = core;
        }
        
        public boolean hasNext() {
            while (this.current == null || !this.current.hasNext()) {
                if (!this.core.hasNext()) {
                    return false;
                }
                this.current = this.apply(this.core.next());
            }
            return true;
        }
        
        public T next() {
            return (T)this.current.next();
        }
        
        protected abstract Iterator<? extends T> apply(final U p0);
    }
    
    public abstract static class Filter<T> extends ReadOnly<T>
    {
        private final Iterator<? extends T> core;
        private T next;
        
        protected Filter(final Iterator<? extends T> core) {
            this.core = core;
        }
        
        protected abstract boolean matches(final T p0);
        
        public boolean hasNext() {
            while (this.core.hasNext() && this.next == null) {
                this.next = (T)this.core.next();
                if (!this.matches(this.next)) {
                    this.next = null;
                }
            }
            return this.next != null;
        }
        
        public T next() {
            if (this.next == null) {
                throw new NoSuchElementException();
            }
            final T r = this.next;
            this.next = null;
            return r;
        }
    }
    
    static final class Unique<T> extends Filter<T>
    {
        private Set<T> values;
        
        public Unique(final Iterator<? extends T> core) {
            super(core);
            this.values = new HashSet<T>();
        }
        
        protected boolean matches(final T value) {
            return this.values.add(value);
        }
    }
    
    public static final class Union<T> extends ReadOnly<T>
    {
        private final Iterator<? extends T> first;
        private final Iterator<? extends T> second;
        
        public Union(final Iterator<? extends T> first, final Iterator<? extends T> second) {
            this.first = first;
            this.second = second;
        }
        
        public boolean hasNext() {
            return this.first.hasNext() || this.second.hasNext();
        }
        
        public T next() {
            if (this.first.hasNext()) {
                return (T)this.first.next();
            }
            return (T)this.second.next();
        }
    }
    
    public static final class Array<T> extends ReadOnly<T>
    {
        private final T[] items;
        private int index;
        
        public Array(final T[] items) {
            this.index = 0;
            this.items = items;
        }
        
        public boolean hasNext() {
            return this.index < this.items.length;
        }
        
        public T next() {
            return (T)this.items[this.index++];
        }
    }
}
