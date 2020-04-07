// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom;

import java.util.Set;

public interface XSElementDecl extends XSDeclaration, XSTerm
{
    XSType getType();
    
    boolean isNillable();
    
    XSElementDecl getSubstAffiliation();
    
    boolean isSubstitutionExcluded(final int p0);
    
    boolean isSubstitutionDisallowed(final int p0);
    
    boolean isAbstract();
    
    XSElementDecl[] listSubstitutables();
    
    Set getSubstitutables();
    
    boolean canBeSubstitutedBy(final XSElementDecl p0);
    
    String getDefaultValue();
    
    String getFixedValue();
}
