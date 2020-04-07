// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2;

import java.util.WeakHashMap;
import com.sun.xml.bind.Util;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.logging.Level;
import java.lang.reflect.Constructor;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.logging.Logger;

public final class ClassFactory
{
    private static final Class[] emptyClass;
    private static final Object[] emptyObject;
    private static final Logger logger;
    private static final ThreadLocal<Map<Class, WeakReference<Constructor>>> tls;
    
    public static <T> T create0(final Class<T> clazz) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        final Map<Class, WeakReference<Constructor>> m = ClassFactory.tls.get();
        Constructor<T> cons = null;
        final WeakReference<Constructor> consRef = m.get(clazz);
        if (consRef != null) {
            cons = consRef.get();
        }
        if (cons == null) {
            try {
                cons = clazz.getDeclaredConstructor((Class<?>[])ClassFactory.emptyClass);
            }
            catch (NoSuchMethodException e) {
                ClassFactory.logger.log(Level.INFO, "No default constructor found on " + clazz, e);
                NoSuchMethodError exp;
                if (clazz.getDeclaringClass() != null && !Modifier.isStatic(clazz.getModifiers())) {
                    exp = new NoSuchMethodError(Messages.NO_DEFAULT_CONSTRUCTOR_IN_INNER_CLASS.format(clazz.getName()));
                }
                else {
                    exp = new NoSuchMethodError(e.getMessage());
                }
                exp.initCause(e);
                throw exp;
            }
            final int classMod = clazz.getModifiers();
            Label_0225: {
                if (Modifier.isPublic(classMod)) {
                    if (Modifier.isPublic(cons.getModifiers())) {
                        break Label_0225;
                    }
                }
                try {
                    cons.setAccessible(true);
                }
                catch (SecurityException e2) {
                    ClassFactory.logger.log(Level.FINE, "Unable to make the constructor of " + clazz + " accessible", e2);
                    throw e2;
                }
            }
            m.put(clazz, new WeakReference<Constructor>(cons));
        }
        return cons.newInstance(ClassFactory.emptyObject);
    }
    
    public static <T> T create(final Class<T> clazz) {
        try {
            return (T)create0((Class<Object>)clazz);
        }
        catch (InstantiationException e) {
            ClassFactory.logger.log(Level.INFO, "failed to create a new instance of " + clazz, e);
            throw new InstantiationError(e.toString());
        }
        catch (IllegalAccessException e2) {
            ClassFactory.logger.log(Level.INFO, "failed to create a new instance of " + clazz, e2);
            throw new IllegalAccessError(e2.toString());
        }
        catch (InvocationTargetException e3) {
            final Throwable target = e3.getTargetException();
            if (target instanceof RuntimeException) {
                throw (RuntimeException)target;
            }
            if (target instanceof Error) {
                throw (Error)target;
            }
            throw new IllegalStateException(target);
        }
    }
    
    public static Object create(final Method method) {
        Throwable errorMsg;
        try {
            return method.invoke(null, ClassFactory.emptyObject);
        }
        catch (InvocationTargetException ive) {
            final Throwable target = ive.getTargetException();
            if (target instanceof RuntimeException) {
                throw (RuntimeException)target;
            }
            if (target instanceof Error) {
                throw (Error)target;
            }
            throw new IllegalStateException(target);
        }
        catch (IllegalAccessException e) {
            ClassFactory.logger.log(Level.INFO, "failed to create a new instance of " + method.getReturnType().getName(), e);
            throw new IllegalAccessError(e.toString());
        }
        catch (IllegalArgumentException iae) {
            ClassFactory.logger.log(Level.INFO, "failed to create a new instance of " + method.getReturnType().getName(), iae);
            errorMsg = iae;
        }
        catch (NullPointerException npe) {
            ClassFactory.logger.log(Level.INFO, "failed to create a new instance of " + method.getReturnType().getName(), npe);
            errorMsg = npe;
        }
        catch (ExceptionInInitializerError eie) {
            ClassFactory.logger.log(Level.INFO, "failed to create a new instance of " + method.getReturnType().getName(), eie);
            errorMsg = eie;
        }
        final NoSuchMethodError exp = new NoSuchMethodError(errorMsg.getMessage());
        exp.initCause(errorMsg);
        throw exp;
    }
    
    public static <T> Class<? extends T> inferImplClass(final Class<T> fieldType, final Class[] knownImplClasses) {
        if (!fieldType.isInterface()) {
            return (Class<? extends T>)fieldType;
        }
        for (final Class<?> impl : knownImplClasses) {
            if (fieldType.isAssignableFrom(impl)) {
                return impl.asSubclass(fieldType);
            }
        }
        return null;
    }
    
    static {
        emptyClass = new Class[0];
        emptyObject = new Object[0];
        logger = Util.getClassLogger();
        tls = new ThreadLocal<Map<Class, WeakReference<Constructor>>>() {
            public Map<Class, WeakReference<Constructor>> initialValue() {
                return new WeakHashMap<Class, WeakReference<Constructor>>();
            }
        };
    }
}
