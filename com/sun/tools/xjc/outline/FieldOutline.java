// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.outline;

import com.sun.codemodel.JExpression;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.model.CPropertyInfo;

public interface FieldOutline
{
    ClassOutline parent();
    
    CPropertyInfo getPropertyInfo();
    
    JType getRawType();
    
    FieldAccessor create(final JExpression p0);
}
