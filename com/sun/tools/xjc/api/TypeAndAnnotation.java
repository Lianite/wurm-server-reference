// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.api;

import com.sun.codemodel.JAnnotatable;
import com.sun.codemodel.JType;

public interface TypeAndAnnotation
{
    JType getTypeClass();
    
    void annotate(final JAnnotatable p0);
    
    boolean equals(final Object p0);
}
