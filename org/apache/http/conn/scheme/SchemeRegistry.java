// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.conn.scheme;

import java.util.concurrent.locks.ReentrantLock;
import java.util.Spliterator;
import java.util.NoSuchElementException;
import sun.misc.Contended;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.locks.LockSupport;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.function.ToDoubleFunction;
import java.util.function.IntBinaryOperator;
import java.util.function.ToIntBiFunction;
import java.util.function.LongBinaryOperator;
import java.util.function.ToLongBiFunction;
import java.util.function.DoubleBinaryOperator;
import java.util.function.ToDoubleBiFunction;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Enumeration;
import java.util.function.Function;
import java.util.function.BiFunction;
import java.util.function.BiConsumer;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Set;
import java.util.Iterator;
import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;
import sun.misc.Unsafe;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.concurrent.ConcurrentMap;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpHost;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.annotation.ThreadSafe;

@ThreadSafe
public final class SchemeRegistry
{
    private final ConcurrentHashMap<String, Scheme> registeredSchemes;
    
    public SchemeRegistry() {
        this.registeredSchemes = new ConcurrentHashMap<String, Scheme>();
    }
    
    public final Scheme getScheme(final String name) {
        final Scheme found = this.get(name);
        if (found == null) {
            throw new IllegalStateException("Scheme '" + name + "' not registered.");
        }
        return found;
    }
    
    public final Scheme getScheme(final HttpHost host) {
        if (host == null) {
            throw new IllegalArgumentException("Host must not be null.");
        }
        return this.getScheme(host.getSchemeName());
    }
    
    public final Scheme get(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name must not be null.");
        }
        final Scheme found = this.registeredSchemes.get(name);
        return found;
    }
    
    public final Scheme register(final Scheme sch) {
        if (sch == null) {
            throw new IllegalArgumentException("Scheme must not be null.");
        }
        final Scheme old = this.registeredSchemes.put(sch.getName(), sch);
        return old;
    }
    
    public final Scheme unregister(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name must not be null.");
        }
        final Scheme gone = this.registeredSchemes.remove(name);
        return gone;
    }
    
    public final List<String> getSchemeNames() {
        return new ArrayList<String>(this.registeredSchemes.keySet());
    }
    
    public void setItems(final Map<String, Scheme> map) {
        if (map == null) {
            return;
        }
        this.registeredSchemes.clear();
        this.registeredSchemes.putAll(map);
    }
}
