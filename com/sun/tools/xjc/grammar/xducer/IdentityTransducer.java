// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar.xducer;

import com.sun.codemodel.JExpr;
import com.sun.msv.grammar.ValueExp;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JType;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JClass;

public class IdentityTransducer extends TransducerImpl
{
    private final JClass stringType;
    
    public IdentityTransducer(final JCodeModel codeModel) {
        this.stringType = codeModel.ref((IdentityTransducer.class$java$lang$String == null) ? (IdentityTransducer.class$java$lang$String = class$("java.lang.String")) : IdentityTransducer.class$java$lang$String);
    }
    
    public JType getReturnType() {
        return this.stringType;
    }
    
    public JExpression generateSerializer(final JExpression value, final SerializerContext context) {
        return value;
    }
    
    public JExpression generateDeserializer(final JExpression literal, final DeserializerContext context) {
        return literal;
    }
    
    public JExpression generateConstant(final ValueExp exp) {
        return JExpr.lit(this.obtainString(exp));
    }
}
