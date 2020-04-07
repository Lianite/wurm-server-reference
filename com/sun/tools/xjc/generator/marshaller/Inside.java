// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.marshaller;

import com.sun.tools.xjc.grammar.FieldItem;
import com.sun.tools.xjc.grammar.util.TypeItemCollector;
import com.sun.codemodel.JBlock;
import com.sun.tools.xjc.generator.LookupTableUse;
import com.sun.tools.xjc.grammar.xducer.SerializerContext;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JExpr;
import com.sun.msv.grammar.ChoiceExp;
import com.sun.tools.xjc.grammar.ClassItem;
import com.sun.msv.grammar.Expression;
import com.sun.codemodel.JType;
import com.sun.codemodel.JClass;
import com.sun.tools.xjc.grammar.TypeItem;

final class Inside extends AbstractSideImpl
{
    public Inside(final Context _context) {
        super(_context);
    }
    
    private boolean isImplentingElement(final TypeItem t) {
        final JType jt = t.getType();
        if (jt.isPrimitive()) {
            return false;
        }
        final JClass jc = (JClass)jt;
        return this.context.codeModel.ref((Inside.class$javax$xml$bind$Element == null) ? (Inside.class$javax$xml$bind$Element = class$("javax.xml.bind.Element")) : Inside.class$javax$xml$bind$Element).isAssignableFrom(jc);
    }
    
    private boolean tryOptimizedChoice1(final Expression[] children, final TypeItem[][] types) {
        final FieldMarshallerGenerator fmg = this.context.getCurrentFieldMarshaller();
        Expression rest = Expression.nullSet;
        int count = 0;
        for (int i = 0; i < children.length; ++i) {
            if (types[i].length != 1) {
                return false;
            }
            if (this.isImplentingElement(types[i][0])) {
                if (!(children[i] instanceof ClassItem)) {
                    return false;
                }
                ++count;
            }
            else {
                rest = this.context.pool.createChoice(rest, children[i]);
            }
        }
        if (count == 0) {
            return false;
        }
        if (rest == Expression.nullSet) {
            this.onMarshallableObject();
        }
        else {
            final IfThenElseBlockReference ifb = new IfThenElseBlockReference(this.context, fmg.peek(false)._instanceof(this.context.codeModel.ref((Inside.class$javax$xml$bind$Element == null) ? (Inside.class$javax$xml$bind$Element = class$("javax.xml.bind.Element")) : Inside.class$javax$xml$bind$Element)));
            this.context.pushNewBlock(ifb.createThenProvider());
            this.onMarshallableObject();
            this.context.popBlock();
            this.context.pushNewBlock(ifb.createElseProvider());
            this.context.build(rest);
            this.context.popBlock();
        }
        return true;
    }
    
    private boolean tryOptimizedChoice2(final ChoiceExp exp, final Expression[] children) {
        final LookupTableUse tableUse = this.context.genContext.getLookupTableBuilder().buildTable(exp);
        if (tableUse == null) {
            return false;
        }
        NestedIfBlockProvider nib = null;
        final FieldMarshallerGenerator fmg = this.context.getCurrentFieldMarshaller();
        if (tableUse.anomaly != null) {
            if (!(tableUse.anomaly instanceof ClassItem)) {
                return false;
            }
            final JClass vo = this.context.getRuntime((Inside.class$com$sun$tools$xjc$runtime$ValidatableObject == null) ? (Inside.class$com$sun$tools$xjc$runtime$ValidatableObject = class$("com.sun.tools.xjc.runtime.ValidatableObject")) : Inside.class$com$sun$tools$xjc$runtime$ValidatableObject);
            final JExpression test = JExpr.cast(vo, this.context.codeModel.ref((Inside.class$com$sun$xml$bind$ProxyGroup == null) ? (Inside.class$com$sun$xml$bind$ProxyGroup = class$("com.sun.xml.bind.ProxyGroup")) : Inside.class$com$sun$xml$bind$ProxyGroup).staticInvoke("blindWrap").arg(fmg.peek(false)).arg(vo.dotclass()).arg(JExpr._null())).invoke("getPrimaryInterface");
            final ClassItem ancls = (ClassItem)tableUse.anomaly;
            nib = new NestedIfBlockProvider(this.context);
            nib.startBlock(test.ne(ancls.getTypeAsDefined().dotclass()));
        }
        if (this.context.currentPass != this.context.skipPass) {
            if (this.context.currentPass == this.context.uriPass) {
                this.getBlock(true).invoke(this.context.$serializer.invoke("getNamespaceContext"), "declareNamespace").arg(JExpr.lit(tableUse.switchAttName.namespaceURI)).arg(JExpr._null()).arg(JExpr.FALSE);
                tableUse.table.declareNamespace(this.context.getCurrentBlock(), fmg.peek(false), (SerializerContext)this.context);
            }
            else if (this.context.currentPass != this.context.bodyPass) {
                if (this.context.currentPass == this.context.attPass) {
                    final JBlock block = this.getBlock(true);
                    block.invoke(this.context.$serializer, "startAttribute").arg(JExpr.lit(tableUse.switchAttName.namespaceURI)).arg(JExpr.lit(tableUse.switchAttName.localName));
                    block.invoke(this.context.$serializer, "text").arg(tableUse.table.reverseLookup(fmg.peek(false), (SerializerContext)this.context)).arg(JExpr.lit(fmg.owner().getFieldUse().name));
                    block.invoke(this.context.$serializer, "endAttribute");
                }
                else {
                    _assert(false);
                }
            }
        }
        if (nib != null) {
            nib.end();
        }
        this.onMarshallableObject();
        return true;
    }
    
    public void onChoice(final ChoiceExp exp) {
        final FieldMarshallerGenerator fmg = this.context.getCurrentFieldMarshaller();
        final Expression[] children = exp.getChildren();
        final TypeItem[][] types = new TypeItem[children.length][];
        for (int i = 0; i < children.length; ++i) {
            types[i] = TypeItemCollector.collect(children[i]);
        }
        if (this.tryOptimizedChoice1(children, types)) {
            return;
        }
        if (this.tryOptimizedChoice2(exp, children)) {
            return;
        }
        final NestedIfBlockProvider nib = new NestedIfBlockProvider(this.context);
        for (int j = 0; j < children.length; ++j) {
            if (types[j].length == 0) {
                nib.startBlock(fmg.hasMore().not());
            }
            else {
                JExpression testExp = null;
                for (int k = 0; k < types[j].length; ++k) {
                    final JType t = types[j][k].getType();
                    final JExpression e = this.instanceOf(fmg.peek(false), t);
                    if (testExp == null) {
                        testExp = e;
                    }
                    else {
                        testExp = testExp.cor(e);
                    }
                }
                nib.startBlock(testExp);
            }
            this.context.build(children[j]);
        }
        nib.startElse();
        if (this.getBlock(false) != null) {
            this.getBlock(false).staticInvoke(this.context.getRuntime((Inside.class$com$sun$tools$xjc$runtime$Util == null) ? (Inside.class$com$sun$tools$xjc$runtime$Util = class$("com.sun.tools.xjc.runtime.Util")) : Inside.class$com$sun$tools$xjc$runtime$Util), "handleTypeMismatchError").arg(this.context.$serializer).arg(JExpr._this()).arg(JExpr.lit(fmg.owner().getFieldUse().name)).arg(fmg.peek(false));
        }
        nib.end();
    }
    
    public void onZeroOrMore(final Expression exp) {
        final JExpression expr = this.context.getCurrentFieldMarshaller().hasMore();
        this.context.pushNewBlock(this.createWhileBlock(this.context.getCurrentBlock(), expr));
        this.context.build(exp);
        this.context.popBlock();
    }
    
    public void onMarshallableObject() {
        final FieldMarshallerGenerator fm = this.context.getCurrentFieldMarshaller();
        if (this.context.currentPass == this.context.skipPass) {
            fm.increment(this.context.getCurrentBlock());
            return;
        }
        final JClass joRef = this.context.codeModel.ref((Inside.class$com$sun$xml$bind$JAXBObject == null) ? (Inside.class$com$sun$xml$bind$JAXBObject = class$("com.sun.xml.bind.JAXBObject")) : Inside.class$com$sun$xml$bind$JAXBObject);
        this.getBlock(true).invoke(this.context.$serializer, "childAs" + this.context.currentPass.getName()).arg(JExpr.cast(joRef, fm.peek(true))).arg(JExpr.lit(fm.owner().getFieldUse().name));
    }
    
    public void onField(final FieldItem item) {
        _assert(false);
    }
}
