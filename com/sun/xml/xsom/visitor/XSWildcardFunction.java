// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.visitor;

import com.sun.xml.xsom.XSWildcard;

public interface XSWildcardFunction
{
    Object any(final XSWildcard.Any p0);
    
    Object other(final XSWildcard.Other p0);
    
    Object union(final XSWildcard.Union p0);
}
