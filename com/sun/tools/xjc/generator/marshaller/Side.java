// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.marshaller;

import com.sun.tools.xjc.grammar.FieldItem;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ChoiceExp;

interface Side
{
    void onChoice(final ChoiceExp p0);
    
    void onZeroOrMore(final Expression p0);
    
    void onMarshallableObject();
    
    void onField(final FieldItem p0);
}
