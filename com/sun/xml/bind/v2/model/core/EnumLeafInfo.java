// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.core;

public interface EnumLeafInfo<T, C> extends LeafInfo<T, C>
{
    C getClazz();
    
    NonElement<T, C> getBaseType();
    
    Iterable<? extends EnumConstant> getConstants();
}
