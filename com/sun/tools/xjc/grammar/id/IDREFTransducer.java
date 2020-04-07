// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar.id;

import com.sun.msv.grammar.ValueExp;
import com.sun.tools.xjc.generator.util.WhitespaceNormalizer;
import com.sun.tools.xjc.grammar.xducer.DeserializerContext;
import com.sun.codemodel.JExpr;
import com.sun.tools.xjc.grammar.xducer.SerializerContext;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JType;
import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.grammar.xducer.TransducerImpl;

public class IDREFTransducer extends TransducerImpl
{
    private final JCodeModel codeModel;
    private final SymbolSpace symbolSpace;
    private final boolean whitespaceNormalization;
    static /* synthetic */ Class class$com$sun$xml$bind$marshaller$IdentifiableObject;
    
    public IDREFTransducer(final JCodeModel _codeModel, final SymbolSpace _symbolSpace, final boolean _whitespaceNormalization) {
        this.codeModel = _codeModel;
        this.symbolSpace = _symbolSpace;
        this.whitespaceNormalization = _whitespaceNormalization;
    }
    
    public SymbolSpace getIDSymbolSpace() {
        return this.symbolSpace;
    }
    
    public JType getReturnType() {
        return this.symbolSpace.getType();
    }
    
    public JExpression generateSerializer(final JExpression literal, final SerializerContext context) {
        return context.onIDREF((JExpression)JExpr.cast(this.codeModel.ref((IDREFTransducer.class$com$sun$xml$bind$marshaller$IdentifiableObject == null) ? (IDREFTransducer.class$com$sun$xml$bind$marshaller$IdentifiableObject = class$("com.sun.xml.bind.marshaller.IdentifiableObject")) : IDREFTransducer.class$com$sun$xml$bind$marshaller$IdentifiableObject), literal));
    }
    
    public JExpression generateDeserializer(final JExpression literal, final DeserializerContext context) {
        return JExpr.cast(this.symbolSpace.getType(), context.getObjectFromId(this.whitespaceNormalization ? WhitespaceNormalizer.COLLAPSE.generate(this.codeModel, literal) : literal));
    }
    
    public boolean needsDelayedDeserialization() {
        return true;
    }
    
    public String toString() {
        return "IDREFTransducer:" + this.symbolSpace.toString();
    }
    
    public JExpression generateConstant(final ValueExp exp) {
        throw new UnsupportedOperationException(Messages.format("IDREFTransducer.ConstantIDREFError"));
    }
    
    static /* synthetic */ Class class$(final String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x) {
            throw new NoClassDefFoundError(x.getMessage());
        }
    }
}
