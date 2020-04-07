// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar.xducer;

import com.sun.tools.xjc.generator.GeneratorContext;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;
import com.sun.tools.xjc.generator.util.BlockReference;
import com.sun.msv.grammar.ValueExp;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JType;

public abstract class DelayedTransducer implements Transducer
{
    private Transducer core;
    
    public DelayedTransducer() {
        this.core = null;
    }
    
    protected abstract Transducer create();
    
    private void update() {
        if (this.core == null) {
            this.core = this.create();
        }
    }
    
    public JType getReturnType() {
        this.update();
        return this.core.getReturnType();
    }
    
    public boolean isBuiltin() {
        return false;
    }
    
    public JExpression generateSerializer(final JExpression value, final SerializerContext context) {
        this.update();
        return this.core.generateSerializer(value, context);
    }
    
    public JExpression generateDeserializer(final JExpression literal, final DeserializerContext context) {
        this.update();
        return this.core.generateDeserializer(literal, context);
    }
    
    public JExpression generateConstant(final ValueExp exp) {
        this.update();
        return this.core.generateConstant(exp);
    }
    
    public void declareNamespace(final BlockReference body, final JExpression value, final SerializerContext context) {
        this.update();
        this.core.declareNamespace(body, value, context);
    }
    
    public boolean needsDelayedDeserialization() {
        this.update();
        return this.core.needsDelayedDeserialization();
    }
    
    public void populate(final AnnotatedGrammar grammar, final GeneratorContext context) {
        this.update();
        this.core.populate(grammar, context);
    }
}
