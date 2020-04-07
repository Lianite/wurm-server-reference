// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.util;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.TypeVariable;
import java.util.Map;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

public class Reflections
{
    public static Object invoke(final Method method, final Object target, final Object... args) throws Exception {
        try {
            return method.invoke(target, args);
        }
        catch (IllegalArgumentException iae) {
            String message = "Could not invoke method by reflection: " + toString(method);
            if (args != null && args.length > 0) {
                message = message + " with parameters: (" + toClassNameString(", ", args) + ')';
            }
            message = message + " on: " + target.getClass().getName();
            throw new IllegalArgumentException(message, iae);
        }
        catch (InvocationTargetException ite) {
            if (ite.getCause() instanceof Exception) {
                throw (Exception)ite.getCause();
            }
            throw ite;
        }
    }
    
    public static Object get(final Field field, final Object target) throws Exception {
        final boolean accessible = field.isAccessible();
        try {
            field.setAccessible(true);
            return field.get(target);
        }
        catch (IllegalArgumentException iae) {
            final String message = "Could not get field value by reflection: " + toString(field) + " on: " + target.getClass().getName();
            throw new IllegalArgumentException(message, iae);
        }
        finally {
            field.setAccessible(accessible);
        }
    }
    
    public static Method getMethod(final Class clazz, final String name) {
        Class superClass = clazz;
        while (superClass != null && superClass != Object.class) {
            try {
                return superClass.getDeclaredMethod(name, (Class[])new Class[0]);
            }
            catch (NoSuchMethodException nsme) {
                superClass = superClass.getSuperclass();
                continue;
            }
            break;
        }
        throw new IllegalArgumentException("No such method: " + clazz.getName() + '.' + name);
    }
    
    public static void set(final Field field, final Object target, final Object value) throws Exception {
        final boolean accessible = field.isAccessible();
        try {
            field.setAccessible(true);
            field.set(target, value);
        }
        catch (IllegalArgumentException iae) {
            String message = "Could not set field value by reflection: " + toString(field) + " on: " + field.getDeclaringClass().getName();
            if (value == null) {
                message += " with null value";
            }
            else {
                message = message + " with value: " + value.getClass();
            }
            throw new IllegalArgumentException(message, iae);
        }
        finally {
            field.setAccessible(accessible);
        }
    }
    
    public static String getMethodPropertyName(final String methodName) {
        String methodPropertyName = null;
        if (methodName.startsWith("get")) {
            methodPropertyName = decapitalize(methodName.substring(3));
        }
        else if (methodName.startsWith("is")) {
            methodPropertyName = decapitalize(methodName.substring(2));
        }
        else if (methodName.startsWith("set")) {
            methodPropertyName = decapitalize(methodName.substring(3));
        }
        return methodPropertyName;
    }
    
    public static Method getGetterMethod(final Class clazz, final String name) {
        for (Class superClass = clazz; superClass != null && superClass != Object.class; superClass = superClass.getSuperclass()) {
            for (final Method method : superClass.getDeclaredMethods()) {
                final String methodName = method.getName();
                if (method.getParameterTypes().length == 0) {
                    if (methodName.startsWith("get")) {
                        if (decapitalize(methodName.substring(3)).equals(name)) {
                            return method;
                        }
                    }
                    else if (methodName.startsWith("is") && decapitalize(methodName.substring(2)).equals(name)) {
                        return method;
                    }
                }
            }
        }
        return null;
    }
    
    public static List<Method> getMethods(final Class clazz, final Class annotation) {
        final List<Method> methods = new ArrayList<Method>();
        for (Class superClass = clazz; superClass != null && superClass != Object.class; superClass = superClass.getSuperclass()) {
            for (final Method method : superClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(annotation)) {
                    methods.add(method);
                }
            }
        }
        return methods;
    }
    
    public static Field getField(final Class clazz, final String name) {
        Class superClass = clazz;
        while (superClass != null && superClass != Object.class) {
            try {
                return superClass.getDeclaredField(name);
            }
            catch (NoSuchFieldException nsfe) {
                superClass = superClass.getSuperclass();
                continue;
            }
            break;
        }
        return null;
    }
    
    public static List<Field> getFields(final Class clazz, final Class annotation) {
        final List<Field> fields = new ArrayList<Field>();
        for (Class superClass = clazz; superClass != null && superClass != Object.class; superClass = superClass.getSuperclass()) {
            for (final Field field : superClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(annotation)) {
                    fields.add(field);
                }
            }
        }
        return fields;
    }
    
    public static <T> List<Class<?>> getTypeArguments(final Class<T> baseClass, final Class<? extends T> childClass) {
        final Map<Type, Type> resolvedTypes = new HashMap<Type, Type>();
        Type type = childClass;
        while (!getClass(type).equals(baseClass)) {
            if (type instanceof Class) {
                type = ((Class)type).getGenericSuperclass();
            }
            else {
                final ParameterizedType parameterizedType = (ParameterizedType)type;
                final Class<?> rawType = (Class<?>)parameterizedType.getRawType();
                final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                final TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
                for (int i = 0; i < actualTypeArguments.length; ++i) {
                    resolvedTypes.put(typeParameters[i], actualTypeArguments[i]);
                }
                if (rawType.equals(baseClass)) {
                    continue;
                }
                type = rawType.getGenericSuperclass();
            }
        }
        Type[] actualTypeArguments2;
        if (type instanceof Class) {
            actualTypeArguments2 = ((Class)type).getTypeParameters();
        }
        else {
            actualTypeArguments2 = ((ParameterizedType)type).getActualTypeArguments();
        }
        final List<Class<?>> typeArgumentsAsClasses = new ArrayList<Class<?>>();
        for (Type baseType : actualTypeArguments2) {
            while (resolvedTypes.containsKey(baseType)) {
                baseType = resolvedTypes.get(baseType);
            }
            typeArgumentsAsClasses.add(getClass(baseType));
        }
        return typeArgumentsAsClasses;
    }
    
    public static Class<?> getClass(final Type type) {
        if (type instanceof Class) {
            return (Class<?>)type;
        }
        if (type instanceof ParameterizedType) {
            return getClass(((ParameterizedType)type).getRawType());
        }
        if (!(type instanceof GenericArrayType)) {
            return null;
        }
        final Type componentType = ((GenericArrayType)type).getGenericComponentType();
        final Class<?> componentClass = getClass(componentType);
        if (componentClass != null) {
            return Array.newInstance(componentClass, 0).getClass();
        }
        return null;
    }
    
    public static Object getAndWrap(final Field field, final Object target) {
        final boolean accessible = field.isAccessible();
        try {
            field.setAccessible(true);
            return get(field, target);
        }
        catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new IllegalArgumentException("exception setting: " + field.getName(), e);
        }
        finally {
            field.setAccessible(accessible);
        }
    }
    
    public static void setAndWrap(final Field field, final Object target, final Object value) {
        final boolean accessible = field.isAccessible();
        try {
            field.setAccessible(true);
            set(field, target, value);
        }
        catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new IllegalArgumentException("exception setting: " + field.getName(), e);
        }
        finally {
            field.setAccessible(accessible);
        }
    }
    
    public static Object invokeAndWrap(final Method method, final Object target, final Object... args) {
        try {
            return invoke(method, target, args);
        }
        catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new RuntimeException("exception invoking: " + method.getName(), e);
        }
    }
    
    public static String toString(final Member member) {
        return unqualify(member.getDeclaringClass().getName()) + '.' + member.getName();
    }
    
    public static Class classForName(final String name) throws ClassNotFoundException {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(name);
        }
        catch (Exception e) {
            return Class.forName(name);
        }
    }
    
    public static boolean isClassAvailable(final String name) {
        try {
            classForName(name);
        }
        catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }
    
    public static Class getCollectionElementType(final Type collectionType) {
        if (!(collectionType instanceof ParameterizedType)) {
            throw new IllegalArgumentException("collection type not parameterized");
        }
        final Type[] typeArguments = ((ParameterizedType)collectionType).getActualTypeArguments();
        if (typeArguments.length == 0) {
            throw new IllegalArgumentException("no type arguments for collection type");
        }
        Type typeArgument = (typeArguments.length == 1) ? typeArguments[0] : typeArguments[1];
        if (typeArgument instanceof ParameterizedType) {
            typeArgument = ((ParameterizedType)typeArgument).getRawType();
        }
        if (!(typeArgument instanceof Class)) {
            throw new IllegalArgumentException("type argument not a class");
        }
        return (Class)typeArgument;
    }
    
    public static Class getMapKeyType(final Type collectionType) {
        if (!(collectionType instanceof ParameterizedType)) {
            throw new IllegalArgumentException("collection type not parameterized");
        }
        final Type[] typeArguments = ((ParameterizedType)collectionType).getActualTypeArguments();
        if (typeArguments.length == 0) {
            throw new IllegalArgumentException("no type arguments for collection type");
        }
        final Type typeArgument = typeArguments[0];
        if (!(typeArgument instanceof Class)) {
            throw new IllegalArgumentException("type argument not a class");
        }
        return (Class)typeArgument;
    }
    
    public static Method getSetterMethod(final Class clazz, final String name) {
        final Method[] arr$;
        final Method[] methods = arr$ = clazz.getMethods();
        for (final Method method : arr$) {
            final String methodName = method.getName();
            if (methodName.startsWith("set") && method.getParameterTypes().length == 1 && decapitalize(methodName.substring(3)).equals(name)) {
                return method;
            }
        }
        throw new IllegalArgumentException("no such setter method: " + clazz.getName() + '.' + name);
    }
    
    public static Method getMethod(final Annotation annotation, final String name) {
        try {
            return annotation.annotationType().getMethod(name, (Class<?>[])new Class[0]);
        }
        catch (NoSuchMethodException nsme) {
            return null;
        }
    }
    
    public static boolean isInstanceOf(final Class clazz, final String name) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        for (Class c = clazz; c != Object.class; c = c.getSuperclass()) {
            if (instanceOf(c, name)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean instanceOf(final Class clazz, final String name) {
        if (name.equals(clazz.getName())) {
            return true;
        }
        boolean found = false;
        final Class[] interfaces = clazz.getInterfaces();
        for (int i = 0; i < interfaces.length && !found; found = instanceOf(interfaces[i], name), ++i) {}
        return found;
    }
    
    public static String toClassNameString(final String sep, final Object... objects) {
        if (objects.length == 0) {
            return "";
        }
        final StringBuilder builder = new StringBuilder();
        for (final Object object : objects) {
            builder.append(sep);
            if (object == null) {
                builder.append("null");
            }
            else {
                builder.append(object.getClass().getName());
            }
        }
        return builder.substring(sep.length());
    }
    
    public static String unqualify(final String name) {
        return unqualify(name, '.');
    }
    
    public static String unqualify(final String name, final char sep) {
        return name.substring(name.lastIndexOf(sep) + 1, name.length());
    }
    
    public static String decapitalize(final String name) {
        if (name == null) {
            return null;
        }
        if (name.length() == 0 || (name.length() > 1 && Character.isUpperCase(name.charAt(1)))) {
            return name;
        }
        final char[] chars = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }
}
