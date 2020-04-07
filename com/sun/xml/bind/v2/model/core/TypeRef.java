// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.core;

import javax.xml.namespace.QName;

public interface TypeRef<T, C> extends NonElementRef<T, C>
{
    QName getTagName();
    
    boolean isNillable();
    
    String getDefaultValue();
}
