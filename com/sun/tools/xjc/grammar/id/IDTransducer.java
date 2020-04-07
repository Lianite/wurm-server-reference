// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar.id;

import com.sun.codemodel.JExpr;
import com.sun.tools.xjc.grammar.xducer.SerializerContext;
import com.sun.tools.xjc.grammar.xducer.DeserializerContext;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.grammar.xducer.IdentityTransducer;

public class IDTransducer extends IdentityTransducer
{
    private final SymbolSpace symbolSpace;
    
    public IDTransducer(final JCodeModel _codeModel, final SymbolSpace _symbolSpace) {
        super(_codeModel);
        this.symbolSpace = _symbolSpace;
    }
    
    public boolean isID() {
        return true;
    }
    
    public SymbolSpace getIDSymbolSpace() {
        return this.symbolSpace;
    }
    
    public JExpression generateDeserializer(final JExpression literal, final DeserializerContext context) {
        return context.addToIdTable(literal);
    }
    
    public JExpression generateSerializer(final JExpression value, final SerializerContext context) {
        return context.onID(JExpr._this(), value);
    }
}
