// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar.xducer;

import com.sun.msv.datatype.SerializationContext;
import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.grammar.ValueExp;
import com.sun.tools.xjc.grammar.id.SymbolSpace;
import com.sun.codemodel.JExpression;
import com.sun.tools.xjc.generator.util.BlockReference;
import com.sun.tools.xjc.generator.GeneratorContext;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;

public abstract class TransducerImpl implements Transducer
{
    public void populate(final AnnotatedGrammar grammar, final GeneratorContext context) {
    }
    
    public void declareNamespace(final BlockReference body, final JExpression value, final SerializerContext context) {
    }
    
    public boolean needsDelayedDeserialization() {
        return false;
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
    
    public String toString() {
        String className = this.getClass().getName();
        final int idx = className.lastIndexOf(46);
        if (idx >= 0) {
            className = className.substring(idx + 1);
        }
        return className + ":" + this.getReturnType().name();
    }
    
    protected final String obtainString(final ValueExp exp) {
        return ((XSDatatype)exp.dt).convertToLexicalValue(exp.value, (SerializationContext)null);
    }
}
