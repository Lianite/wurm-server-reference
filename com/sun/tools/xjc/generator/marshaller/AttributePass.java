// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.marshaller;

import com.sun.msv.grammar.ValueExp;
import com.sun.tools.xjc.grammar.PrimitiveItem;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpression;
import com.sun.msv.grammar.NameClassAndExpression;
import com.sun.tools.xjc.generator.XmlNameStoreAlgorithm;
import com.sun.msv.grammar.AttributeExp;
import com.sun.tools.xjc.grammar.ExternalItem;
import com.sun.msv.grammar.ElementExp;

class AttributePass extends AbstractPassImpl
{
    AttributePass(final Context _context) {
        super(_context, "Attributes");
    }
    
    public void onElement(final ElementExp exp) {
        if (this.context.isInside()) {
            this.context.skipPass.build(exp.contentModel);
        }
    }
    
    public void onExternal(final ExternalItem item) {
        final FieldMarshallerGenerator fmg = this.context.getCurrentFieldMarshaller();
        fmg.increment(this.context.getCurrentBlock());
    }
    
    public void onAttribute(final AttributeExp exp) {
        final JBlock block = this.getBlock(true);
        final XmlNameStoreAlgorithm algorithm = XmlNameStoreAlgorithm.get((NameClassAndExpression)exp);
        block.invoke(this.context.$serializer, "startAttribute").arg(algorithm.getNamespaceURI()).arg(algorithm.getLocalPart());
        this.context.bodyPass.build(exp.exp);
        block.invoke(this.context.$serializer, "endAttribute");
    }
    
    public void onPrimitive(final PrimitiveItem exp) {
        final FieldMarshallerGenerator fmg = this.context.getCurrentFieldMarshaller();
        fmg.increment(this.context.getCurrentBlock());
    }
    
    public void onValue(final ValueExp exp) {
    }
}
