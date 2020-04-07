// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar.xducer;

import com.sun.msv.grammar.ValueExp;
import com.sun.tools.xjc.generator.util.BlockReference;
import com.sun.codemodel.JExpression;
import com.sun.tools.xjc.generator.GeneratorContext;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;
import com.sun.tools.xjc.grammar.id.SymbolSpace;
import com.sun.codemodel.JType;

public class FacadeTransducer implements Transducer
{
    private final Transducer marshaller;
    private final Transducer unmarshaller;
    
    public FacadeTransducer(final Transducer _marshaller, final Transducer _unmarshaller) {
        this.marshaller = _marshaller;
        this.unmarshaller = _unmarshaller;
    }
    
    public JType getReturnType() {
        return this.marshaller.getReturnType();
    }
    
    public boolean isID() {
        return false;
    }
    
    public SymbolSpace getIDSymbolSpace() {
        return null;
    }
    
    public boolean isBuiltin() {
        return false;
    }
    
    public void populate(final AnnotatedGrammar grammar, final GeneratorContext context) {
        this.marshaller.populate(grammar, context);
        this.unmarshaller.populate(grammar, context);
    }
    
    public JExpression generateSerializer(final JExpression value, final SerializerContext context) {
        return this.marshaller.generateSerializer(value, context);
    }
    
    public void declareNamespace(final BlockReference body, final JExpression value, final SerializerContext context) {
        this.marshaller.declareNamespace(body, value, context);
    }
    
    public JExpression generateDeserializer(final JExpression literal, final DeserializerContext context) {
        return this.unmarshaller.generateDeserializer(literal, context);
    }
    
    public boolean needsDelayedDeserialization() {
        return this.unmarshaller.needsDelayedDeserialization();
    }
    
    public JExpression generateConstant(final ValueExp exp) {
        return this.unmarshaller.generateConstant(exp);
    }
}
