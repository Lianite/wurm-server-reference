// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model.nav;

import com.sun.codemodel.JClass;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.outline.Outline;

public interface NClass extends NType
{
    JClass toType(final Outline p0, final Aspect p1);
    
    boolean isAbstract();
}
