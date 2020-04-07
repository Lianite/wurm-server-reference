// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.schemagen;

import java.util.Map;
import java.util.TreeMap;

final class MultiMap<K extends Comparable<K>, V> extends TreeMap<K, V>
{
    private final V many;
    
    public MultiMap(final V many) {
        this.many = many;
    }
    
    public V put(final K key, final V value) {
        final V old = super.put(key, value);
        if (old != null && !old.equals(value)) {
            super.put(key, this.many);
        }
        return old;
    }
    
    public void putAll(final Map<? extends K, ? extends V> map) {
        throw new UnsupportedOperationException();
    }
}
