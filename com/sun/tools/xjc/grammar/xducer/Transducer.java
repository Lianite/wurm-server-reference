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

public interface Transducer
{
    JType getReturnType();
    
    void populate(final AnnotatedGrammar p0, final GeneratorContext p1);
    
    JExpression generateSerializer(final JExpression p0, final SerializerContext p1);
    
    void declareNamespace(final BlockReference p0, final JExpression p1, final SerializerContext p2);
    
    JExpression generateDeserializer(final JExpression p0, final DeserializerContext p1);
    
    boolean needsDelayedDeserialization();
    
    boolean isID();
    
    SymbolSpace getIDSymbolSpace();
    
    boolean isBuiltin();
    
    JExpression generateConstant(final ValueExp p0);
}
