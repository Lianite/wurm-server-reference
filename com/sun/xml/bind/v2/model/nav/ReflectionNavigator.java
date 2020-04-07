// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.nav;

import java.lang.reflect.WildcardType;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Array;
import com.sun.xml.bind.v2.runtime.Location;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

public final class ReflectionNavigator implements Navigator<Type, Class, Field, Method>
{
    private static final TypeVisitor<Type, Class> baseClassFinder;
    private static final TypeVisitor<Type, BinderArg> binder;
    private static final TypeVisitor<Class, Void> eraser;
    
    public Class getSuperClass(final Class clazz) {
        if (clazz == Object.class) {
            return null;
        }
        Class sc = clazz.getSuperclass();
        if (sc == null) {
            sc = Object.class;
        }
        return sc;
    }
    
    public Type getBaseClass(final Type t, final Class sup) {
        return ReflectionNavigator.baseClassFinder.visit(t, sup);
    }
    
    public String getClassName(final Class clazz) {
        return clazz.getName();
    }
    
    public String getTypeName(final Type type) {
        if (!(type instanceof Class)) {
            return type.toString();
        }
        final Class c = (Class)type;
        if (c.isArray()) {
            return this.getTypeName((Type)c.getComponentType()) + "[]";
        }
        return c.getName();
    }
    
    public String getClassShortName(final Class clazz) {
        return clazz.getSimpleName();
    }
    
    public Collection<? extends Field> getDeclaredFields(final Class clazz) {
        return Arrays.asList(clazz.getDeclaredFields());
    }
    
    public Field getDeclaredField(final Class clazz, final String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        }
        catch (NoSuchFieldException e) {
            return null;
        }
    }
    
    public Collection<? extends Method> getDeclaredMethods(final Class clazz) {
        return Arrays.asList(clazz.getDeclaredMethods());
    }
    
    public Class getDeclaringClassForField(final Field field) {
        return field.getDeclaringClass();
    }
    
    public Class getDeclaringClassForMethod(final Method method) {
        return method.getDeclaringClass();
    }
    
    public Type getFieldType(final Field field) {
        return this.fix(field.getGenericType());
    }
    
    public String getFieldName(final Field field) {
        return field.getName();
    }
    
    public String getMethodName(final Method method) {
        return method.getName();
    }
    
    public Type getReturnType(final Method method) {
        return this.fix(method.getGenericReturnType());
    }
    
    public Type[] getMethodParameters(final Method method) {
        return method.getGenericParameterTypes();
    }
    
    public boolean isStaticMethod(final Method method) {
        return Modifier.isStatic(method.getModifiers());
    }
    
    public boolean isSubClassOf(final Type sub, final Type sup) {
        return this.erasure(sup).isAssignableFrom(this.erasure(sub));
    }
    
    public Class ref(final Class c) {
        return c;
    }
    
    public Class use(final Class c) {
        return c;
    }
    
    public Class asDecl(final Type t) {
        return this.erasure(t);
    }
    
    public Class asDecl(final Class c) {
        return c;
    }
    
    public <T> Class<T> erasure(final Type t) {
        return ReflectionNavigator.eraser.visit(t, null);
    }
    
    public boolean isAbstract(final Class clazz) {
        return Modifier.isAbstract(clazz.getModifiers());
    }
    
    public boolean isFinal(final Class clazz) {
        return Modifier.isFinal(clazz.getModifiers());
    }
    
    public Type createParameterizedType(final Class rawType, final Type... arguments) {
        return new ParameterizedTypeImpl(rawType, arguments, null);
    }
    
    public boolean isArray(final Type t) {
        if (t instanceof Class) {
            final Class c = (Class)t;
            return c.isArray();
        }
        return t instanceof GenericArrayType;
    }
    
    public boolean isArrayButNotByteArray(Type t) {
        if (t instanceof Class) {
            final Class c = (Class)t;
            return c.isArray() && c != byte[].class;
        }
        if (t instanceof GenericArrayType) {
            t = ((GenericArrayType)t).getGenericComponentType();
            return t != Byte.TYPE;
        }
        return false;
    }
    
    public Type getComponentType(final Type t) {
        if (t instanceof Class) {
            final Class c = (Class)t;
            return c.getComponentType();
        }
        if (t instanceof GenericArrayType) {
            return ((GenericArrayType)t).getGenericComponentType();
        }
        throw new IllegalArgumentException();
    }
    
    public Type getTypeArgument(final Type type, final int i) {
        if (type instanceof ParameterizedType) {
            final ParameterizedType p = (ParameterizedType)type;
            return this.fix(p.getActualTypeArguments()[i]);
        }
        throw new IllegalArgumentException();
    }
    
    public boolean isParameterizedType(final Type type) {
        return type instanceof ParameterizedType;
    }
    
    public boolean isPrimitive(final Type type) {
        if (type instanceof Class) {
            final Class c = (Class)type;
            return c.isPrimitive();
        }
        return false;
    }
    
    public Type getPrimitive(final Class primitiveType) {
        assert primitiveType.isPrimitive();
        return primitiveType;
    }
    
    public Location getClassLocation(final Class clazz) {
        return new Location() {
            public String toString() {
                return clazz.getName();
            }
        };
    }
    
    public Location getFieldLocation(final Field field) {
        return new Location() {
            public String toString() {
                return field.toString();
            }
        };
    }
    
    public Location getMethodLocation(final Method method) {
        return new Location() {
            public String toString() {
                return method.toString();
            }
        };
    }
    
    public boolean hasDefaultConstructor(final Class c) {
        try {
            c.getDeclaredConstructor((Class[])new Class[0]);
            return true;
        }
        catch (NoSuchMethodException e) {
            return false;
        }
    }
    
    public boolean isStaticField(final Field field) {
        return Modifier.isStatic(field.getModifiers());
    }
    
    public boolean isPublicMethod(final Method method) {
        return Modifier.isPublic(method.getModifiers());
    }
    
    public boolean isPublicField(final Field field) {
        return Modifier.isPublic(field.getModifiers());
    }
    
    public boolean isEnum(final Class c) {
        return Enum.class.isAssignableFrom(c);
    }
    
    public Field[] getEnumConstants(final Class clazz) {
        try {
            final Object[] values = clazz.getEnumConstants();
            final Field[] fields = new Field[values.length];
            for (int i = 0; i < values.length; ++i) {
                fields[i] = clazz.getField(((Enum)values[i]).name());
            }
            return fields;
        }
        catch (NoSuchFieldException e) {
            throw new NoSuchFieldError(e.getMessage());
        }
    }
    
    public Type getVoidType() {
        return Void.class;
    }
    
    public String getPackageName(final Class clazz) {
        final String name = clazz.getName();
        final int idx = name.lastIndexOf(46);
        if (idx < 0) {
            return "";
        }
        return name.substring(0, idx);
    }
    
    public Class findClass(final String className, final Class referencePoint) {
        try {
            ClassLoader cl = referencePoint.getClassLoader();
            if (cl == null) {
                cl = ClassLoader.getSystemClassLoader();
            }
            return cl.loadClass(className);
        }
        catch (ClassNotFoundException e) {
            return null;
        }
    }
    
    public boolean isBridgeMethod(final Method method) {
        return method.isBridge();
    }
    
    public boolean isOverriding(final Method method, Class base) {
        final String name = method.getName();
        final Class[] params = method.getParameterTypes();
        while (base != null) {
            try {
                if (base.getDeclaredMethod(name, (Class[])params) != null) {
                    return true;
                }
            }
            catch (NoSuchMethodException ex) {}
            base = base.getSuperclass();
        }
        return false;
    }
    
    public boolean isInterface(final Class clazz) {
        return clazz.isInterface();
    }
    
    public boolean isTransient(final Field f) {
        return Modifier.isTransient(f.getModifiers());
    }
    
    public boolean isInnerClass(final Class clazz) {
        return clazz.getEnclosingClass() != null && !Modifier.isStatic(clazz.getModifiers());
    }
    
    private Type fix(final Type t) {
        if (!(t instanceof GenericArrayType)) {
            return t;
        }
        final GenericArrayType gat = (GenericArrayType)t;
        if (gat.getGenericComponentType() instanceof Class) {
            final Class c = (Class)gat.getGenericComponentType();
            return Array.newInstance(c, 0).getClass();
        }
        return t;
    }
    
    static {
        baseClassFinder = new TypeVisitor<Type, Class>() {
            public Type onClass(final Class c, final Class sup) {
                if (sup == c) {
                    return sup;
                }
                final Type sc = c.getGenericSuperclass();
                if (sc != null) {
                    final Type r = this.visit(sc, sup);
                    if (r != null) {
                        return r;
                    }
                }
                for (final Type i : c.getGenericInterfaces()) {
                    final Type r = this.visit(i, sup);
                    if (r != null) {
                        return r;
                    }
                }
                return null;
            }
            
            public Type onParameterizdType(final ParameterizedType p, final Class sup) {
                final Class raw = (Class)p.getRawType();
                if (raw == sup) {
                    return p;
                }
                Type r = raw.getGenericSuperclass();
                if (r != null) {
                    r = this.visit(this.bind(r, raw, p), sup);
                }
                if (r != null) {
                    return r;
                }
                for (final Type i : raw.getGenericInterfaces()) {
                    r = this.visit(this.bind(i, raw, p), sup);
                    if (r != null) {
                        return r;
                    }
                }
                return null;
            }
            
            public Type onGenericArray(final GenericArrayType g, final Class sup) {
                return null;
            }
            
            public Type onVariable(final TypeVariable v, final Class sup) {
                return this.visit(v.getBounds()[0], sup);
            }
            
            public Type onWildcard(final WildcardType w, final Class sup) {
                return null;
            }
            
            private Type bind(final Type t, final GenericDeclaration decl, final ParameterizedType args) {
                return ReflectionNavigator.binder.visit(t, new BinderArg(decl, args.getActualTypeArguments()));
            }
        };
        binder = new TypeVisitor<Type, BinderArg>() {
            public Type onClass(final Class c, final BinderArg args) {
                return c;
            }
            
            public Type onParameterizdType(final ParameterizedType p, final BinderArg args) {
                final Type[] params = p.getActualTypeArguments();
                boolean different = false;
                for (int i = 0; i < params.length; ++i) {
                    final Type t = params[i];
                    params[i] = this.visit(t, args);
                    different |= (t != params[i]);
                }
                Type newOwner = p.getOwnerType();
                if (newOwner != null) {
                    newOwner = this.visit(newOwner, args);
                }
                different |= (p.getOwnerType() != newOwner);
                if (!different) {
                    return p;
                }
                return new ParameterizedTypeImpl((Class<?>)p.getRawType(), params, newOwner);
            }
            
            public Type onGenericArray(final GenericArrayType g, final BinderArg types) {
                final Type c = this.visit(g.getGenericComponentType(), types);
                if (c == g.getGenericComponentType()) {
                    return g;
                }
                return new GenericArrayTypeImpl(c);
            }
            
            public Type onVariable(final TypeVariable v, final BinderArg types) {
                return types.replace(v);
            }
            
            public Type onWildcard(final WildcardType w, final BinderArg types) {
                final Type[] lb = w.getLowerBounds();
                final Type[] ub = w.getUpperBounds();
                boolean diff = false;
                for (int i = 0; i < lb.length; ++i) {
                    final Type t = lb[i];
                    lb[i] = this.visit(t, types);
                    diff |= (t != lb[i]);
                }
                for (int i = 0; i < ub.length; ++i) {
                    final Type t = ub[i];
                    ub[i] = this.visit(t, types);
                    diff |= (t != ub[i]);
                }
                if (!diff) {
                    return w;
                }
                return new WildcardTypeImpl(lb, ub);
            }
        };
        eraser = new TypeVisitor<Class, Void>() {
            public Class onClass(final Class c, final Void _) {
                return c;
            }
            
            public Class onParameterizdType(final ParameterizedType p, final Void _) {
                return this.visit(p.getRawType(), null);
            }
            
            public Class onGenericArray(final GenericArrayType g, final Void _) {
                return Array.newInstance(((TypeVisitor<Class<?>, Void>)this).visit(g.getGenericComponentType(), null), 0).getClass();
            }
            
            public Class onVariable(final TypeVariable v, final Void _) {
                return this.visit(v.getBounds()[0], null);
            }
            
            public Class onWildcard(final WildcardType w, final Void _) {
                return this.visit(w.getUpperBounds()[0], null);
            }
        };
    }
    
    private static class BinderArg
    {
        final TypeVariable[] params;
        final Type[] args;
        
        BinderArg(final TypeVariable[] params, final Type[] args) {
            this.params = params;
            this.args = args;
            assert params.length == args.length;
        }
        
        public BinderArg(final GenericDeclaration decl, final Type[] args) {
            this(decl.getTypeParameters(), args);
        }
        
        Type replace(final TypeVariable v) {
            for (int i = 0; i < this.params.length; ++i) {
                if (this.params[i].equals(v)) {
                    return this.args[i];
                }
            }
            return v;
        }
    }
}
