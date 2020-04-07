// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model.nav;

import com.sun.xml.bind.v2.runtime.Location;
import java.lang.reflect.Type;
import com.sun.codemodel.JClass;
import java.util.Collection;
import com.sun.xml.bind.v2.model.nav.Navigator;

public final class NavigatorImpl implements Navigator<NType, NClass, Void, Void>
{
    public static final NavigatorImpl theInstance;
    
    public NClass getSuperClass(final NClass nClass) {
        throw new UnsupportedOperationException();
    }
    
    public NType getBaseClass(final NType nt, final NClass base) {
        if (!(nt instanceof EagerNType)) {
            if (nt instanceof NClassByJClass) {
                final NClassByJClass nnt = (NClassByJClass)nt;
                if (base instanceof EagerNClass) {
                    final EagerNClass enc = (EagerNClass)base;
                    return this.ref(nnt.clazz.getBaseClass(enc.c));
                }
            }
            throw new UnsupportedOperationException();
        }
        final EagerNType ent = (EagerNType)nt;
        if (base instanceof EagerNClass) {
            final EagerNClass enc = (EagerNClass)base;
            return create(NavigatorImpl.REFLECTION.getBaseClass(ent.t, enc.c));
        }
        return null;
    }
    
    public String getClassName(final NClass nClass) {
        throw new UnsupportedOperationException();
    }
    
    public String getTypeName(final NType type) {
        return type.fullName();
    }
    
    public String getClassShortName(final NClass nClass) {
        throw new UnsupportedOperationException();
    }
    
    public Collection<? extends Void> getDeclaredFields(final NClass nClass) {
        throw new UnsupportedOperationException();
    }
    
    public Void getDeclaredField(final NClass clazz, final String fieldName) {
        throw new UnsupportedOperationException();
    }
    
    public Collection<? extends Void> getDeclaredMethods(final NClass nClass) {
        throw new UnsupportedOperationException();
    }
    
    public NClass getDeclaringClassForField(final Void aVoid) {
        throw new UnsupportedOperationException();
    }
    
    public NClass getDeclaringClassForMethod(final Void aVoid) {
        throw new UnsupportedOperationException();
    }
    
    public NType getFieldType(final Void aVoid) {
        throw new UnsupportedOperationException();
    }
    
    public String getFieldName(final Void aVoid) {
        throw new UnsupportedOperationException();
    }
    
    public String getMethodName(final Void aVoid) {
        throw new UnsupportedOperationException();
    }
    
    public NType getReturnType(final Void aVoid) {
        throw new UnsupportedOperationException();
    }
    
    public NType[] getMethodParameters(final Void aVoid) {
        throw new UnsupportedOperationException();
    }
    
    public boolean isStaticMethod(final Void aVoid) {
        throw new UnsupportedOperationException();
    }
    
    public boolean isSubClassOf(final NType sub, final NType sup) {
        throw new UnsupportedOperationException();
    }
    
    public NClass ref(final Class c) {
        return create(c);
    }
    
    public NClass ref(final JClass c) {
        if (c == null) {
            return null;
        }
        return new NClassByJClass(c);
    }
    
    public NType use(final NClass nc) {
        return nc;
    }
    
    public NClass asDecl(final NType nt) {
        if (nt instanceof NClass) {
            return (NClass)nt;
        }
        return null;
    }
    
    public NClass asDecl(final Class c) {
        return this.ref(c);
    }
    
    public boolean isArray(final NType nType) {
        throw new UnsupportedOperationException();
    }
    
    public boolean isArrayButNotByteArray(final NType t) {
        throw new UnsupportedOperationException();
    }
    
    public NType getComponentType(final NType nType) {
        throw new UnsupportedOperationException();
    }
    
    public NType getTypeArgument(final NType nt, final int i) {
        if (nt instanceof EagerNType) {
            final EagerNType ent = (EagerNType)nt;
            return create(NavigatorImpl.REFLECTION.getTypeArgument(ent.t, i));
        }
        if (nt instanceof NClassByJClass) {
            final NClassByJClass nnt = (NClassByJClass)nt;
            return this.ref(nnt.clazz.getTypeParameters().get(i));
        }
        throw new UnsupportedOperationException();
    }
    
    public boolean isParameterizedType(final NType nt) {
        if (nt instanceof EagerNType) {
            final EagerNType ent = (EagerNType)nt;
            return NavigatorImpl.REFLECTION.isParameterizedType(ent.t);
        }
        if (nt instanceof NClassByJClass) {
            final NClassByJClass nnt = (NClassByJClass)nt;
            return nnt.clazz.isParameterized();
        }
        throw new UnsupportedOperationException();
    }
    
    public boolean isPrimitive(final NType type) {
        throw new UnsupportedOperationException();
    }
    
    public NType getPrimitive(final Class primitiveType) {
        return create(primitiveType);
    }
    
    public static final NType create(final Type t) {
        if (t == null) {
            return null;
        }
        if (t instanceof Class) {
            return create((Class)t);
        }
        return new EagerNType(t);
    }
    
    public static NClass create(final Class c) {
        if (c == null) {
            return null;
        }
        return new EagerNClass(c);
    }
    
    public static NType createParameterizedType(final NClass rawType, final NType... args) {
        return new NParameterizedType(rawType, args);
    }
    
    public static NType createParameterizedType(final Class rawType, final NType... args) {
        return new NParameterizedType(create(rawType), args);
    }
    
    public Location getClassLocation(final NClass c) {
        return new Location() {
            public String toString() {
                return c.fullName();
            }
        };
    }
    
    public Location getFieldLocation(final Void _) {
        throw new IllegalStateException();
    }
    
    public Location getMethodLocation(final Void _) {
        throw new IllegalStateException();
    }
    
    public boolean hasDefaultConstructor(final NClass nClass) {
        throw new UnsupportedOperationException();
    }
    
    public boolean isStaticField(final Void aVoid) {
        throw new IllegalStateException();
    }
    
    public boolean isPublicMethod(final Void aVoid) {
        throw new IllegalStateException();
    }
    
    public boolean isPublicField(final Void aVoid) {
        throw new IllegalStateException();
    }
    
    public boolean isEnum(final NClass c) {
        return this.isSubClassOf((NType)c, (NType)create(Enum.class));
    }
    
    public <T> NType erasure(final NType type) {
        if (type instanceof NParameterizedType) {
            final NParameterizedType pt = (NParameterizedType)type;
            return pt.rawType;
        }
        return type;
    }
    
    public boolean isAbstract(final NClass clazz) {
        return clazz.isAbstract();
    }
    
    public boolean isFinal(final NClass clazz) {
        return false;
    }
    
    public Void[] getEnumConstants(final NClass clazz) {
        throw new UnsupportedOperationException();
    }
    
    public NType getVoidType() {
        return this.ref((Class)Void.TYPE);
    }
    
    public String getPackageName(final NClass clazz) {
        throw new UnsupportedOperationException();
    }
    
    public NClass findClass(final String className, final NClass referencePoint) {
        throw new UnsupportedOperationException();
    }
    
    public boolean isBridgeMethod(final Void method) {
        throw new UnsupportedOperationException();
    }
    
    public boolean isOverriding(final Void method, final NClass clazz) {
        throw new UnsupportedOperationException();
    }
    
    public boolean isInterface(final NClass clazz) {
        throw new UnsupportedOperationException();
    }
    
    public boolean isTransient(final Void f) {
        throw new UnsupportedOperationException();
    }
    
    public boolean isInnerClass(final NClass clazz) {
        throw new UnsupportedOperationException();
    }
    
    static {
        theInstance = new NavigatorImpl();
    }
}
