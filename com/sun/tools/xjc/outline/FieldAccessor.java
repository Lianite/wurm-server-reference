// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.outline;

import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JVar;
import com.sun.codemodel.JBlock;

public interface FieldAccessor
{
    void toRawValue(final JBlock p0, final JVar p1);
    
    void fromRawValue(final JBlock p0, final String p1, final JExpression p2);
    
    void unsetValues(final JBlock p0);
    
    JExpression hasSetValue();
    
    FieldOutline owner();
    
    CPropertyInfo getPropertyInfo();
}
