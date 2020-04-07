// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public final class Ring
{
    private final Map<Class, Object> components;
    private static final ThreadLocal<Ring> instances;
    
    private Ring() {
        this.components = new HashMap<Class, Object>();
    }
    
    public static <T> void add(final Class<T> clazz, final T instance) {
        assert !get().components.containsKey(clazz);
        get().components.put(clazz, instance);
    }
    
    public static <T> void add(final T o) {
        add(o.getClass(), o);
    }
    
    public static <T> T get(final Class<T> key) {
        T t = (T)get().components.get(key);
        if (t == null) {
            try {
                final Constructor<T> c = key.getDeclaredConstructor((Class<?>[])new Class[0]);
                c.setAccessible(true);
                t = c.newInstance(new Object[0]);
                if (!get().components.containsKey(key)) {
                    add(key, t);
                }
            }
            catch (InstantiationException e) {
                throw new Error(e);
            }
            catch (IllegalAccessException e2) {
                throw new Error(e2);
            }
            catch (NoSuchMethodException e3) {
                throw new Error(e3);
            }
            catch (InvocationTargetException e4) {
                throw new Error(e4);
            }
        }
        assert t != null;
        return t;
    }
    
    public static Ring get() {
        return Ring.instances.get();
    }
    
    public static Ring begin() {
        final Ring r = Ring.instances.get();
        Ring.instances.set(new Ring());
        return r;
    }
    
    public static void end(final Ring old) {
        Ring.instances.set(old);
    }
    
    static {
        instances = new ThreadLocal<Ring>() {
            public Ring initialValue() {
                return new Ring(null);
            }
        };
    }
}
