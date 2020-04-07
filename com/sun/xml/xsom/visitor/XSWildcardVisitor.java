// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.visitor;

import com.sun.xml.xsom.XSWildcard;

public interface XSWildcardVisitor
{
    void any(final XSWildcard.Any p0);
    
    void other(final XSWildcard.Other p0);
    
    void union(final XSWildcard.Union p0);
}
