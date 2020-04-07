// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.api;

import com.sun.mirror.util.SourcePosition;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.TypeDeclaration;
import com.sun.mirror.declaration.ParameterDeclaration;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.type.TypeMirror;

public final class Reference
{
    public final TypeMirror type;
    public final Declaration annotations;
    
    public Reference(final MethodDeclaration method) {
        this(method.getReturnType(), (Declaration)method);
    }
    
    public Reference(final ParameterDeclaration param) {
        this(param.getType(), (Declaration)param);
    }
    
    public Reference(final TypeDeclaration type, final AnnotationProcessorEnvironment env) {
        this((TypeMirror)env.getTypeUtils().getDeclaredType(type, new TypeMirror[0]), (Declaration)type);
    }
    
    public Reference(final TypeMirror type, final Declaration annotations) {
        if (type == null || annotations == null) {
            throw new IllegalArgumentException();
        }
        this.type = type;
        this.annotations = annotations;
    }
    
    public SourcePosition getPosition() {
        return this.annotations.getPosition();
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reference)) {
            return false;
        }
        final Reference that = (Reference)o;
        return this.annotations.equals(that.annotations) && this.type.equals(that.type);
    }
    
    public int hashCode() {
        return 29 * this.type.hashCode() + this.annotations.hashCode();
    }
}
