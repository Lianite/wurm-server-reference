// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.visitor;

import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSWildcard;

public interface XSTermFunctionWithParam<T, P>
{
    T wildcard(final XSWildcard p0, final P p1);
    
    T modelGroupDecl(final XSModelGroupDecl p0, final P p1);
    
    T modelGroup(final XSModelGroup p0, final P p1);
    
    T elementDecl(final XSElementDecl p0, final P p1);
}
