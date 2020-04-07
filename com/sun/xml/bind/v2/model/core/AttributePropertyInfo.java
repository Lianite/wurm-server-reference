// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.core;

import javax.xml.namespace.QName;

public interface AttributePropertyInfo<T, C> extends PropertyInfo<T, C>, NonElementRef<T, C>
{
    NonElement<T, C> getTarget();
    
    boolean isRequired();
    
    QName getXmlName();
    
    Adapter<T, C> getAdapter();
}
