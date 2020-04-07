// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.core;

import javax.xml.namespace.QName;

public interface MapPropertyInfo<T, C> extends PropertyInfo<T, C>
{
    QName getXmlName();
    
    boolean isCollectionNillable();
    
    NonElement<T, C> getKeyType();
    
    NonElement<T, C> getValueType();
}
