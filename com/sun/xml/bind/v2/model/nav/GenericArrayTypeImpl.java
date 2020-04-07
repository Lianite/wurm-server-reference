// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.nav;

import java.lang.reflect.Type;
import java.lang.reflect.GenericArrayType;

final class GenericArrayTypeImpl implements GenericArrayType
{
    private Type genericComponentType;
    
    GenericArrayTypeImpl(final Type ct) {
        assert ct != null;
        this.genericComponentType = ct;
    }
    
    public Type getGenericComponentType() {
        return this.genericComponentType;
    }
    
    public String toString() {
        final Type componentType = this.getGenericComponentType();
        final StringBuilder sb = new StringBuilder();
        if (componentType instanceof Class) {
            sb.append(((Class)componentType).getName());
        }
        else {
            sb.append(componentType.toString());
        }
        sb.append("[]");
        return sb.toString();
    }
    
    public boolean equals(final Object o) {
        if (o instanceof GenericArrayType) {
            final GenericArrayType that = (GenericArrayType)o;
            final Type thatComponentType = that.getGenericComponentType();
            return this.genericComponentType.equals(thatComponentType);
        }
        return false;
    }
    
    public int hashCode() {
        return this.genericComponentType.hashCode();
    }
}
