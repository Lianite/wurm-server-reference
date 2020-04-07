// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar.xducer;

import com.sun.msv.grammar.ValueExp;
import com.sun.tools.xjc.grammar.id.SymbolSpace;
import com.sun.tools.xjc.generator.util.BlockReference;
import com.sun.codemodel.JExpression;
import com.sun.tools.xjc.generator.GeneratorContext;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;
import com.sun.codemodel.JType;

public abstract class TransducerDecorator implements Transducer
{
    protected final Transducer core;
    
    protected TransducerDecorator(final Transducer _core) {
        this.core = _core;
    }
    
    public JType getReturnType() {
        return this.core.getReturnType();
    }
    
    public boolean isBuiltin() {
        return false;
    }
    
    public void populate(final AnnotatedGrammar grammar, final GeneratorContext context) {
        this.core.populate(grammar, context);
    }
    
    public JExpression generateSerializer(final JExpression value, final SerializerContext context) {
        return this.core.generateSerializer(value, context);
    }
    
    public void declareNamespace(final BlockReference body, final JExpression value, final SerializerContext context) {
        this.core.declareNamespace(body, value, context);
    }
    
    public JExpression generateDeserializer(final JExpression literal, final DeserializerContext context) {
        return this.core.generateDeserializer(literal, context);
    }
    
    public boolean needsDelayedDeserialization() {
        return this.core.needsDelayedDeserialization();
    }
    
    public boolean isID() {
        return this.core.isID();
    }
    
    public SymbolSpace getIDSymbolSpace() {
        return this.core.getIDSymbolSpace();
    }
    
    public JExpression generateConstant(final ValueExp exp) {
        return this.core.generateConstant(exp);
    }
}
