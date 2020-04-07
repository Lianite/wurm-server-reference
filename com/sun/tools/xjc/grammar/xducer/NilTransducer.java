// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar.xducer;

import com.sun.msv.grammar.ValueExp;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JType;
import com.sun.codemodel.JCodeModel;

public class NilTransducer extends TransducerImpl
{
    private final JCodeModel codeModel;
    
    public NilTransducer(final JCodeModel _codeModel) {
        this.codeModel = _codeModel;
    }
    
    public JType getReturnType() {
        return this.codeModel.NULL;
    }
    
    public JExpression generateSerializer(final JExpression value, final SerializerContext context) {
        return JExpr.lit("true");
    }
    
    public JExpression generateDeserializer(final JExpression literal, final DeserializerContext context) {
        return JExpr._null();
    }
    
    public JExpression generateConstant(final ValueExp exp) {
        throw new UnsupportedOperationException();
    }
}
