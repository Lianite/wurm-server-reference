// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.core;

import java.util.Set;

public interface RegistryInfo<T, C>
{
    Set<TypeInfo<T, C>> getReferences();
    
    C getClazz();
}
