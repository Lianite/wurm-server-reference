// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.core;

public interface NonElementRef<T, C>
{
    NonElement<T, C> getTarget();
    
    PropertyInfo<T, C> getSource();
}
