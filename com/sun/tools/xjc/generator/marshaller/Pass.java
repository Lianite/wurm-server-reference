// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.marshaller;

import com.sun.msv.grammar.ValueExp;
import com.sun.tools.xjc.grammar.PrimitiveItem;
import com.sun.msv.grammar.AttributeExp;
import com.sun.tools.xjc.grammar.ExternalItem;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;

interface Pass
{
    void build(final Expression p0);
    
    String getName();
    
    void onElement(final ElementExp p0);
    
    void onExternal(final ExternalItem p0);
    
    void onAttribute(final AttributeExp p0);
    
    void onPrimitive(final PrimitiveItem p0);
    
    void onValue(final ValueExp p0);
}
