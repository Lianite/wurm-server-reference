// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar;

public interface JavaItemVisitor
{
    Object onClass(final ClassItem p0);
    
    Object onField(final FieldItem p0);
    
    Object onIgnore(final IgnoreItem p0);
    
    Object onInterface(final InterfaceItem p0);
    
    Object onPrimitive(final PrimitiveItem p0);
    
    Object onExternal(final ExternalItem p0);
    
    Object onSuper(final SuperClassItem p0);
}
