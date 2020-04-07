// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.annotation;

import java.util.HashMap;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.lang.annotation.Annotation;
import com.sun.xml.bind.v2.runtime.Location;
import java.lang.reflect.InvocationHandler;

public class LocatableAnnotation implements InvocationHandler, Locatable, Location
{
    private final Annotation core;
    private final Locatable upstream;
    private static final Map<Class, Quick> quicks;
    
    public static <A extends Annotation> A create(final A annotation, final Locatable parentSourcePos) {
        if (annotation == null) {
            return null;
        }
        final Class<? extends Annotation> type = annotation.annotationType();
        if (LocatableAnnotation.quicks.containsKey(type)) {
            return (A)LocatableAnnotation.quicks.get(type).newInstance(parentSourcePos, annotation);
        }
        final ClassLoader cl = LocatableAnnotation.class.getClassLoader();
        try {
            final Class loadableT = Class.forName(type.getName(), false, cl);
            if (loadableT != type) {
                return annotation;
            }
            return (A)Proxy.newProxyInstance(cl, new Class[] { type, Locatable.class }, new LocatableAnnotation(annotation, parentSourcePos));
        }
        catch (ClassNotFoundException e) {
            return annotation;
        }
        catch (IllegalArgumentException e2) {
            return annotation;
        }
    }
    
    LocatableAnnotation(final Annotation core, final Locatable upstream) {
        this.core = core;
        this.upstream = upstream;
    }
    
    public Locatable getUpstream() {
        return this.upstream;
    }
    
    public Location getLocation() {
        return this;
    }
    
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        try {
            if (method.getDeclaringClass() == Locatable.class) {
                return method.invoke(this, args);
            }
            if (Modifier.isStatic(method.getModifiers())) {
                throw new IllegalArgumentException();
            }
            return method.invoke(this.core, args);
        }
        catch (InvocationTargetException e) {
            if (e.getTargetException() != null) {
                throw e.getTargetException();
            }
            throw e;
        }
    }
    
    public String toString() {
        return this.core.toString();
    }
    
    static {
        quicks = new HashMap<Class, Quick>();
        for (final Quick q : Init.getAll()) {
            LocatableAnnotation.quicks.put(q.annotationType(), q);
        }
    }
}
