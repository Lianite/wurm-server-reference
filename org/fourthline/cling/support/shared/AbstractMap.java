// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.shared;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Collection;
import java.util.Set;
import java.util.Map;

public abstract class AbstractMap<K, V> implements Map<K, V>
{
    Set<K> keySet;
    Collection<V> valuesCollection;
    
    @Override
    public void clear() {
        this.entrySet().clear();
    }
    
    @Override
    public boolean containsKey(final Object key) {
        final Iterator<Entry<K, V>> it = this.entrySet().iterator();
        if (key != null) {
            while (it.hasNext()) {
                if (key.equals(it.next().getKey())) {
                    return true;
                }
            }
        }
        else {
            while (it.hasNext()) {
                if (it.next().getKey() == null) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean containsValue(final Object value) {
        final Iterator<Entry<K, V>> it = this.entrySet().iterator();
        if (value != null) {
            while (it.hasNext()) {
                if (value.equals(it.next().getValue())) {
                    return true;
                }
            }
        }
        else {
            while (it.hasNext()) {
                if (it.next().getValue() == null) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public abstract Set<Entry<K, V>> entrySet();
    
    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Map)) {
            return false;
        }
        final Map<?, ?> map = (Map<?, ?>)object;
        if (this.size() != map.size()) {
            return false;
        }
        try {
            for (final Entry<K, V> entry : this.entrySet()) {
                final K key = entry.getKey();
                final V mine = entry.getValue();
                final Object theirs = map.get(key);
                if (mine == null) {
                    if (theirs != null || !map.containsKey(key)) {
                        return false;
                    }
                    continue;
                }
                else {
                    if (!mine.equals(theirs)) {
                        return false;
                    }
                    continue;
                }
            }
        }
        catch (NullPointerException ignored) {
            return false;
        }
        catch (ClassCastException ignored2) {
            return false;
        }
        return true;
    }
    
    @Override
    public V get(final Object key) {
        final Iterator<Entry<K, V>> it = this.entrySet().iterator();
        if (key != null) {
            while (it.hasNext()) {
                final Entry<K, V> entry = it.next();
                if (key.equals(entry.getKey())) {
                    return entry.getValue();
                }
            }
        }
        else {
            while (it.hasNext()) {
                final Entry<K, V> entry = it.next();
                if (entry.getKey() == null) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }
    
    @Override
    public int hashCode() {
        int result = 0;
        final Iterator<Entry<K, V>> it = this.entrySet().iterator();
        while (it.hasNext()) {
            result += it.next().hashCode();
        }
        return result;
    }
    
    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }
    
    @Override
    public Set<K> keySet() {
        if (this.keySet == null) {
            this.keySet = new AbstractSet<K>() {
                @Override
                public boolean contains(final Object object) {
                    return AbstractMap.this.containsKey(object);
                }
                
                @Override
                public int size() {
                    return AbstractMap.this.size();
                }
                
                @Override
                public Iterator<K> iterator() {
                    return new Iterator<K>() {
                        Iterator<Entry<K, V>> setIterator = AbstractMap.this.entrySet().iterator();
                        
                        @Override
                        public boolean hasNext() {
                            return this.setIterator.hasNext();
                        }
                        
                        @Override
                        public K next() {
                            return this.setIterator.next().getKey();
                        }
                        
                        @Override
                        public void remove() {
                            this.setIterator.remove();
                        }
                    };
                }
            };
        }
        return this.keySet;
    }
    
    @Override
    public V put(final K key, final V value) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends V> map) {
        for (final Entry<? extends K, ? extends V> entry : map.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }
    
    @Override
    public V remove(final Object key) {
        final Iterator<Entry<K, V>> it = this.entrySet().iterator();
        if (key != null) {
            while (it.hasNext()) {
                final Entry<K, V> entry = it.next();
                if (key.equals(entry.getKey())) {
                    it.remove();
                    return entry.getValue();
                }
            }
        }
        else {
            while (it.hasNext()) {
                final Entry<K, V> entry = it.next();
                if (entry.getKey() == null) {
                    it.remove();
                    return entry.getValue();
                }
            }
        }
        return null;
    }
    
    @Override
    public int size() {
        return this.entrySet().size();
    }
    
    @Override
    public String toString() {
        if (this.isEmpty()) {
            return "{}";
        }
        final StringBuilder buffer = new StringBuilder(this.size() * 28);
        buffer.append('{');
        final Iterator<Entry<K, V>> it = this.entrySet().iterator();
        while (it.hasNext()) {
            final Entry<K, V> entry = it.next();
            final Object key = entry.getKey();
            if (key != this) {
                buffer.append(key);
            }
            else {
                buffer.append("(this Map)");
            }
            buffer.append('=');
            final Object value = entry.getValue();
            if (value != this) {
                buffer.append(value);
            }
            else {
                buffer.append("(this Map)");
            }
            if (it.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append('}');
        return buffer.toString();
    }
    
    @Override
    public Collection<V> values() {
        if (this.valuesCollection == null) {
            this.valuesCollection = new AbstractCollection<V>() {
                @Override
                public int size() {
                    return AbstractMap.this.size();
                }
                
                @Override
                public boolean contains(final Object object) {
                    return AbstractMap.this.containsValue(object);
                }
                
                @Override
                public Iterator<V> iterator() {
                    return new Iterator<V>() {
                        Iterator<Entry<K, V>> setIterator = AbstractMap.this.entrySet().iterator();
                        
                        @Override
                        public boolean hasNext() {
                            return this.setIterator.hasNext();
                        }
                        
                        @Override
                        public V next() {
                            return this.setIterator.next().getValue();
                        }
                        
                        @Override
                        public void remove() {
                            this.setIterator.remove();
                        }
                    };
                }
            };
        }
        return this.valuesCollection;
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        final AbstractMap<K, V> result = (AbstractMap<K, V>)super.clone();
        result.keySet = null;
        result.valuesCollection = null;
        return result;
    }
    
    public static class SimpleImmutableEntry<K, V> implements Entry<K, V>, Serializable
    {
        private static final long serialVersionUID = 7138329143949025153L;
        private final K key;
        private final V value;
        
        public SimpleImmutableEntry(final K theKey, final V theValue) {
            this.key = theKey;
            this.value = theValue;
        }
        
        public SimpleImmutableEntry(final Entry<? extends K, ? extends V> copyFrom) {
            this.key = (K)copyFrom.getKey();
            this.value = (V)copyFrom.getValue();
        }
        
        @Override
        public K getKey() {
            return this.key;
        }
        
        @Override
        public V getValue() {
            return this.value;
        }
        
        @Override
        public V setValue(final V object) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean equals(final Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof Entry) {
                final Entry<?, ?> entry = (Entry<?, ?>)object;
                if (this.key == null) {
                    if (entry.getKey() != null) {
                        return false;
                    }
                }
                else if (!this.key.equals(entry.getKey())) {
                    return false;
                }
                if ((this.value != null) ? this.value.equals(entry.getValue()) : (entry.getValue() == null)) {
                    return true;
                }
                return false;
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return ((this.key == null) ? 0 : this.key.hashCode()) ^ ((this.value == null) ? 0 : this.value.hashCode());
        }
        
        @Override
        public String toString() {
            return this.key + "=" + this.value;
        }
    }
    
    public static class SimpleEntry<K, V> implements Entry<K, V>, Serializable
    {
        private static final long serialVersionUID = -8499721149061103585L;
        private final K key;
        private V value;
        
        public SimpleEntry(final K theKey, final V theValue) {
            this.key = theKey;
            this.value = theValue;
        }
        
        public SimpleEntry(final Entry<? extends K, ? extends V> copyFrom) {
            this.key = (K)copyFrom.getKey();
            this.value = (V)copyFrom.getValue();
        }
        
        @Override
        public K getKey() {
            return this.key;
        }
        
        @Override
        public V getValue() {
            return this.value;
        }
        
        @Override
        public V setValue(final V object) {
            final V result = this.value;
            this.value = object;
            return result;
        }
        
        @Override
        public boolean equals(final Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof Entry) {
                final Entry<?, ?> entry = (Entry<?, ?>)object;
                if (this.key == null) {
                    if (entry.getKey() != null) {
                        return false;
                    }
                }
                else if (!this.key.equals(entry.getKey())) {
                    return false;
                }
                if ((this.value != null) ? this.value.equals(entry.getValue()) : (entry.getValue() == null)) {
                    return true;
                }
                return false;
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return ((this.key == null) ? 0 : this.key.hashCode()) ^ ((this.value == null) ? 0 : this.value.hashCode());
        }
        
        @Override
        public String toString() {
            return this.key + "=" + this.value;
        }
    }
}
