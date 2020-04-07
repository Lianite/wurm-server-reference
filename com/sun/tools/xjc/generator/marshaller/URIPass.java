// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.marshaller;

import com.sun.msv.grammar.ValueExp;
import com.sun.tools.xjc.grammar.xducer.Transducer;
import com.sun.tools.xjc.grammar.xducer.SerializerContext;
import com.sun.tools.xjc.grammar.xducer.TypeAdaptedTransducer;
import com.sun.tools.xjc.grammar.PrimitiveItem;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JStringLiteral;
import com.sun.msv.grammar.NameClassAndExpression;
import com.sun.tools.xjc.generator.XmlNameStoreAlgorithm;
import com.sun.msv.grammar.AttributeExp;
import com.sun.tools.xjc.grammar.ExternalItem;
import com.sun.msv.grammar.ElementExp;

class URIPass extends AbstractPassImpl
{
    URIPass(final Context _context) {
        super(_context, "URIs");
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
        final XmlNameStoreAlgorithm algorithm = XmlNameStoreAlgorithm.get((NameClassAndExpression)exp);
        final JExpression namespaceURI = algorithm.getNamespaceURI();
        if (!(namespaceURI instanceof JStringLiteral) || !((JStringLiteral)namespaceURI).str.equals("")) {
            this.getBlock(true).invoke(this.context.$serializer.invoke("getNamespaceContext"), "declareNamespace").arg(namespaceURI).arg(JExpr._null()).arg(JExpr.TRUE);
        }
        this.context.uriPass.build(exp.exp);
    }
    
    public void onPrimitive(final PrimitiveItem exp) {
        final FieldMarshallerGenerator fmg = this.context.getCurrentFieldMarshaller();
        final Transducer xducer = TypeAdaptedTransducer.adapt(exp.xducer, fmg.owner().getFieldUse().type);
        xducer.declareNamespace(this.context.getCurrentBlock(), (JExpression)JExpr.cast(xducer.getReturnType(), fmg.peek(false)), (SerializerContext)this.context);
        fmg.increment(this.context.getCurrentBlock());
    }
    
    public void onValue(final ValueExp exp) {
    }
}
