// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar.xducer;

import com.sun.codemodel.JExpression;

public interface DeserializerContext
{
    JExpression addToIdTable(final JExpression p0);
    
    JExpression getObjectFromId(final JExpression p0);
    
    JExpression getNamespaceContext();
}
