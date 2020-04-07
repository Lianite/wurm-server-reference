// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar.xducer;

import com.sun.msv.grammar.ValueExp;
import com.sun.tools.xjc.generator.util.BlockReference;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JType;
import com.sun.xml.bind.JAXBAssertionError;
import com.sun.codemodel.JPrimitiveType;

public class CastTranducer extends TransducerDecorator
{
    private final JPrimitiveType type;
    
    public CastTranducer(final JPrimitiveType _type, final Transducer _core) {
        super(_core);
        this.type = _type;
        if (!super.getReturnType().isPrimitive()) {
            throw new JAXBAssertionError();
        }
    }
    
    public JType getReturnType() {
        return this.type;
    }
    
    public JExpression generateSerializer(final JExpression value, final SerializerContext context) {
        return super.generateSerializer((JExpression)JExpr.cast(super.getReturnType(), value), context);
    }
    
    public JExpression generateDeserializer(final JExpression literal, final DeserializerContext context) {
        return JExpr.cast(this.type, super.generateDeserializer(literal, context));
    }
    
    public void declareNamespace(final BlockReference body, final JExpression value, final SerializerContext context) {
        super.declareNamespace(body, (JExpression)JExpr.cast(super.getReturnType(), value), context);
    }
    
    public JExpression generateConstant(final ValueExp exp) {
        return JExpr.cast(this.type, super.generateConstant(exp));
    }
}
