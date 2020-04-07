// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model;

public interface CPropertyVisitor<V>
{
    V onElement(final CElementPropertyInfo p0);
    
    V onAttribute(final CAttributePropertyInfo p0);
    
    V onValue(final CValuePropertyInfo p0);
    
    V onReference(final CReferencePropertyInfo p0);
}
