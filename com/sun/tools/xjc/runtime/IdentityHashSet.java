// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.runtime;

final class IdentityHashSet
{
    private Object[] table;
    private int count;
    private int threshold;
    private static final float loadFactor = 0.3f;
    private static final int initialCapacity = 191;
    
    public IdentityHashSet() {
        this.table = new Object[191];
        this.threshold = 57;
    }
    
    public boolean contains(final Object key) {
        final Object[] tab = this.table;
        int index = (System.identityHashCode(key) & Integer.MAX_VALUE) % tab.length;
        while (true) {
            final Object e = tab[index];
            if (e == null) {
                return false;
            }
            if (e == key) {
                return true;
            }
            index = (index + 1) % tab.length;
        }
    }
    
    private void rehash() {
        final int oldCapacity = this.table.length;
        final Object[] oldMap = this.table;
        final int newCapacity = oldCapacity * 2 + 1;
        final Object[] newMap = new Object[newCapacity];
        int i = oldCapacity;
        while (i-- > 0) {
            if (oldMap[i] != null) {
                int index;
                for (index = (System.identityHashCode(oldMap[i]) & Integer.MAX_VALUE) % newMap.length; newMap[index] != null; index = (index + 1) % newMap.length) {}
                newMap[index] = oldMap[i];
            }
        }
        this.threshold = (int)(newCapacity * 0.3f);
        this.table = newMap;
    }
    
    public boolean add(final Object newObj) {
        if (this.count >= this.threshold) {
            this.rehash();
        }
        Object[] tab;
        int index;
        Object existing;
        for (tab = this.table, index = (System.identityHashCode(newObj) & Integer.MAX_VALUE) % tab.length; (existing = tab[index]) != null; index = (index + 1) % tab.length) {
            if (existing == newObj) {
                return false;
            }
        }
        tab[index] = newObj;
        ++this.count;
        return true;
    }
}
