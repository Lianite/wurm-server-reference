// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom;

public interface XSComplexType extends XSType, XSAttContainer
{
    boolean isAbstract();
    
    boolean isFinal(final int p0);
    
    boolean isSubstitutionProhibited(final int p0);
    
    XSElementDecl getScope();
    
    XSContentType getContentType();
    
    XSContentType getExplicitContent();
    
    boolean isMixed();
}
