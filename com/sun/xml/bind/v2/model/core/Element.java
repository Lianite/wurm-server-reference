// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.core;

import javax.xml.namespace.QName;

public interface Element<T, C> extends TypeInfo<T, C>
{
    QName getElementName();
    
    Element<T, C> getSubstitutionHead();
    
    ClassInfo<T, C> getScope();
}
