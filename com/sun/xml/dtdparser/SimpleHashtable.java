// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.dtdparser;

import java.util.Enumeration;

final class SimpleHashtable implements Enumeration
{
    private Entry[] table;
    private Entry current;
    private int currentBucket;
    private int count;
    private int threshold;
    private static final float loadFactor = 0.75f;
    
    public SimpleHashtable(int initialCapacity) {
        this.current = null;
        this.currentBucket = 0;
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
        if (initialCapacity == 0) {
            initialCapacity = 1;
        }
        this.table = new Entry[initialCapacity];
        this.threshold = (int)(initialCapacity * 0.75f);
    }
    
    public SimpleHashtable() {
        this(11);
    }
    
    public void clear() {
        this.count = 0;
        this.currentBucket = 0;
        this.current = null;
        for (int i = 0; i < this.table.length; ++i) {
            this.table[i] = null;
        }
    }
    
    public int size() {
        return this.count;
    }
    
    public Enumeration keys() {
        this.currentBucket = 0;
        this.current = null;
        return this;
    }
    
    public boolean hasMoreElements() {
        if (this.current != null) {
            return true;
        }
        while (this.currentBucket < this.table.length) {
            this.current = this.table[this.currentBucket++];
            if (this.current != null) {
                return true;
            }
        }
        return false;
    }
    
    public Object nextElement() {
        if (this.current == null) {
            throw new IllegalStateException();
        }
        final Object retval = this.current.key;
        this.current = this.current.next;
        return retval;
    }
    
    public Object get(final String key) {
        final Entry[] tab = this.table;
        final int hash = key.hashCode();
        final int index = (hash & Integer.MAX_VALUE) % tab.length;
        for (Entry e = tab[index]; e != null; e = e.next) {
            if (e.hash == hash && e.key == key) {
                return e.value;
            }
        }
        return null;
    }
    
    public Object getNonInterned(final String key) {
        final Entry[] tab = this.table;
        final int hash = key.hashCode();
        final int index = (hash & Integer.MAX_VALUE) % tab.length;
        for (Entry e = tab[index]; e != null; e = e.next) {
            if (e.hash == hash && e.key.equals(key)) {
                return e.value;
            }
        }
        return null;
    }
    
    private void rehash() {
        final int oldCapacity = this.table.length;
        final Entry[] oldMap = this.table;
        final int newCapacity = oldCapacity * 2 + 1;
        final Entry[] newMap = new Entry[newCapacity];
        this.threshold = (int)(newCapacity * 0.75f);
        this.table = newMap;
        int i = oldCapacity;
        while (i-- > 0) {
            Entry e;
            int index;
            for (Entry old = oldMap[i]; old != null; old = old.next, index = (e.hash & Integer.MAX_VALUE) % newCapacity, e.next = newMap[index], newMap[index] = e) {
                e = old;
            }
        }
    }
    
    public Object put(final Object key, final Object value) {
        if (value == null) {
            throw new NullPointerException();
        }
        Entry[] tab = this.table;
        final int hash = key.hashCode();
        int index = (hash & Integer.MAX_VALUE) % tab.length;
        for (Entry e = tab[index]; e != null; e = e.next) {
            if (e.hash == hash && e.key == key) {
                final Object old = e.value;
                e.value = value;
                return old;
            }
        }
        if (this.count >= this.threshold) {
            this.rehash();
            tab = this.table;
            index = (hash & Integer.MAX_VALUE) % tab.length;
        }
        Entry e = new Entry(hash, key, value, tab[index]);
        tab[index] = e;
        ++this.count;
        return null;
    }
    
    private static class Entry
    {
        int hash;
        Object key;
        Object value;
        Entry next;
        
        protected Entry(final int hash, final Object key, final Object value, final Entry next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
}
