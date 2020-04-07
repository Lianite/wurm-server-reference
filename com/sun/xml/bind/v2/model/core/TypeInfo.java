// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.core;

import com.sun.xml.bind.v2.model.annotation.Locatable;

public interface TypeInfo<T, C> extends Locatable
{
    T getType();
    
    boolean canBeReferencedByIDREF();
}
