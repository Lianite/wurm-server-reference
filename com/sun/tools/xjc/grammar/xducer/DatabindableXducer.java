// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar.xducer;

import com.sun.msv.grammar.ValueExp;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JType;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JClass;
import com.sun.msv.datatype.DatabindableDatatype;

public class DatabindableXducer extends TransducerImpl
{
    private final DatabindableDatatype dt;
    private final JClass returnType;
    
    public DatabindableXducer(final JCodeModel writer, final DatabindableDatatype _dt) {
        this.dt = _dt;
        final String name = this.dt.getJavaObjectType().getName();
        final int idx = name.lastIndexOf(".");
        if (idx < 0) {
            this.returnType = writer._package("").ref(name);
        }
        else {
            this.returnType = writer._package(name.substring(0, idx)).ref(name.substring(idx + 1));
        }
    }
    
    public JType getReturnType() {
        return this.returnType;
    }
    
    public JExpression generateSerializer(final JExpression value, final SerializerContext context) {
        throw new UnsupportedOperationException("TODO");
    }
    
    public JExpression generateDeserializer(final JExpression value, final DeserializerContext context) {
        throw new UnsupportedOperationException("TODO");
    }
    
    public JExpression generateConstant(final ValueExp exp) {
        throw new UnsupportedOperationException("TODO");
    }
}
