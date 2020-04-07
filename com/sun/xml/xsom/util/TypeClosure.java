// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.util;

import com.sun.xml.xsom.XSType;

public class TypeClosure extends TypeSet
{
    private final TypeSet typeSet;
    
    public TypeClosure(final TypeSet typeSet) {
        this.typeSet = typeSet;
    }
    
    public boolean contains(final XSType type) {
        if (this.typeSet.contains(type)) {
            return true;
        }
        final XSType baseType = type.getBaseType();
        return baseType != null && this.contains(baseType);
    }
}
