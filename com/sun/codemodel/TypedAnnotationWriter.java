// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.lang.reflect.InvocationHandler;
import java.lang.annotation.Annotation;

class TypedAnnotationWriter<A extends Annotation, W extends JAnnotationWriter<A>> implements InvocationHandler, JAnnotationWriter<A>
{
    private final JAnnotationUse use;
    private final Class<A> annotation;
    private final Class<W> writerType;
    private Map<String, JAnnotationArrayMember> arrays;
    
    public TypedAnnotationWriter(final Class<A> annotation, final Class<W> writer, final JAnnotationUse use) {
        this.annotation = annotation;
        this.writerType = writer;
        this.use = use;
    }
    
    public JAnnotationUse getAnnotationUse() {
        return this.use;
    }
    
    public Class<A> getAnnotationType() {
        return this.annotation;
    }
    
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        if (method.getDeclaringClass() == JAnnotationWriter.class) {
            try {
                return method.invoke(this, args);
            }
            catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
        final String name = method.getName();
        Object arg = null;
        if (args != null && args.length > 0) {
            arg = args[0];
        }
        final Method m = this.annotation.getDeclaredMethod(name, (Class<?>[])new Class[0]);
        final Class<?> rt = m.getReturnType();
        if (rt.isArray()) {
            return this.addArrayValue(proxy, name, rt.getComponentType(), method.getReturnType(), arg);
        }
        if (Annotation.class.isAssignableFrom(rt)) {
            final Class<? extends Annotation> r = (Class<? extends Annotation>)rt;
            return new TypedAnnotationWriter<Annotation, Object>((Class<Annotation>)r, (Class<Object>)method.getReturnType(), this.use.annotationParam(name, r)).createProxy();
        }
        if (arg instanceof JType) {
            final JType targ = (JType)arg;
            this.checkType(Class.class, rt);
            if (m.getDefaultValue() != null && targ.equals(targ.owner().ref((Class)m.getDefaultValue()))) {
                return proxy;
            }
            this.use.param(name, targ);
            return proxy;
        }
        else {
            this.checkType(arg.getClass(), rt);
            if (m.getDefaultValue() != null && m.getDefaultValue().equals(arg)) {
                return proxy;
            }
            if (arg instanceof String) {
                this.use.param(name, (String)arg);
                return proxy;
            }
            if (arg instanceof Boolean) {
                this.use.param(name, (boolean)arg);
                return proxy;
            }
            if (arg instanceof Integer) {
                this.use.param(name, (int)arg);
                return proxy;
            }
            if (arg instanceof Class) {
                this.use.param(name, (Class)arg);
                return proxy;
            }
            if (arg instanceof Enum) {
                this.use.param(name, (Enum)arg);
                return proxy;
            }
            throw new IllegalArgumentException("Unable to handle this method call " + method.toString());
        }
    }
    
    private Object addArrayValue(final Object proxy, final String name, final Class itemType, final Class expectedReturnType, final Object arg) {
        if (this.arrays == null) {
            this.arrays = new HashMap<String, JAnnotationArrayMember>();
        }
        JAnnotationArrayMember m = this.arrays.get(name);
        if (m == null) {
            m = this.use.paramArray(name);
            this.arrays.put(name, m);
        }
        if (Annotation.class.isAssignableFrom(itemType)) {
            if (!JAnnotationWriter.class.isAssignableFrom(expectedReturnType)) {
                throw new IllegalArgumentException("Unexpected return type " + expectedReturnType);
            }
            return new TypedAnnotationWriter<A, Object>(itemType, expectedReturnType, m.annotate(itemType)).createProxy();
        }
        else {
            if (arg instanceof JType) {
                this.checkType(Class.class, itemType);
                m.param((JType)arg);
                return proxy;
            }
            this.checkType(arg.getClass(), itemType);
            if (arg instanceof String) {
                m.param((String)arg);
                return proxy;
            }
            if (arg instanceof Boolean) {
                m.param((boolean)arg);
                return proxy;
            }
            if (arg instanceof Integer) {
                m.param((int)arg);
                return proxy;
            }
            if (arg instanceof Class) {
                m.param((Class)arg);
                return proxy;
            }
            throw new IllegalArgumentException("Unable to handle this method call ");
        }
    }
    
    private void checkType(final Class actual, final Class expected) {
        if (expected == actual || expected.isAssignableFrom(actual)) {
            return;
        }
        if (expected == JCodeModel.boxToPrimitive.get(actual)) {
            return;
        }
        throw new IllegalArgumentException("Expected " + expected + " but found " + actual);
    }
    
    private W createProxy() {
        return (W)Proxy.newProxyInstance(this.writerType.getClassLoader(), new Class[] { this.writerType }, this);
    }
    
    static <W extends JAnnotationWriter<?>> W create(final Class<W> w, final JAnnotatable annotatable) {
        final Class<? extends Annotation> a = findAnnotationType(w);
        return new TypedAnnotationWriter<Annotation, W>((Class<Annotation>)a, w, annotatable.annotate(a)).createProxy();
    }
    
    private static Class<? extends Annotation> findAnnotationType(final Class clazz) {
        for (final Type t : clazz.getGenericInterfaces()) {
            if (t instanceof ParameterizedType) {
                final ParameterizedType p = (ParameterizedType)t;
                if (p.getRawType() == JAnnotationWriter.class) {
                    return (Class<? extends Annotation>)p.getActualTypeArguments()[0];
                }
            }
            if (t instanceof Class) {
                final Class<? extends Annotation> r = findAnnotationType((Class)t);
                if (r != null) {
                    return r;
                }
            }
        }
        return null;
    }
}
