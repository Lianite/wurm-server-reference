// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.marshaller;

import java.util.Iterator;
import com.sun.tools.xjc.grammar.ClassItem;
import com.sun.tools.xjc.grammar.TypeItem;
import com.sun.tools.xjc.grammar.InterfaceItem;
import com.sun.tools.xjc.grammar.SuperClassItem;
import com.sun.tools.xjc.grammar.IgnoreItem;
import com.sun.msv.grammar.ExpressionVisitorVoid;
import com.sun.tools.xjc.grammar.FieldItem;
import java.util.HashSet;
import java.util.Set;
import com.sun.tools.xjc.grammar.BGMWalker;
import com.sun.msv.grammar.ValueExp;
import com.sun.tools.xjc.grammar.xducer.Transducer;
import com.sun.tools.xjc.grammar.xducer.SerializerContext;
import com.sun.codemodel.JExpr;
import com.sun.tools.xjc.grammar.xducer.TypeAdaptedTransducer;
import com.sun.tools.xjc.grammar.PrimitiveItem;
import com.sun.msv.grammar.AttributeExp;
import com.sun.tools.xjc.grammar.ExternalItem;
import com.sun.codemodel.JBlock;
import com.sun.msv.grammar.Expression;
import com.sun.codemodel.JExpression;
import com.sun.tools.xjc.generator.XmlNameStoreAlgorithm;
import com.sun.msv.grammar.NameClassAndExpression;
import com.sun.msv.grammar.ElementExp;

class BodyPass extends AbstractPassImpl
{
    BodyPass(final Context _context, final String name) {
        super(_context, name);
    }
    
    public void onElement(final ElementExp exp) {
        this._onElement((NameClassAndExpression)exp);
    }
    
    private void _onElement(final NameClassAndExpression exp) {
        final Expression contentModel = exp.getContentModel();
        final JBlock block = this.getBlock(true);
        final XmlNameStoreAlgorithm algorithm = XmlNameStoreAlgorithm.get(exp.getNameClass());
        block.invoke(this.context.$serializer, "startElement").arg(algorithm.getNamespaceURI()).arg(algorithm.getLocalPart());
        FieldCloner fc = new FieldCloner(this, contentModel, true);
        fc.push();
        this.context.uriPass.build(contentModel);
        block.invoke(this.context.$serializer, "endNamespaceDecls");
        fc.pop();
        fc = new FieldCloner(this, contentModel, false);
        fc.push();
        this.context.attPass.build(contentModel);
        block.invoke(this.context.$serializer, "endAttributes");
        fc.pop();
        this.context.bodyPass.build(contentModel);
        block.invoke(this.context.$serializer, "endElement");
    }
    
    public void onExternal(final ExternalItem item) {
        item.generateMarshaller(this.context.genContext, this.getBlock(true), this.context.getCurrentFieldMarshaller(), (JExpression)this.context.$serializer);
    }
    
    public void onAttribute(final AttributeExp exp) {
    }
    
    public void onPrimitive(final PrimitiveItem exp) {
        final FieldMarshallerGenerator fmg = this.context.getCurrentFieldMarshaller();
        final Transducer xducer = TypeAdaptedTransducer.adapt(exp.xducer, fmg.owner().getFieldUse().type);
        this.getBlock(true).invoke(this.context.$serializer, "text").arg(xducer.generateSerializer((JExpression)JExpr.cast(xducer.getReturnType(), fmg.peek(true)), (SerializerContext)this.context)).arg(JExpr.lit(fmg.owner().getFieldUse().name));
    }
    
    public void onValue(final ValueExp exp) {
        this.marshalValue(exp);
    }
    
    private final class FieldFinder extends BGMWalker
    {
        private final Set result;
        private final boolean visitAttributes;
        
        public FieldFinder(final BodyPass this$0, final boolean _visitAttributes) {
            this.this$0 = this$0;
            this.result = new HashSet();
            this.visitAttributes = _visitAttributes;
        }
        
        public Set getResult() {
            return this.result;
        }
        
        public void onAttribute(final AttributeExp exp) {
            if (this.visitAttributes) {
                super.onAttribute(exp);
            }
        }
        
        public Object onField(final FieldItem item) {
            this.this$0.context.pushFieldItem(item);
            item.exp.visit((ExpressionVisitorVoid)this);
            this.this$0.context.popFieldItem(item);
            return null;
        }
        
        public Object onIgnore(final IgnoreItem exp) {
            return null;
        }
        
        public Object onSuper(final SuperClassItem exp) {
            return null;
        }
        
        public Object onInterface(final InterfaceItem exp) {
            return this.onTypedItem((TypeItem)exp);
        }
        
        public Object onExternal(final ExternalItem exp) {
            return this.onTypedItem((TypeItem)exp);
        }
        
        public Object onClass(final ClassItem exp) {
            return this.onTypedItem((TypeItem)exp);
        }
        
        public Object onPrimitive(final PrimitiveItem exp) {
            return this.onTypedItem((TypeItem)exp);
        }
        
        private Object onTypedItem(final TypeItem ti) {
            this.result.add(this.this$0.context.getCurrentFieldMarshaller());
            return null;
        }
    }
    
    private class FieldCloner
    {
        private final Set results;
        
        protected FieldCloner(final BodyPass this$0, final Expression e, final boolean visitAttributes) {
            this.this$0 = this$0;
            final FieldFinder ff = new FieldFinder(this$0, visitAttributes);
            e.visit((ExpressionVisitorVoid)ff);
            this.results = ff.getResult();
        }
        
        public void push() {
            for (final FieldMarshallerGenerator fmg : this.results) {
                final FieldMarshallerGenerator cloned = fmg.clone(this.this$0.getBlock(true), this.this$0.context.createIdentifier());
                this.this$0.context.pushNewFieldMarshallerMapping(fmg, cloned);
            }
        }
        
        public void pop() {
            for (int cnt = this.results.size(), i = 0; i < cnt; ++i) {
                this.this$0.context.popFieldMarshallerMapping();
            }
        }
    }
}
