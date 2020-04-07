// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar.xducer;

import com.sun.tools.xjc.generator.util.BlockReference;
import com.sun.msv.grammar.ValueExp;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JPrimitiveType;
import com.sun.tools.xjc.generator.field.FieldRenderer;
import com.sun.codemodel.JType;

public final class TypeAdaptedTransducer extends TransducerDecorator
{
    private final JType expectedType;
    private final boolean boxing;
    
    public static Transducer adapt(final Transducer xducer, final FieldRenderer fieldRenderer) {
        return adapt(xducer, fieldRenderer.getFieldUse().type);
    }
    
    public static Transducer adapt(final Transducer xducer, JType expectedType) {
        final JType t = xducer.getReturnType();
        if (t instanceof JPrimitiveType && expectedType instanceof JClass) {
            expectedType = ((JPrimitiveType)t).getWrapperClass();
            return (Transducer)new TypeAdaptedTransducer(xducer, expectedType);
        }
        if (t instanceof JClass && expectedType instanceof JPrimitiveType) {
            return (Transducer)new TypeAdaptedTransducer(xducer, expectedType);
        }
        return xducer;
    }
    
    private TypeAdaptedTransducer(final Transducer _xducer, final JType _expectedType) {
        super(_xducer);
        this.expectedType = _expectedType;
        this.boxing = (this.expectedType instanceof JClass);
    }
    
    public JType getReturnType() {
        return this.expectedType;
    }
    
    public JExpression generateSerializer(final JExpression value, final SerializerContext context) {
        if (this.boxing) {
            return super.generateSerializer(((JPrimitiveType)super.getReturnType()).unwrap(value), context);
        }
        return super.generateSerializer(((JPrimitiveType)this.expectedType).wrap(value), context);
    }
    
    public JExpression generateDeserializer(final JExpression literal, final DeserializerContext context) {
        if (this.boxing) {
            return ((JPrimitiveType)super.getReturnType()).wrap(super.generateDeserializer(literal, context));
        }
        return ((JPrimitiveType)this.expectedType).unwrap(super.generateDeserializer(literal, context));
    }
    
    public JExpression generateConstant(final ValueExp exp) {
        if (this.boxing) {
            return ((JPrimitiveType)super.getReturnType()).wrap(super.generateConstant(exp));
        }
        return ((JPrimitiveType)this.expectedType).unwrap(super.generateConstant(exp));
    }
    
    public void declareNamespace(final BlockReference body, final JExpression value, final SerializerContext context) {
        if (this.boxing) {
            super.declareNamespace(body, ((JPrimitiveType)super.getReturnType()).unwrap(value), context);
        }
        else {
            super.declareNamespace(body, ((JPrimitiveType)this.expectedType).wrap(value), context);
        }
    }
}
