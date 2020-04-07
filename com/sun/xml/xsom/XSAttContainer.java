// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom;

import java.util.Iterator;

public interface XSAttContainer extends XSDeclaration
{
    XSWildcard getAttributeWildcard();
    
    XSAttributeUse getAttributeUse(final String p0, final String p1);
    
    Iterator iterateAttributeUses();
    
    XSAttributeUse getDeclaredAttributeUse(final String p0, final String p1);
    
    Iterator iterateDeclaredAttributeUses();
    
    Iterator iterateAttGroups();
}
