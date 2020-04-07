// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar.ext;

import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JVar;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.tools.xjc.generator.marshaller.FieldMarshallerGenerator;
import com.sun.codemodel.JBlock;
import com.sun.tools.xjc.generator.GeneratorContext;
import com.sun.msv.grammar.trex.ElementPattern;
import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.NamespaceNameClass;
import com.sun.xml.bind.GrammarImpl;
import com.sun.xml.bind.xmlschema.LaxWildcardPlug;
import com.sun.xml.bind.xmlschema.StrictWildcardPlug;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.reader.xmlschema.WildcardNameClassBuilder;
import com.sun.xml.xsom.XSWildcard;
import org.xml.sax.Locator;
import com.sun.msv.grammar.NameClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JClass;
import com.sun.tools.xjc.grammar.ExternalItem;

public class WildcardItem extends ExternalItem
{
    public final boolean errorIfNotFound;
    private final JClass refObject;
    static /* synthetic */ Class class$java$lang$Object;
    static /* synthetic */ Class class$com$sun$xml$bind$JAXBObject;
    
    public WildcardItem(final JCodeModel codeModel, final NameClass nc, final boolean errorIfNotFound, final Locator loc) {
        super("wildcard", nc, loc);
        this.refObject = codeModel.ref((WildcardItem.class$java$lang$Object == null) ? (WildcardItem.class$java$lang$Object = class$("java.lang.Object")) : WildcardItem.class$java$lang$Object);
        this.errorIfNotFound = errorIfNotFound;
    }
    
    public WildcardItem(final JCodeModel codeModel, final XSWildcard wc) {
        this(codeModel, WildcardNameClassBuilder.build(wc), wc.getMode() == 2, wc.getLocator());
    }
    
    public JType getType() {
        return this.refObject;
    }
    
    public Expression createAGM(final ExpressionPool pool) {
        GrammarImpl.Plug p;
        if (this.errorIfNotFound) {
            p = (GrammarImpl.Plug)new StrictWildcardPlug(this.elementName);
        }
        else {
            p = (GrammarImpl.Plug)new LaxWildcardPlug(this.elementName);
        }
        return (Expression)p;
    }
    
    public Expression createValidationFragment() {
        return (Expression)new ElementPattern((NameClass)new NamespaceNameClass("http://java.sun.com/jaxb/xjc/dummy-elements"), (Expression)new AttributeExp(this.elementName, Expression.anyString));
    }
    
    public void generateMarshaller(final GeneratorContext context, final JBlock block, final FieldMarshallerGenerator fmg, final JExpression $context) {
        block.invoke($context, "childAsBody").arg(JExpr.cast(context.getCodeModel().ref((WildcardItem.class$com$sun$xml$bind$JAXBObject == null) ? (WildcardItem.class$com$sun$xml$bind$JAXBObject = class$("com.sun.xml.bind.JAXBObject")) : WildcardItem.class$com$sun$xml$bind$JAXBObject), fmg.peek(true))).arg(JExpr.lit(fmg.owner().getFieldUse().name));
    }
    
    public JExpression generateUnmarshaller(final GeneratorContext context, final JExpression $unmarshallingContext, final JBlock block, final JExpression memento, final JVar $uri, final JVar $local, final JVar $qname, final JVar $atts) {
        final JInvocation spawn = JExpr.invoke("spawnWildcard").arg(memento).arg($uri).arg($local).arg($qname).arg($atts);
        return block.decl(this.getType(), "co", spawn);
    }
    
    public JExpression createRootUnmarshaller(final GeneratorContext context, final JVar $unmarshallingContext) {
        return null;
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
