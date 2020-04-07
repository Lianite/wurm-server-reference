// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.marshaller;

import com.sun.codemodel.JBlock;
import com.sun.tools.xjc.generator.util.BlockReference;
import com.sun.codemodel.JExpression;
import com.sun.tools.xjc.generator.field.FieldRenderer;

public interface FieldMarshallerGenerator
{
    FieldRenderer owner();
    
    JExpression peek(final boolean p0);
    
    void increment(final BlockReference p0);
    
    JExpression hasMore();
    
    FieldMarshallerGenerator clone(final JBlock p0, final String p1);
}
