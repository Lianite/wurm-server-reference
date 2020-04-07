// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.marshaller;

import com.sun.tools.xjc.grammar.SuperClassItem;
import com.sun.tools.xjc.grammar.IgnoreItem;
import com.sun.tools.xjc.grammar.JavaItem;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.tools.xjc.grammar.JavaItemVisitor;
import com.sun.tools.xjc.grammar.util.Multiplicity;
import com.sun.tools.xjc.generator.field.FieldRenderer;
import com.sun.tools.xjc.grammar.util.FieldMultiplicityCounter;
import com.sun.codemodel.JVar;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JType;
import com.sun.xml.bind.JAXBAssertionError;
import com.sun.tools.xjc.grammar.ExternalItem;
import com.sun.tools.xjc.grammar.ClassItem;
import com.sun.tools.xjc.grammar.InterfaceItem;
import com.sun.tools.xjc.grammar.PrimitiveItem;
import com.sun.tools.xjc.grammar.TypeItem;
import com.sun.tools.xjc.generator.util.BlockReference;
import com.sun.msv.grammar.ExpressionVisitorBoolean;
import com.sun.codemodel.JExpression;
import com.sun.tools.xjc.grammar.FieldItem;
import com.sun.msv.grammar.Expression;
import com.sun.tools.xjc.grammar.util.FieldItemCollector;
import com.sun.msv.grammar.ChoiceExp;
import com.sun.msv.grammar.util.ExpressionFinder;

class Outside extends AbstractSideImpl
{
    private static final ExpressionFinder isNonOptimizable;
    
    protected Outside(final Context _context) {
        super(_context);
    }
    
    public void onChoice(final ChoiceExp exp) {
        final Expression[] children = exp.getChildren();
        final NestedIfBlockProvider nib = new NestedIfBlockProvider(this.context);
        Expression defaultBranch = null;
        boolean strong = true;
        if (this.context.inOneOrMore) {
            strong = false;
        }
        final FieldItem[] allFi = FieldItemCollector.collect((Expression)exp);
        for (int i = 0; i < children.length; ++i) {
            final Expression e = children[i];
            final FieldItem[] fi = FieldItemCollector.collect(e);
            if (fi.length == 0) {
                if (defaultBranch == null) {
                    defaultBranch = children[i];
                }
            }
            else {
                nib.startBlock(strong ? this.createStrongTest(children[i], allFi) : this.createWeakTest(fi));
                this.context.build(children[i]);
            }
        }
        if (defaultBranch != null) {
            nib.startElse();
            this.context.build(defaultBranch);
        }
        nib.end();
    }
    
    public void onZeroOrMore(final Expression exp) {
        final JExpression expr = this.createWeakTest(FieldItemCollector.collect(exp));
        if (expr == null) {
            this.context.build(exp);
            return;
        }
        this.context.pushNewBlock(this.createWhileBlock(this.context.getCurrentBlock(), expr));
        this.context.build(exp);
        this.context.popBlock();
    }
    
    public void onMarshallableObject() {
        _assert(false);
    }
    
    public void onField(final FieldItem item) {
        final FieldMarshallerGenerator fmg = this.context.getMarshaller(item);
        if (fmg == null) {
            return;
        }
        this.context.pushFieldItem(item);
        if (item.exp.visit((ExpressionVisitorBoolean)Outside.isNonOptimizable)) {
            this.context.build(item.exp);
        }
        else if (item.multiplicity.max == null) {
            this.context.pushNewBlock(this.createWhileBlock(this.context.getCurrentBlock(), fmg.hasMore()));
            this.onTypeItem(item);
            this.context.popBlock();
        }
        else {
            for (int i = 0; i < item.multiplicity.min; ++i) {
                this.onTypeItem(item);
            }
            final int repeatCount = item.multiplicity.max - item.multiplicity.min;
            if (repeatCount > 0) {
                final BlockReference parent = this.context.getCurrentBlock();
                this.context.pushNewBlock((BlockReference)new Outside$1(this, parent, repeatCount, fmg));
                this.onTypeItem(item);
                this.context.popBlock();
            }
        }
        this.context.popFieldItem(item);
    }
    
    private void onTypeItem(final FieldItem parent) {
        final TypeItem[] types = parent.listTypes();
        TypeItem.sort(types);
        boolean haveSerializableObject = false;
        boolean haveOtherObject = false;
        for (int i = 0; i < types.length; ++i) {
            if (types[i] instanceof PrimitiveItem) {
                haveOtherObject = true;
            }
            else if (types[i] instanceof InterfaceItem) {
                haveSerializableObject = true;
            }
            else if (types[i] instanceof ClassItem) {
                haveSerializableObject = true;
            }
            else {
                if (!(types[i] instanceof ExternalItem)) {
                    throw new JAXBAssertionError();
                }
                haveOtherObject = true;
            }
        }
        if (haveSerializableObject && !haveOtherObject) {
            this.context.currentSide.onMarshallableObject();
            return;
        }
        if (!haveSerializableObject && types.length == 1) {
            this.context.build((Expression)types[0]);
            return;
        }
        final JBlock block = this.getBlock(true).block();
        this.context.pushNewBlock(block);
        final JCodeModel codeModel = this.context.codeModel;
        final FieldMarshallerGenerator fmg = this.context.getCurrentFieldMarshaller();
        final JVar $o = block.decl(codeModel.ref((Outside.class$java$lang$Object == null) ? (Outside.class$java$lang$Object = class$("java.lang.Object")) : Outside.class$java$lang$Object), "o", fmg.peek(false));
        final NestedIfBlockProvider nib = new NestedIfBlockProvider(this.context);
        if (haveSerializableObject) {
            nib.startBlock($o._instanceof(codeModel.ref((Outside.class$com$sun$xml$bind$JAXBObject == null) ? (Outside.class$com$sun$xml$bind$JAXBObject = class$("com.sun.xml.bind.JAXBObject")) : Outside.class$com$sun$xml$bind$JAXBObject)));
            this.context.currentSide.onMarshallableObject();
        }
        for (int j = 0; j < types.length; ++j) {
            if (types[j] instanceof PrimitiveItem || types[j] instanceof ExternalItem) {
                nib.startBlock(this.instanceOf((JExpression)$o, types[j].getType()));
                this.context.build((Expression)types[j]);
            }
        }
        nib.startElse();
        if (this.getBlock(false) != null) {
            this.getBlock(false).staticInvoke(this.context.getRuntime((Outside.class$com$sun$tools$xjc$runtime$Util == null) ? (Outside.class$com$sun$tools$xjc$runtime$Util = class$("com.sun.tools.xjc.runtime.Util")) : Outside.class$com$sun$tools$xjc$runtime$Util), "handleTypeMismatchError").arg(this.context.$serializer).arg(JExpr._this()).arg(JExpr.lit(fmg.owner().getFieldUse().name)).arg($o);
        }
        nib.end();
        this.context.popBlock();
    }
    
    private JExpression createWeakTest(final FieldItem[] fi) {
        JExpression expr = JExpr.FALSE;
        for (int i = 0; i < fi.length; ++i) {
            final FieldMarshallerGenerator fmg = this.context.getMarshaller(fi[i]);
            if (fmg != null) {
                expr = expr.cor(fmg.hasMore());
            }
        }
        return expr;
    }
    
    private JExpression createStrongTest(final Expression branch, final FieldItem[] fi) {
        JExpression expr = JExpr.TRUE;
        for (int i = 0; i < fi.length; ++i) {
            final FieldRenderer fr = this.context.getMarshaller(fi[i]).owner();
            final Multiplicity m = FieldMultiplicityCounter.count(branch, fi[i]);
            JExpression e = JExpr.TRUE;
            JExpression f = JExpr.TRUE;
            if (m.max != null && m.min == m.max) {
                e = fr.ifCountEqual(m.min);
            }
            else {
                if (m.min != 0) {
                    e = fr.ifCountGte(m.min);
                }
                if (m.max != null) {
                    f = fr.ifCountLte((int)m.max);
                }
            }
            expr = expr.cand(e).cand(f);
        }
        return expr;
    }
    
    static {
        Outside.isNonOptimizable = (ExpressionFinder)new NonOptimizabilityChecker((Outside$1)null);
    }
    
    private static class NonOptimizabilityChecker extends ExpressionFinder implements JavaItemVisitor
    {
        public boolean onElement(final ElementExp e) {
            return true;
        }
        
        public boolean onAttribute(final AttributeExp a) {
            return true;
        }
        
        public boolean onOther(final OtherExp exp) {
            if (exp instanceof JavaItem) {
                return (boolean)((JavaItem)exp).visitJI((JavaItemVisitor)this);
            }
            return exp.exp.visit((ExpressionVisitorBoolean)this);
        }
        
        public Object onClass(final ClassItem c) {
            return Boolean.FALSE;
        }
        
        public Object onInterface(final InterfaceItem i) {
            return Boolean.FALSE;
        }
        
        public Object onPrimitive(final PrimitiveItem p) {
            return Boolean.FALSE;
        }
        
        public Object onExternal(final ExternalItem p) {
            return Boolean.FALSE;
        }
        
        public Object onIgnore(final IgnoreItem i) {
            return Boolean.TRUE;
        }
        
        public Object onField(final FieldItem f) {
            throw new JAXBAssertionError();
        }
        
        public Object onSuper(final SuperClassItem s) {
            throw new JAXBAssertionError();
        }
    }
}
