// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.marshaller;

import com.sun.codemodel.JBlock;
import com.sun.msv.grammar.xmlschema.OccurrenceExp;
import com.sun.msv.grammar.OtherExp;
import com.sun.tools.xjc.generator.util.BlockReference;
import com.sun.tools.xjc.grammar.PrimitiveItem;
import com.sun.tools.xjc.grammar.ExternalItem;
import com.sun.tools.xjc.grammar.FieldItem;
import com.sun.tools.xjc.grammar.ClassItem;
import com.sun.tools.xjc.grammar.InterfaceItem;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.AttributeExp;
import com.sun.tools.xjc.grammar.SuperClassItem;
import com.sun.msv.grammar.ValueExp;
import com.sun.msv.grammar.DataExp;
import com.sun.msv.grammar.MixedExp;
import com.sun.xml.bind.JAXBAssertionError;
import com.sun.msv.grammar.ConcurExp;
import com.sun.msv.grammar.ExpressionVisitorVoid;
import com.sun.tools.xjc.grammar.IgnoreItem;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JExpr;
import com.sun.msv.grammar.OneOrMoreExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ChoiceExp;
import com.sun.tools.xjc.grammar.BGMWalker;

final class Builder extends BGMWalker
{
    private final Context context;
    static /* synthetic */ Class class$org$xml$sax$SAXException;
    
    protected Builder(final Context _context) {
        this.context = _context;
    }
    
    public final void onChoice(final ChoiceExp exp) {
        if (exp.exp1 == Expression.epsilon && exp.exp2 instanceof OneOrMoreExp) {
            this.onOneOrMore((OneOrMoreExp)exp.exp2);
            return;
        }
        if (exp.exp2 == Expression.epsilon && exp.exp1 instanceof OneOrMoreExp) {
            this.onOneOrMore((OneOrMoreExp)exp.exp1);
            return;
        }
        this.context.currentSide.onChoice(exp);
    }
    
    public final void onOneOrMore(final OneOrMoreExp exp) {
        this._onOneOrMore(exp.exp);
    }
    
    private void _onOneOrMore(final Expression itemExp) {
        final boolean oldOOM = this.context.inOneOrMore;
        this.context.inOneOrMore = true;
        this.context.currentSide.onZeroOrMore(itemExp);
        this.context.inOneOrMore = oldOOM;
    }
    
    public final void onNullSet() {
        this.getBlock(true)._throw(JExpr._new(this.context.codeModel.ref((Builder.class$org$xml$sax$SAXException == null) ? (Builder.class$org$xml$sax$SAXException = class$("org.xml.sax.SAXException")) : Builder.class$org$xml$sax$SAXException)).arg(JExpr.lit("this object doesn't have any XML representation")));
    }
    
    public Object onIgnore(final IgnoreItem exp) {
        if (exp.exp.isEpsilonReducible()) {
            return null;
        }
        exp.exp.visit((ExpressionVisitorVoid)this);
        return null;
    }
    
    public void onConcur(final ConcurExp exp) {
        throw new JAXBAssertionError();
    }
    
    public void onMixed(final MixedExp exp) {
        throw new JAXBAssertionError();
    }
    
    public void onData(final DataExp exp) {
        throw new JAXBAssertionError();
    }
    
    public void onAnyString() {
    }
    
    public final void onValue(final ValueExp exp) {
        this.context.currentPass.onValue(exp);
    }
    
    public Object onSuper(final SuperClassItem exp) {
        if (this.context.currentPass == this.context.skipPass) {
            return null;
        }
        this.getBlock(true).invoke(JExpr._super(), "serialize" + this.context.currentPass.getName()).arg(this.context.$serializer);
        return null;
    }
    
    public void onAttribute(final AttributeExp exp) {
        this.context.currentPass.onAttribute(exp);
    }
    
    public void onElement(final ElementExp exp) {
        this.context.currentPass.onElement(exp);
    }
    
    public final Object onInterface(final InterfaceItem exp) {
        this.context.currentSide.onMarshallableObject();
        return null;
    }
    
    public final Object onClass(final ClassItem exp) {
        this.context.currentSide.onMarshallableObject();
        return null;
    }
    
    public Object onField(final FieldItem item) {
        this.context.currentSide.onField(item);
        return null;
    }
    
    public final Object onExternal(final ExternalItem item) {
        this.context.currentPass.onExternal(item);
        return null;
    }
    
    public final Object onPrimitive(final PrimitiveItem item) {
        this.context.pushNewBlock((BlockReference)new PrintExceptionTryCatchBlockReference(this.context));
        this.context.currentPass.onPrimitive(item);
        this.context.popBlock();
        return null;
    }
    
    public void onOther(final OtherExp exp) {
        if (exp instanceof OccurrenceExp) {
            this.onOccurence((OccurrenceExp)exp);
        }
        else {
            super.onOther(exp);
        }
    }
    
    public void onOccurence(final OccurrenceExp exp) {
        this._onOneOrMore(exp.itemExp);
    }
    
    protected final JBlock getBlock(final boolean create) {
        return this.context.getCurrentBlock().get(create);
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
