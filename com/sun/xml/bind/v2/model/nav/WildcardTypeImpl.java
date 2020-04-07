// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.nav;

import java.util.Arrays;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

final class WildcardTypeImpl implements WildcardType
{
    private final Type[] ub;
    private final Type[] lb;
    
    public WildcardTypeImpl(final Type[] ub, final Type[] lb) {
        this.ub = ub;
        this.lb = lb;
    }
    
    public Type[] getUpperBounds() {
        return this.ub;
    }
    
    public Type[] getLowerBounds() {
        return this.lb;
    }
    
    public int hashCode() {
        return Arrays.hashCode(this.lb) ^ Arrays.hashCode(this.ub);
    }
    
    public boolean equals(final Object obj) {
        if (obj instanceof WildcardType) {
            final WildcardType that = (WildcardType)obj;
            return Arrays.equals(that.getLowerBounds(), this.lb) && Arrays.equals(that.getUpperBounds(), this.ub);
        }
        return false;
    }
}
