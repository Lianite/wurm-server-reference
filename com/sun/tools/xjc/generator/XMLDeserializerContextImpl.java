// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator;

import com.sun.codemodel.JExpression;
import com.sun.tools.xjc.grammar.xducer.DeserializerContext;

public final class XMLDeserializerContextImpl implements DeserializerContext
{
    private final JExpression $context;
    
    public XMLDeserializerContextImpl(final JExpression _$context) {
        this.$context = _$context;
    }
    
    public JExpression ref() {
        return this.$context;
    }
    
    public JExpression addToIdTable(final JExpression literal) {
        return this.$context.invoke("addToIdTable").arg(literal);
    }
    
    public JExpression getObjectFromId(final JExpression literal) {
        return this.$context.invoke("getObjectFromId").arg(literal);
    }
    
    public JExpression getNamespaceContext() {
        return this.$context;
    }
}
