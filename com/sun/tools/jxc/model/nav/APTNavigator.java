// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.jxc.model.nav;

import com.sun.mirror.util.TypeVisitor;
import java.util.HashMap;
import com.sun.mirror.declaration.ConstructorDeclaration;
import com.sun.mirror.declaration.MemberDeclaration;
import com.sun.xml.bind.v2.runtime.Location;
import com.sun.mirror.declaration.InterfaceDeclaration;
import com.sun.mirror.util.Declarations;
import com.sun.mirror.declaration.EnumConstantDeclaration;
import com.sun.mirror.declaration.EnumDeclaration;
import com.sun.mirror.util.Types;
import com.sun.mirror.declaration.Modifier;
import com.sun.mirror.declaration.ParameterDeclaration;
import com.sun.mirror.util.SourcePosition;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import com.sun.mirror.declaration.ClassDeclaration;
import com.sun.mirror.type.WildcardType;
import com.sun.mirror.type.ArrayType;
import com.sun.mirror.type.ReferenceType;
import com.sun.mirror.type.TypeVariable;
import java.util.Iterator;
import com.sun.mirror.type.InterfaceType;
import com.sun.mirror.type.VoidType;
import com.sun.mirror.type.DeclaredType;
import com.sun.mirror.type.ClassType;
import com.sun.mirror.declaration.Declaration;
import java.util.Comparator;
import com.sun.istack.tools.APTTypeVisitor;
import java.util.Map;
import com.sun.mirror.type.PrimitiveType;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.FieldDeclaration;
import com.sun.mirror.declaration.TypeDeclaration;
import com.sun.mirror.type.TypeMirror;
import com.sun.xml.bind.v2.model.nav.Navigator;

public class APTNavigator implements Navigator<TypeMirror, TypeDeclaration, FieldDeclaration, MethodDeclaration>
{
    private final AnnotationProcessorEnvironment env;
    private final PrimitiveType primitiveByte;
    private static final Map<Class, PrimitiveType.Kind> primitives;
    private static final TypeMirror DUMMY;
    private final APTTypeVisitor<TypeMirror, TypeDeclaration> baseClassFinder;
    private static final Comparator<Declaration> SOURCE_POS_COMPARATOR;
    
    public APTNavigator(final AnnotationProcessorEnvironment env) {
        this.baseClassFinder = new APTTypeVisitor<TypeMirror, TypeDeclaration>() {
            public TypeMirror onClassType(final ClassType type, final TypeDeclaration sup) {
                TypeMirror r = this.onDeclaredType((DeclaredType)type, sup);
                if (r != null) {
                    return r;
                }
                if (type.getSuperclass() != null) {
                    r = this.onClassType(type.getSuperclass(), sup);
                    if (r != null) {
                        return r;
                    }
                }
                return null;
            }
            
            protected TypeMirror onPrimitiveType(final PrimitiveType type, final TypeDeclaration param) {
                return (TypeMirror)type;
            }
            
            protected TypeMirror onVoidType(final VoidType type, final TypeDeclaration param) {
                return (TypeMirror)type;
            }
            
            public TypeMirror onInterfaceType(final InterfaceType type, final TypeDeclaration sup) {
                return this.onDeclaredType((DeclaredType)type, sup);
            }
            
            private TypeMirror onDeclaredType(final DeclaredType t, final TypeDeclaration sup) {
                if (t.getDeclaration().equals(sup)) {
                    return (TypeMirror)t;
                }
                for (final InterfaceType i : t.getSuperinterfaces()) {
                    final TypeMirror r = this.onInterfaceType(i, sup);
                    if (r != null) {
                        return r;
                    }
                }
                return null;
            }
            
            public TypeMirror onTypeVariable(final TypeVariable t, final TypeDeclaration sup) {
                for (final ReferenceType r : t.getDeclaration().getBounds()) {
                    final TypeMirror m = this.apply((TypeMirror)r, sup);
                    if (m != null) {
                        return m;
                    }
                }
                return null;
            }
            
            public TypeMirror onArrayType(final ArrayType type, final TypeDeclaration sup) {
                return null;
            }
            
            public TypeMirror onWildcard(final WildcardType type, final TypeDeclaration sup) {
                for (final ReferenceType r : type.getLowerBounds()) {
                    final TypeMirror m = this.apply((TypeMirror)r, sup);
                    if (m != null) {
                        return m;
                    }
                }
                return null;
            }
        };
        this.env = env;
        this.primitiveByte = env.getTypeUtils().getPrimitiveType(PrimitiveType.Kind.BYTE);
    }
    
    public TypeDeclaration getSuperClass(final TypeDeclaration t) {
        if (!(t instanceof ClassDeclaration)) {
            return this.env.getTypeDeclaration(Object.class.getName());
        }
        final ClassDeclaration c = (ClassDeclaration)t;
        final ClassType sup = c.getSuperclass();
        if (sup != null) {
            return (TypeDeclaration)sup.getDeclaration();
        }
        return null;
    }
    
    public TypeMirror getBaseClass(final TypeMirror type, final TypeDeclaration sup) {
        return this.baseClassFinder.apply(type, sup);
    }
    
    public String getClassName(final TypeDeclaration t) {
        return t.getQualifiedName();
    }
    
    public String getTypeName(final TypeMirror typeMirror) {
        return typeMirror.toString();
    }
    
    public String getClassShortName(final TypeDeclaration t) {
        return t.getSimpleName();
    }
    
    public Collection<FieldDeclaration> getDeclaredFields(final TypeDeclaration c) {
        final List<FieldDeclaration> l = new ArrayList<FieldDeclaration>(c.getFields());
        return this.sort(l);
    }
    
    public FieldDeclaration getDeclaredField(final TypeDeclaration clazz, final String fieldName) {
        for (final FieldDeclaration fd : clazz.getFields()) {
            if (fd.getSimpleName().equals(fieldName)) {
                return fd;
            }
        }
        return null;
    }
    
    public Collection<MethodDeclaration> getDeclaredMethods(final TypeDeclaration c) {
        final List<MethodDeclaration> l = new ArrayList<MethodDeclaration>(c.getMethods());
        return this.sort(l);
    }
    
    private <A extends Declaration> List<A> sort(final List<A> l) {
        if (l.isEmpty()) {
            return l;
        }
        final SourcePosition pos = l.get(0).getPosition();
        if (pos != null) {
            Collections.sort(l, APTNavigator.SOURCE_POS_COMPARATOR);
        }
        else {
            Collections.reverse(l);
        }
        return l;
    }
    
    public ClassDeclaration getDeclaringClassForField(final FieldDeclaration f) {
        return (ClassDeclaration)f.getDeclaringType();
    }
    
    public ClassDeclaration getDeclaringClassForMethod(final MethodDeclaration m) {
        return (ClassDeclaration)m.getDeclaringType();
    }
    
    public TypeMirror getFieldType(final FieldDeclaration f) {
        return f.getType();
    }
    
    public String getFieldName(final FieldDeclaration f) {
        return f.getSimpleName();
    }
    
    public String getMethodName(final MethodDeclaration m) {
        return m.getSimpleName();
    }
    
    public TypeMirror getReturnType(final MethodDeclaration m) {
        return m.getReturnType();
    }
    
    public TypeMirror[] getMethodParameters(final MethodDeclaration m) {
        final Collection<ParameterDeclaration> ps = (Collection<ParameterDeclaration>)m.getParameters();
        final TypeMirror[] r = new TypeMirror[ps.size()];
        int i = 0;
        for (final ParameterDeclaration p : ps) {
            r[i++] = p.getType();
        }
        return r;
    }
    
    public boolean isStaticMethod(final MethodDeclaration m) {
        return this.hasModifier((Declaration)m, Modifier.STATIC);
    }
    
    private boolean hasModifier(final Declaration d, final Modifier mod) {
        return d.getModifiers().contains(mod);
    }
    
    public boolean isSubClassOf(final TypeMirror sub, final TypeMirror sup) {
        return sup != APTNavigator.DUMMY && this.env.getTypeUtils().isSubtype(sub, sup);
    }
    
    private String getSourceClassName(final Class clazz) {
        final Class<?> d = (Class<?>)clazz.getDeclaringClass();
        if (d == null) {
            return clazz.getName();
        }
        final String shortName = clazz.getName().substring(d.getName().length() + 1);
        return this.getSourceClassName(d) + '.' + shortName;
    }
    
    public TypeMirror ref(final Class c) {
        if (c.isArray()) {
            return (TypeMirror)this.env.getTypeUtils().getArrayType(this.ref((Class)c.getComponentType()));
        }
        if (c.isPrimitive()) {
            return this.getPrimitive(c);
        }
        final TypeDeclaration t = this.env.getTypeDeclaration(this.getSourceClassName(c));
        if (t == null) {
            return APTNavigator.DUMMY;
        }
        return (TypeMirror)this.env.getTypeUtils().getDeclaredType(t, new TypeMirror[0]);
    }
    
    public TypeMirror use(final TypeDeclaration t) {
        assert t != null;
        return (TypeMirror)this.env.getTypeUtils().getDeclaredType(t, new TypeMirror[0]);
    }
    
    public TypeDeclaration asDecl(TypeMirror m) {
        m = this.env.getTypeUtils().getErasure(m);
        if (m instanceof DeclaredType) {
            final DeclaredType d = (DeclaredType)m;
            return d.getDeclaration();
        }
        return null;
    }
    
    public TypeDeclaration asDecl(final Class c) {
        return this.env.getTypeDeclaration(this.getSourceClassName(c));
    }
    
    public <T> TypeMirror erasure(TypeMirror t) {
        final Types tu = this.env.getTypeUtils();
        t = tu.getErasure(t);
        if (t instanceof DeclaredType) {
            final DeclaredType dt = (DeclaredType)t;
            if (!dt.getActualTypeArguments().isEmpty()) {
                return (TypeMirror)tu.getDeclaredType(dt.getDeclaration(), new TypeMirror[0]);
            }
        }
        return t;
    }
    
    public boolean isAbstract(final TypeDeclaration clazz) {
        return this.hasModifier((Declaration)clazz, Modifier.ABSTRACT);
    }
    
    public boolean isFinal(final TypeDeclaration clazz) {
        return this.hasModifier((Declaration)clazz, Modifier.FINAL);
    }
    
    public FieldDeclaration[] getEnumConstants(final TypeDeclaration clazz) {
        final EnumDeclaration ed = (EnumDeclaration)clazz;
        final Collection<EnumConstantDeclaration> constants = (Collection<EnumConstantDeclaration>)ed.getEnumConstants();
        return constants.toArray((FieldDeclaration[])new EnumConstantDeclaration[constants.size()]);
    }
    
    public TypeMirror getVoidType() {
        return (TypeMirror)this.env.getTypeUtils().getVoidType();
    }
    
    public String getPackageName(final TypeDeclaration clazz) {
        return clazz.getPackage().getQualifiedName();
    }
    
    public TypeDeclaration findClass(final String className, final TypeDeclaration referencePoint) {
        return this.env.getTypeDeclaration(className);
    }
    
    public boolean isBridgeMethod(final MethodDeclaration method) {
        return method.getModifiers().contains(Modifier.VOLATILE);
    }
    
    public boolean isOverriding(final MethodDeclaration method, final TypeDeclaration base) {
        ClassDeclaration sc = (ClassDeclaration)base;
        final Declarations declUtil = this.env.getDeclarationUtils();
        while (true) {
            for (final MethodDeclaration m : sc.getMethods()) {
                if (declUtil.overrides(method, m)) {
                    return true;
                }
            }
            if (sc.getSuperclass() == null) {
                return false;
            }
            sc = sc.getSuperclass().getDeclaration();
        }
    }
    
    public boolean isInterface(final TypeDeclaration clazz) {
        return clazz instanceof InterfaceDeclaration;
    }
    
    public boolean isTransient(final FieldDeclaration f) {
        return f.getModifiers().contains(Modifier.TRANSIENT);
    }
    
    public boolean isInnerClass(final TypeDeclaration clazz) {
        return clazz.getDeclaringType() != null && !clazz.getModifiers().contains(Modifier.STATIC);
    }
    
    public boolean isArray(final TypeMirror t) {
        return t instanceof ArrayType;
    }
    
    public boolean isArrayButNotByteArray(final TypeMirror t) {
        if (!this.isArray(t)) {
            return false;
        }
        final ArrayType at = (ArrayType)t;
        final TypeMirror ct = at.getComponentType();
        return !ct.equals(this.primitiveByte);
    }
    
    public TypeMirror getComponentType(final TypeMirror t) {
        if (t instanceof ArrayType) {
            final ArrayType at = (ArrayType)t;
            return at.getComponentType();
        }
        throw new IllegalArgumentException();
    }
    
    public TypeMirror getTypeArgument(final TypeMirror typeMirror, final int i) {
        if (typeMirror instanceof DeclaredType) {
            final DeclaredType d = (DeclaredType)typeMirror;
            final TypeMirror[] args = d.getActualTypeArguments().toArray(new TypeMirror[0]);
            return args[i];
        }
        throw new IllegalArgumentException();
    }
    
    public boolean isParameterizedType(final TypeMirror t) {
        if (t instanceof DeclaredType) {
            final DeclaredType d = (DeclaredType)t;
            return !d.getActualTypeArguments().isEmpty();
        }
        return false;
    }
    
    public boolean isPrimitive(final TypeMirror t) {
        return t instanceof PrimitiveType;
    }
    
    public TypeMirror getPrimitive(final Class primitiveType) {
        assert primitiveType.isPrimitive();
        if (primitiveType == Void.TYPE) {
            return this.getVoidType();
        }
        return (TypeMirror)this.env.getTypeUtils().getPrimitiveType((PrimitiveType.Kind)APTNavigator.primitives.get(primitiveType));
    }
    
    public Location getClassLocation(final TypeDeclaration decl) {
        return this.getLocation(decl.getQualifiedName(), decl.getPosition());
    }
    
    public Location getFieldLocation(final FieldDeclaration decl) {
        return this.getLocation((MemberDeclaration)decl);
    }
    
    public Location getMethodLocation(final MethodDeclaration decl) {
        return this.getLocation((MemberDeclaration)decl);
    }
    
    public boolean hasDefaultConstructor(final TypeDeclaration t) {
        if (!(t instanceof ClassDeclaration)) {
            return false;
        }
        final ClassDeclaration c = (ClassDeclaration)t;
        for (final ConstructorDeclaration init : c.getConstructors()) {
            if (init.getParameters().isEmpty()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isStaticField(final FieldDeclaration f) {
        return this.hasModifier((Declaration)f, Modifier.STATIC);
    }
    
    public boolean isPublicMethod(final MethodDeclaration m) {
        return this.hasModifier((Declaration)m, Modifier.PUBLIC);
    }
    
    public boolean isPublicField(final FieldDeclaration f) {
        return this.hasModifier((Declaration)f, Modifier.PUBLIC);
    }
    
    public boolean isEnum(final TypeDeclaration t) {
        return t instanceof EnumDeclaration;
    }
    
    private Location getLocation(final MemberDeclaration decl) {
        return this.getLocation(decl.getDeclaringType().getQualifiedName() + '.' + decl.getSimpleName(), decl.getPosition());
    }
    
    private Location getLocation(final String name, final SourcePosition sp) {
        return new Location() {
            public String toString() {
                if (sp == null) {
                    return name + " (Unknown Source)";
                }
                return name + '(' + sp.file().getName() + ':' + sp.line() + ')';
            }
        };
    }
    
    static {
        (primitives = new HashMap<Class, PrimitiveType.Kind>()).put(Integer.TYPE, PrimitiveType.Kind.INT);
        APTNavigator.primitives.put(Byte.TYPE, PrimitiveType.Kind.BYTE);
        APTNavigator.primitives.put(Float.TYPE, PrimitiveType.Kind.FLOAT);
        APTNavigator.primitives.put(Boolean.TYPE, PrimitiveType.Kind.BOOLEAN);
        APTNavigator.primitives.put(Short.TYPE, PrimitiveType.Kind.SHORT);
        APTNavigator.primitives.put(Long.TYPE, PrimitiveType.Kind.LONG);
        APTNavigator.primitives.put(Double.TYPE, PrimitiveType.Kind.DOUBLE);
        APTNavigator.primitives.put(Character.TYPE, PrimitiveType.Kind.CHAR);
        DUMMY = (TypeMirror)new TypeMirror() {
            public void accept(final TypeVisitor v) {
                throw new IllegalStateException();
            }
        };
        SOURCE_POS_COMPARATOR = new Comparator<Declaration>() {
            public int compare(final Declaration d1, final Declaration d2) {
                if (d1 == d2) {
                    return 0;
                }
                final SourcePosition p1 = d1.getPosition();
                final SourcePosition p2 = d2.getPosition();
                if (p1 == null) {
                    return (p2 != null) ? 1 : 0;
                }
                if (p2 == null) {
                    return -1;
                }
                final int fileComp = p1.file().compareTo(p2.file());
                if (fileComp != 0) {
                    return fileComp;
                }
                long diff = p1.line() - p2.line();
                if (diff != 0L) {
                    return (diff < 0L) ? -1 : 1;
                }
                diff = Long.signum(p1.column() - p2.column());
                if (diff != 0L) {
                    return (int)diff;
                }
                return Long.signum(System.identityHashCode(d1) - System.identityHashCode(d2));
            }
        };
    }
}
