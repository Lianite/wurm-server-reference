// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.util;

import java.util.ListIterator;
import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import java.util.AbstractList;

public class SubList extends AbstractList
{
    private final List l;
    private final int offset;
    private int size;
    
    public SubList(final List list, final int fromIndex, final int toIndex) {
        if (fromIndex < 0) {
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        }
        if (toIndex > list.size()) {
            throw new IndexOutOfBoundsException("toIndex = " + toIndex);
        }
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
        }
        this.l = list;
        this.offset = fromIndex;
        this.size = toIndex - fromIndex;
    }
    
    public Object set(final int index, final Object element) {
        this.rangeCheck(index);
        return this.l.set(index + this.offset, element);
    }
    
    public Object get(final int index) {
        this.rangeCheck(index);
        return this.l.get(index + this.offset);
    }
    
    public int size() {
        return this.size;
    }
    
    public void add(final int index, final Object element) {
        if (index < 0 || index > this.size) {
            throw new IndexOutOfBoundsException();
        }
        this.l.add(index + this.offset, element);
        ++this.size;
        ++this.modCount;
    }
    
    public Object remove(final int index) {
        this.rangeCheck(index);
        final Object result = this.l.remove(index + this.offset);
        --this.size;
        ++this.modCount;
        return result;
    }
    
    public boolean addAll(final Collection c) {
        return this.addAll(this.size, c);
    }
    
    public boolean addAll(final int index, final Collection c) {
        if (index < 0 || index > this.size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.size);
        }
        final int cSize = c.size();
        if (cSize == 0) {
            return false;
        }
        this.l.addAll(this.offset + index, c);
        this.size += cSize;
        ++this.modCount;
        return true;
    }
    
    public Iterator iterator() {
        return this.listIterator();
    }
    
    public ListIterator listIterator(final int index) {
        if (index < 0 || index > this.size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.size);
        }
        return (ListIterator)new SubList$1(this, index);
    }
    
    public List subList(final int fromIndex, final int toIndex) {
        return (List)new SubList((List)this, fromIndex, toIndex);
    }
    
    private void rangeCheck(final int index) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException("Index: " + index + ",Size: " + this.size);
        }
    }
}
