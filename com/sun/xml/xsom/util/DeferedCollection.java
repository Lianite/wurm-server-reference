// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.util;

import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Collection;

public class DeferedCollection<T> implements Collection<T>
{
    private final Iterator<T> result;
    private final List<T> archive;
    
    public DeferedCollection(final Iterator<T> result) {
        this.archive = new ArrayList<T>();
        this.result = result;
    }
    
    public boolean isEmpty() {
        if (this.archive.isEmpty()) {
            this.fetch();
        }
        return this.archive.isEmpty();
    }
    
    public int size() {
        this.fetchAll();
        return this.archive.size();
    }
    
    public boolean contains(final Object o) {
        if (this.archive.contains(o)) {
            return true;
        }
        while (this.result.hasNext()) {
            final T value = this.result.next();
            this.archive.add(value);
            if (value.equals(o)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean containsAll(final Collection<?> c) {
        for (final Object o : c) {
            if (!this.contains(o)) {
                return false;
            }
        }
        return true;
    }
    
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            int idx = 0;
            
            public boolean hasNext() {
                return this.idx < DeferedCollection.this.archive.size() || DeferedCollection.this.result.hasNext();
            }
            
            public T next() {
                if (this.idx == DeferedCollection.this.archive.size()) {
                    DeferedCollection.this.fetch();
                }
                if (this.idx == DeferedCollection.this.archive.size()) {
                    throw new NoSuchElementException();
                }
                return DeferedCollection.this.archive.get(this.idx++);
            }
            
            public void remove() {
            }
        };
    }
    
    public Object[] toArray() {
        this.fetchAll();
        return this.archive.toArray();
    }
    
    public <T> T[] toArray(final T[] a) {
        this.fetchAll();
        return this.archive.toArray(a);
    }
    
    private void fetchAll() {
        while (this.result.hasNext()) {
            this.archive.add(this.result.next());
        }
    }
    
    private void fetch() {
        if (this.result.hasNext()) {
            this.archive.add(this.result.next());
        }
    }
    
    public boolean add(final T o) {
        throw new UnsupportedOperationException();
    }
    
    public boolean remove(final Object o) {
        throw new UnsupportedOperationException();
    }
    
    public boolean addAll(final Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }
    
    public boolean removeAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }
    
    public boolean retainAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }
    
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
