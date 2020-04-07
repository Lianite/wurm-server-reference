// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.jxc.apt;

import java.util.Collection;
import com.sun.mirror.type.MirroredTypesException;
import java.lang.reflect.InvocationTargetException;
import com.sun.mirror.type.MirroredTypeException;
import com.sun.mirror.declaration.ParameterDeclaration;
import java.util.Iterator;
import java.util.List;
import com.sun.mirror.declaration.AnnotationMirror;
import java.util.ArrayList;
import com.sun.mirror.declaration.Declaration;
import com.sun.xml.bind.v2.model.annotation.LocatableAnnotation;
import com.sun.xml.bind.v2.model.annotation.Locatable;
import java.lang.annotation.Annotation;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.FieldDeclaration;
import com.sun.mirror.declaration.TypeDeclaration;
import com.sun.mirror.type.TypeMirror;
import com.sun.xml.bind.v2.model.annotation.AbstractInlineAnnotationReaderImpl;

public final class InlineAnnotationReaderImpl extends AbstractInlineAnnotationReaderImpl<TypeMirror, TypeDeclaration, FieldDeclaration, MethodDeclaration>
{
    public static final InlineAnnotationReaderImpl theInstance;
    private static final Annotation[] EMPTY_ANNOTATION;
    
    public <A extends Annotation> A getClassAnnotation(final Class<A> a, final TypeDeclaration clazz, final Locatable srcPos) {
        return LocatableAnnotation.create(clazz.getAnnotation((Class)a), srcPos);
    }
    
    public <A extends Annotation> A getFieldAnnotation(final Class<A> a, final FieldDeclaration f, final Locatable srcPos) {
        return LocatableAnnotation.create(f.getAnnotation((Class)a), srcPos);
    }
    
    public boolean hasFieldAnnotation(final Class<? extends Annotation> annotationType, final FieldDeclaration f) {
        return f.getAnnotation((Class)annotationType) != null;
    }
    
    public boolean hasClassAnnotation(final TypeDeclaration clazz, final Class<? extends Annotation> annotationType) {
        return clazz.getAnnotation((Class)annotationType) != null;
    }
    
    public Annotation[] getAllFieldAnnotations(final FieldDeclaration field, final Locatable srcPos) {
        return this.getAllAnnotations((Declaration)field, srcPos);
    }
    
    public <A extends Annotation> A getMethodAnnotation(final Class<A> a, final MethodDeclaration method, final Locatable srcPos) {
        return LocatableAnnotation.create(method.getAnnotation((Class)a), srcPos);
    }
    
    public boolean hasMethodAnnotation(final Class<? extends Annotation> a, final MethodDeclaration method) {
        return method.getAnnotation((Class)a) != null;
    }
    
    public Annotation[] getAllMethodAnnotations(final MethodDeclaration method, final Locatable srcPos) {
        return this.getAllAnnotations((Declaration)method, srcPos);
    }
    
    private Annotation[] getAllAnnotations(final Declaration decl, final Locatable srcPos) {
        final List<Annotation> r = new ArrayList<Annotation>();
        for (final AnnotationMirror m : decl.getAnnotationMirrors()) {
            try {
                final String fullName = m.getAnnotationType().getDeclaration().getQualifiedName();
                final Class<? extends Annotation> type = this.getClass().getClassLoader().loadClass(fullName).asSubclass(Annotation.class);
                final Annotation annotation = decl.getAnnotation((Class)type);
                if (annotation == null) {
                    continue;
                }
                r.add(LocatableAnnotation.create(annotation, srcPos));
            }
            catch (ClassNotFoundException ex) {}
        }
        return r.toArray(InlineAnnotationReaderImpl.EMPTY_ANNOTATION);
    }
    
    public <A extends Annotation> A getMethodParameterAnnotation(final Class<A> a, final MethodDeclaration m, final int paramIndex, final Locatable srcPos) {
        final ParameterDeclaration[] params = m.getParameters().toArray(new ParameterDeclaration[0]);
        return LocatableAnnotation.create(params[paramIndex].getAnnotation((Class)a), srcPos);
    }
    
    public <A extends Annotation> A getPackageAnnotation(final Class<A> a, final TypeDeclaration clazz, final Locatable srcPos) {
        return LocatableAnnotation.create(clazz.getPackage().getAnnotation((Class)a), srcPos);
    }
    
    public TypeMirror getClassValue(final Annotation a, final String name) {
        try {
            a.annotationType().getMethod(name, (Class<?>[])new Class[0]).invoke(a, new Object[0]);
            assert false;
            throw new IllegalStateException("should throw a MirroredTypeException");
        }
        catch (IllegalAccessException e) {
            throw new IllegalAccessError(e.getMessage());
        }
        catch (InvocationTargetException e2) {
            if (e2.getCause() instanceof MirroredTypeException) {
                final MirroredTypeException me = (MirroredTypeException)e2.getCause();
                return me.getTypeMirror();
            }
            throw new RuntimeException(e2);
        }
        catch (NoSuchMethodException e3) {
            throw new NoSuchMethodError(e3.getMessage());
        }
    }
    
    public TypeMirror[] getClassArrayValue(final Annotation a, final String name) {
        try {
            a.annotationType().getMethod(name, (Class<?>[])new Class[0]).invoke(a, new Object[0]);
            assert false;
            throw new IllegalStateException("should throw a MirroredTypesException");
        }
        catch (IllegalAccessException e) {
            throw new IllegalAccessError(e.getMessage());
        }
        catch (InvocationTargetException e2) {
            if (e2.getCause() instanceof MirroredTypesException) {
                final MirroredTypesException me = (MirroredTypesException)e2.getCause();
                final Collection<TypeMirror> r = (Collection<TypeMirror>)me.getTypeMirrors();
                return r.toArray(new TypeMirror[r.size()]);
            }
            throw new RuntimeException(e2);
        }
        catch (NoSuchMethodException e3) {
            throw new NoSuchMethodError(e3.getMessage());
        }
    }
    
    protected String fullName(final MethodDeclaration m) {
        return m.getDeclaringType().getQualifiedName() + '#' + m.getSimpleName();
    }
    
    static {
        theInstance = new InlineAnnotationReaderImpl();
        EMPTY_ANNOTATION = new Annotation[0];
    }
}
