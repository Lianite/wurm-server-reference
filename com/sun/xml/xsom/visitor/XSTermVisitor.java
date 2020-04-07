// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.visitor;

import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSWildcard;

public interface XSTermVisitor
{
    void wildcard(final XSWildcard p0);
    
    void modelGroupDecl(final XSModelGroupDecl p0);
    
    void modelGroup(final XSModelGroup p0);
    
    void elementDecl(final XSElementDecl p0);
}
