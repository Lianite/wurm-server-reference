// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.shared.util;

import java.util.Collection;
import java.util.Set;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.HashMap;

public final class SynchedHashMap<K, V> extends HashMap<K, V>
{
    private static final long serialVersionUID = -7481512012392422544L;
    private final ReentrantReadWriteLock rwl;
    private final Lock readLock;
    private final Lock writeLock;
    
    public SynchedHashMap(final Map<? extends K, ? extends V> m) {
        super(m);
        this.rwl = new ReentrantReadWriteLock();
        this.readLock = this.rwl.readLock();
        this.writeLock = this.rwl.writeLock();
    }
    
    public SynchedHashMap() {
        this.rwl = new ReentrantReadWriteLock();
        this.readLock = this.rwl.readLock();
        this.writeLock = this.rwl.writeLock();
    }
    
    public SynchedHashMap(final int initialCapacity) {
        super(initialCapacity);
        this.rwl = new ReentrantReadWriteLock();
        this.readLock = this.rwl.readLock();
        this.writeLock = this.rwl.writeLock();
    }
    
    public SynchedHashMap(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
        this.rwl = new ReentrantReadWriteLock();
        this.readLock = this.rwl.readLock();
        this.writeLock = this.rwl.writeLock();
    }
    
    @Override
    public void clear() {
        this.writeLock.lock();
        try {
            super.clear();
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @Override
    public boolean containsKey(final Object key) {
        this.readLock.lock();
        try {
            return super.containsKey(key);
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public boolean containsValue(final Object value) {
        this.readLock.lock();
        try {
            return super.containsValue(value);
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        this.readLock.lock();
        try {
            return super.entrySet();
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public V get(final Object key) {
        this.readLock.lock();
        try {
            return super.get(key);
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public Set<K> keySet() {
        this.readLock.lock();
        try {
            return super.keySet();
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public V put(final K key, final V value) {
        this.writeLock.lock();
        try {
            return super.put(key, value);
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends V> map) {
        this.writeLock.lock();
        try {
            super.putAll(map);
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @Override
    public V remove(final Object key) {
        this.writeLock.lock();
        try {
            return super.remove(key);
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @Override
    public Collection<V> values() {
        this.readLock.lock();
        try {
            return super.values();
        }
        finally {
            this.readLock.unlock();
        }
    }
}
