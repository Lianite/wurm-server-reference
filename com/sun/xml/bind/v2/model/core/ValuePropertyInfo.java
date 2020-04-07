// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.core;

public interface ValuePropertyInfo<T, C> extends PropertyInfo<T, C>, NonElementRef<T, C>
{
    Adapter<T, C> getAdapter();
}
