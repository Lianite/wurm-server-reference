// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.reflect.opt;

import java.security.AccessController;
import java.security.PrivilegedAction;
import com.sun.xml.bind.Util;
import java.util.Collections;
import java.util.WeakHashMap;
import java.lang.reflect.InvocationTargetException;
import com.sun.xml.bind.v2.runtime.reflect.Accessor;
import java.util.HashMap;
import java.util.logging.Level;
import java.lang.reflect.Method;
import java.util.logging.Logger;
import java.lang.ref.WeakReference;
import java.util.Map;

final class Injector
{
    private static final Map<ClassLoader, WeakReference<Injector>> injectors;
    private static final Logger logger;
    private final Map<String, Class> classes;
    private final ClassLoader parent;
    private final boolean loadable;
    private static final Method defineClass;
    private static final Method resolveClass;
    
    static Class inject(final ClassLoader cl, final String className, final byte[] image) {
        final Injector injector = get(cl);
        if (injector != null) {
            return injector.inject(className, image);
        }
        return null;
    }
    
    static Class find(final ClassLoader cl, final String className) {
        final Injector injector = get(cl);
        if (injector != null) {
            return injector.find(className);
        }
        return null;
    }
    
    private static Injector get(final ClassLoader cl) {
        Injector injector = null;
        final WeakReference<Injector> wr = Injector.injectors.get(cl);
        if (wr != null) {
            injector = wr.get();
        }
        if (injector == null) {
            try {
                Injector.injectors.put(cl, new WeakReference<Injector>(injector = new Injector(cl)));
            }
            catch (SecurityException e) {
                Injector.logger.log(Level.FINE, "Unable to set up a back-door for the injector", e);
                return null;
            }
        }
        return injector;
    }
    
    private Injector(final ClassLoader parent) {
        this.classes = new HashMap<String, Class>();
        this.parent = parent;
        assert parent != null;
        boolean loadable = false;
        try {
            loadable = (parent.loadClass(Accessor.class.getName()) == Accessor.class);
        }
        catch (ClassNotFoundException ex) {}
        this.loadable = loadable;
    }
    
    private synchronized Class inject(final String className, final byte[] image) {
        if (!this.loadable) {
            return null;
        }
        Class c = this.classes.get(className);
        if (c == null) {
            try {
                c = (Class)Injector.defineClass.invoke(this.parent, className.replace('/', '.'), image, 0, image.length);
                Injector.resolveClass.invoke(this.parent, c);
            }
            catch (IllegalAccessException e) {
                Injector.logger.log(Level.FINE, "Unable to inject " + className, e);
                return null;
            }
            catch (InvocationTargetException e2) {
                Injector.logger.log(Level.FINE, "Unable to inject " + className, e2);
                return null;
            }
            catch (SecurityException e3) {
                Injector.logger.log(Level.FINE, "Unable to inject " + className, e3);
                return null;
            }
            catch (LinkageError e4) {
                Injector.logger.log(Level.FINE, "Unable to inject " + className, e4);
                return null;
            }
            this.classes.put(className, c);
        }
        return c;
    }
    
    private synchronized Class find(final String className) {
        return this.classes.get(className);
    }
    
    static {
        injectors = Collections.synchronizedMap(new WeakHashMap<ClassLoader, WeakReference<Injector>>());
        logger = Util.getClassLogger();
        try {
            defineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, Integer.TYPE, Integer.TYPE);
            resolveClass = ClassLoader.class.getDeclaredMethod("resolveClass", Class.class);
        }
        catch (NoSuchMethodException e) {
            throw new NoSuchMethodError(e.getMessage());
        }
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            public Void run() {
                Injector.defineClass.setAccessible(true);
                Injector.resolveClass.setAccessible(true);
                return null;
            }
        });
    }
}
