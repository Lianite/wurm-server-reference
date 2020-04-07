// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar.xducer;

import com.sun.codemodel.JExpression;
import com.sun.codemodel.JBlock;

public interface SerializerContext
{
    void declareNamespace(final JBlock p0, final JExpression p1, final JExpression p2, final JExpression p3);
    
    JExpression getNamespaceContext();
    
    JExpression onID(final JExpression p0, final JExpression p1);
    
    JExpression onIDREF(final JExpression p0);
}
