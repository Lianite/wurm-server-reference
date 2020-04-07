// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.core;

import java.util.Collection;

public interface ElementInfo<T, C> extends Element<T, C>
{
    ElementPropertyInfo<T, C> getProperty();
    
    NonElement<T, C> getContentType();
    
    T getContentInMemoryType();
    
    T getType();
    
    ElementInfo<T, C> getSubstitutionHead();
    
    Collection<? extends ElementInfo<T, C>> getSubstitutionMembers();
}
