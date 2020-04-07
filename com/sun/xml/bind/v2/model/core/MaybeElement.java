// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.core;

import javax.xml.namespace.QName;

public interface MaybeElement<T, C> extends NonElement<T, C>
{
    boolean isElement();
    
    QName getElementName();
    
    Element<T, C> asElement();
}
