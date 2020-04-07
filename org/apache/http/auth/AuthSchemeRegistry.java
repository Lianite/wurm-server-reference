// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.auth;

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
import org.apache.http.params.HttpParams;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.annotation.ThreadSafe;

@ThreadSafe
public final class AuthSchemeRegistry
{
    private final ConcurrentHashMap<String, AuthSchemeFactory> registeredSchemes;
    
    public AuthSchemeRegistry() {
        this.registeredSchemes = new ConcurrentHashMap<String, AuthSchemeFactory>();
    }
    
    public void register(final String name, final AuthSchemeFactory factory) {
        if (name == null) {
            throw new IllegalArgumentException("Name may not be null");
        }
        if (factory == null) {
            throw new IllegalArgumentException("Authentication scheme factory may not be null");
        }
        this.registeredSchemes.put(name.toLowerCase(Locale.ENGLISH), factory);
    }
    
    public void unregister(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name may not be null");
        }
        this.registeredSchemes.remove(name.toLowerCase(Locale.ENGLISH));
    }
    
    public AuthScheme getAuthScheme(final String name, final HttpParams params) throws IllegalStateException {
        if (name == null) {
            throw new IllegalArgumentException("Name may not be null");
        }
        final AuthSchemeFactory factory = this.registeredSchemes.get(name.toLowerCase(Locale.ENGLISH));
        if (factory != null) {
            return factory.newInstance(params);
        }
        throw new IllegalStateException("Unsupported authentication scheme: " + name);
    }
    
    public List<String> getSchemeNames() {
        return new ArrayList<String>(this.registeredSchemes.keySet());
    }
    
    public void setItems(final Map<String, AuthSchemeFactory> map) {
        if (map == null) {
            return;
        }
        this.registeredSchemes.clear();
        this.registeredSchemes.putAll(map);
    }
}
