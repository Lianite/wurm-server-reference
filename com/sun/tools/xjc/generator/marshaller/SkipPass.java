// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.marshaller;

import com.sun.msv.grammar.ValueExp;
import com.sun.tools.xjc.grammar.PrimitiveItem;
import com.sun.msv.grammar.AttributeExp;
import com.sun.tools.xjc.grammar.ExternalItem;
import com.sun.msv.grammar.ElementExp;

public class SkipPass extends AbstractPassImpl
{
    SkipPass(final Context _context) {
        super(_context, "Skip");
    }
    
    public void onElement(final ElementExp exp) {
        this.context.skipPass.build(exp.contentModel);
    }
    
    public void onExternal(final ExternalItem item) {
        this.increment();
    }
    
    public void onAttribute(final AttributeExp exp) {
        this.context.skipPass.build(exp.exp);
    }
    
    public void onPrimitive(final PrimitiveItem exp) {
        this.increment();
    }
    
    public void onValue(final ValueExp exp) {
    }
    
    private void increment() {
        final FieldMarshallerGenerator fmg = this.context.getCurrentFieldMarshaller();
        fmg.increment(this.context.getCurrentBlock());
    }
}
