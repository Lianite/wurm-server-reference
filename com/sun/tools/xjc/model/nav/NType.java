// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model.nav;

import com.sun.codemodel.JType;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.outline.Outline;

public interface NType
{
    JType toType(final Outline p0, final Aspect p1);
    
    boolean isBoxedType();
    
    String fullName();
}
