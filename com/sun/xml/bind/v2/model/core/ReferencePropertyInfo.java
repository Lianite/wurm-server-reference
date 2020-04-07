// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.core;

import javax.xml.namespace.QName;
import java.util.Collection;
import java.util.Set;

public interface ReferencePropertyInfo<T, C> extends PropertyInfo<T, C>
{
    Set<? extends Element<T, C>> getElements();
    
    Collection<? extends TypeInfo<T, C>> ref();
    
    QName getXmlName();
    
    boolean isCollectionNillable();
    
    boolean isCollectionRequired();
    
    boolean isMixed();
    
    WildcardMode getWildcard();
    
    C getDOMHandler();
    
    Adapter<T, C> getAdapter();
}
