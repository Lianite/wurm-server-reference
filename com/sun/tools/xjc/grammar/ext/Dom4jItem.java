// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar.ext;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JVar;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.tools.xjc.generator.marshaller.FieldMarshallerGenerator;
import com.sun.codemodel.JBlock;
import com.sun.tools.xjc.generator.GeneratorContext;
import org.xml.sax.Locator;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;
import com.sun.msv.grammar.NameClass;
import com.sun.codemodel.JType;

class Dom4jItem extends AbstractDOMItem
{
    private final JType elementType;
    public static DOMItemFactory factory;
    
    public Dom4jItem(final NameClass _elementName, final AnnotatedGrammar grammar, final Locator loc) {
        super(_elementName, grammar, loc);
        this.elementType = this.createPhantomType("org.dom4j.Element");
    }
    
    public void generateMarshaller(final GeneratorContext context, JBlock block, final FieldMarshallerGenerator fmg, final JExpression $context) {
        block = block.block();
        block.directStatement("org.dom4j.io.SAXWriter w = new org.dom4j.io.SAXWriter();");
        final JExpression $w = JExpr.direct("w");
        block.invoke($w, "setContentHandler").arg(JExpr._new(context.getRuntime((Dom4jItem.class$com$sun$tools$xjc$runtime$ContentHandlerAdaptor == null) ? (Dom4jItem.class$com$sun$tools$xjc$runtime$ContentHandlerAdaptor = class$("com.sun.tools.xjc.runtime.ContentHandlerAdaptor")) : Dom4jItem.class$com$sun$tools$xjc$runtime$ContentHandlerAdaptor)).arg($context));
        block.invoke($w, "write").arg(JExpr.cast(this.elementType, fmg.peek(true)));
    }
    
    public JExpression generateUnmarshaller(final GeneratorContext context, final JExpression $context, final JBlock block, final JExpression memento, final JVar $uri, final JVar $local, final JVar $qname, final JVar $atts) {
        final JClass handlerClass = context.getRuntime((Dom4jItem.class$com$sun$tools$xjc$runtime$Dom4jUnmarshallingEventHandler == null) ? (Dom4jItem.class$com$sun$tools$xjc$runtime$Dom4jUnmarshallingEventHandler = class$("com.sun.tools.xjc.runtime.Dom4jUnmarshallingEventHandler")) : Dom4jItem.class$com$sun$tools$xjc$runtime$Dom4jUnmarshallingEventHandler);
        final JVar $u = block.decl(handlerClass, "u", JExpr._new(handlerClass).arg($context));
        block.invoke($context, "pushContentHandler").arg($u).arg(memento);
        block.invoke($context.invoke("getCurrentHandler"), "enterElement").arg($uri).arg($local).arg($qname).arg($atts);
        return $u.invoke("getOwner");
    }
    
    public JType getType() {
        return this.elementType;
    }
    
    public JExpression createRootUnmarshaller(final GeneratorContext context, final JVar $unmarshallingContext) {
        final JClass handlerClass = context.getRuntime((Dom4jItem.class$com$sun$tools$xjc$runtime$Dom4jUnmarshallingEventHandler == null) ? (Dom4jItem.class$com$sun$tools$xjc$runtime$Dom4jUnmarshallingEventHandler = class$("com.sun.tools.xjc.runtime.Dom4jUnmarshallingEventHandler")) : Dom4jItem.class$com$sun$tools$xjc$runtime$Dom4jUnmarshallingEventHandler);
        return JExpr._new(handlerClass).arg($unmarshallingContext);
    }
    
    static {
        Dom4jItem.factory = (DOMItemFactory)new Dom4jItem$1();
    }
}
