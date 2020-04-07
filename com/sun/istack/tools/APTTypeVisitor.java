// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.istack.tools;

import com.sun.mirror.type.WildcardType;
import com.sun.mirror.type.VoidType;
import com.sun.mirror.type.TypeVariable;
import com.sun.mirror.type.InterfaceType;
import com.sun.mirror.type.ClassType;
import com.sun.mirror.type.PrimitiveType;
import com.sun.mirror.type.ArrayType;
import com.sun.mirror.type.TypeMirror;

public abstract class APTTypeVisitor<T, P>
{
    public final T apply(final TypeMirror type, final P param) {
        if (type instanceof ArrayType) {
            return this.onArrayType((ArrayType)type, param);
        }
        if (type instanceof PrimitiveType) {
            return this.onPrimitiveType((PrimitiveType)type, param);
        }
        if (type instanceof ClassType) {
            return this.onClassType((ClassType)type, param);
        }
        if (type instanceof InterfaceType) {
            return this.onInterfaceType((InterfaceType)type, param);
        }
        if (type instanceof TypeVariable) {
            return this.onTypeVariable((TypeVariable)type, param);
        }
        if (type instanceof VoidType) {
            return this.onVoidType((VoidType)type, param);
        }
        if (type instanceof WildcardType) {
            return this.onWildcard((WildcardType)type, param);
        }
        assert false;
        throw new IllegalArgumentException();
    }
    
    protected abstract T onPrimitiveType(final PrimitiveType p0, final P p1);
    
    protected abstract T onArrayType(final ArrayType p0, final P p1);
    
    protected abstract T onClassType(final ClassType p0, final P p1);
    
    protected abstract T onInterfaceType(final InterfaceType p0, final P p1);
    
    protected abstract T onTypeVariable(final TypeVariable p0, final P p1);
    
    protected abstract T onVoidType(final VoidType p0, final P p1);
    
    protected abstract T onWildcard(final WildcardType p0, final P p1);
}
