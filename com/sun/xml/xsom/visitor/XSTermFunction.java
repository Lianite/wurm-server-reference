// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.visitor;

import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSWildcard;

public interface XSTermFunction
{
    Object wildcard(final XSWildcard p0);
    
    Object modelGroupDecl(final XSModelGroupDecl p0);
    
    Object modelGroup(final XSModelGroup p0);
    
    Object elementDecl(final XSElementDecl p0);
}
