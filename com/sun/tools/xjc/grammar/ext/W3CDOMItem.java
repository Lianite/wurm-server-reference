// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar.ext;

import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JAssignmentTarget;
import com.sun.codemodel.JVar;
import com.sun.codemodel.JType;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.tools.xjc.generator.marshaller.FieldMarshallerGenerator;
import com.sun.codemodel.JBlock;
import com.sun.tools.xjc.generator.GeneratorContext;
import org.xml.sax.Locator;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;
import com.sun.msv.grammar.NameClass;

class W3CDOMItem extends AbstractDOMItem
{
    public static DOMItemFactory factory;
    
    public W3CDOMItem(final NameClass _elementName, final AnnotatedGrammar grammar, final Locator loc) {
        super(_elementName, grammar, loc);
    }
    
    public void generateMarshaller(final GeneratorContext context, final JBlock block, final FieldMarshallerGenerator fmg, final JExpression $context) {
        block.invoke(JExpr._new(this.codeModel.ref((W3CDOMItem.class$com$sun$xml$bind$unmarshaller$DOMScanner == null) ? (W3CDOMItem.class$com$sun$xml$bind$unmarshaller$DOMScanner = class$("com.sun.xml.bind.unmarshaller.DOMScanner")) : W3CDOMItem.class$com$sun$xml$bind$unmarshaller$DOMScanner)), "parse").arg(JExpr.cast(this.codeModel.ref((W3CDOMItem.class$org$w3c$dom$Element == null) ? (W3CDOMItem.class$org$w3c$dom$Element = class$("org.w3c.dom.Element")) : W3CDOMItem.class$org$w3c$dom$Element), fmg.peek(true))).arg(JExpr._new(context.getRuntime((W3CDOMItem.class$com$sun$tools$xjc$runtime$ContentHandlerAdaptor == null) ? (W3CDOMItem.class$com$sun$tools$xjc$runtime$ContentHandlerAdaptor = class$("com.sun.tools.xjc.runtime.ContentHandlerAdaptor")) : W3CDOMItem.class$com$sun$tools$xjc$runtime$ContentHandlerAdaptor)).arg($context));
    }
    
    public JExpression generateUnmarshaller(final GeneratorContext context, final JExpression $context, JBlock block, final JExpression memento, final JVar $uri, final JVar $local, final JVar $qname, final JVar $atts) {
        final JVar $v = block.decl(this.codeModel.ref((W3CDOMItem.class$org$w3c$dom$Element == null) ? (W3CDOMItem.class$org$w3c$dom$Element = class$("org.w3c.dom.Element")) : W3CDOMItem.class$org$w3c$dom$Element), "ur", JExpr._null());
        final JClass handlerClass = context.getRuntime((W3CDOMItem.class$com$sun$tools$xjc$runtime$W3CDOMUnmarshallingEventHandler == null) ? (W3CDOMItem.class$com$sun$tools$xjc$runtime$W3CDOMUnmarshallingEventHandler = class$("com.sun.tools.xjc.runtime.W3CDOMUnmarshallingEventHandler")) : W3CDOMItem.class$com$sun$tools$xjc$runtime$W3CDOMUnmarshallingEventHandler);
        final JTryBlock tryBlock = block._try();
        block = tryBlock.body();
        final JVar $u = block.decl(handlerClass, "u", JExpr._new(handlerClass).arg($context));
        block.invoke($context, "pushContentHandler").arg($u).arg(memento);
        block.invoke($context.invoke("getCurrentHandler"), "enterElement").arg($uri).arg($local).arg($qname).arg($atts);
        block.assign($v, $u.invoke("getOwner"));
        final JCatchBlock catchBlock = tryBlock._catch(this.codeModel.ref((W3CDOMItem.class$javax$xml$parsers$ParserConfigurationException == null) ? (W3CDOMItem.class$javax$xml$parsers$ParserConfigurationException = class$("javax.xml.parsers.ParserConfigurationException")) : W3CDOMItem.class$javax$xml$parsers$ParserConfigurationException));
        catchBlock.body().invoke("handleGenericException").arg(catchBlock.param("e"));
        return $v;
    }
    
    public JType getType() {
        return this.codeModel.ref((W3CDOMItem.class$org$w3c$dom$Element == null) ? (W3CDOMItem.class$org$w3c$dom$Element = class$("org.w3c.dom.Element")) : W3CDOMItem.class$org$w3c$dom$Element);
    }
    
    public JExpression createRootUnmarshaller(final GeneratorContext context, final JVar $unmarshallingContext) {
        final JClass handlerClass = context.getRuntime((W3CDOMItem.class$com$sun$tools$xjc$runtime$W3CDOMUnmarshallingEventHandler == null) ? (W3CDOMItem.class$com$sun$tools$xjc$runtime$W3CDOMUnmarshallingEventHandler = class$("com.sun.tools.xjc.runtime.W3CDOMUnmarshallingEventHandler")) : W3CDOMItem.class$com$sun$tools$xjc$runtime$W3CDOMUnmarshallingEventHandler);
        return JExpr._new(handlerClass).arg($unmarshallingContext);
    }
    
    static {
        W3CDOMItem.factory = (DOMItemFactory)new W3CDOMItem$1();
    }
}
