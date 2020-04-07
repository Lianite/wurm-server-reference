// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.field;

import com.sun.tools.xjc.generator.util.BlockReference;
import com.sun.codemodel.JVar;
import com.sun.tools.xjc.generator.marshaller.FieldMarshallerGenerator;
import com.sun.codemodel.JInvocation;
import com.sun.tools.xjc.grammar.FieldItem;
import com.sun.msv.grammar.ExpressionVisitorVoid;
import java.util.ArrayList;
import com.sun.codemodel.JType;
import com.sun.codemodel.JPrimitiveType;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JAssignmentTarget;
import com.sun.codemodel.JStatement;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpression;
import com.sun.tools.xjc.generator.ClassContext;
import com.sun.codemodel.JFieldVar;
import com.sun.tools.xjc.grammar.FieldUse;
import com.sun.codemodel.JCodeModel;

public final class ConstFieldRenderer implements FieldRenderer
{
    private final JCodeModel codeModel;
    private boolean isCollection;
    private final FieldUse use;
    private JFieldVar $ref;
    private int count;
    public static final FieldRendererFactory theFactory;
    
    public ConstFieldRenderer(final ClassContext context, final FieldUse _use) {
        this.isCollection = false;
        this.use = _use;
        this.codeModel = this.use.codeModel;
        final JExpression initializer = this.calcInitializer();
        this.$ref = context.ref.field(25, this.isCollection ? this.getType().array() : this.getType(), this.use.name, initializer);
        this.$ref.javadoc().appendComment(this.use.getJavadoc());
    }
    
    public void generate() {
    }
    
    public JBlock getOnSetEventHandler() {
        return JBlock.dummyInstance;
    }
    
    public void toArray(final JBlock block, final JExpression $array) {
        if (this.isCollection) {
            block.add(this.codeModel.ref((ConstFieldRenderer.class$java$lang$System == null) ? (ConstFieldRenderer.class$java$lang$System = class$("java.lang.System")) : ConstFieldRenderer.class$java$lang$System).staticInvoke("arraycopy").arg(this.$ref).arg(JExpr.lit(0)).arg($array).arg(JExpr.lit(0)).arg(this.$ref.ref("length")));
        }
        else {
            block.assign($array.component(JExpr.lit(0)), this.$ref);
        }
    }
    
    public void unsetValues(final JBlock body) {
    }
    
    public JExpression hasSetValue() {
        return null;
    }
    
    public JExpression getValue() {
        return this.$ref;
    }
    
    public JClass getValueType() {
        if (this.isCollection) {
            return this.getType().array();
        }
        if (this.getType().isPrimitive()) {
            return ((JPrimitiveType)this.getType()).getWrapperClass();
        }
        return (JClass)this.getType();
    }
    
    private JType getType() {
        return this.use.type;
    }
    
    public FieldUse getFieldUse() {
        return this.use;
    }
    
    public void setter(final JBlock body, final JExpression newValue) {
    }
    
    public JExpression ifCountEqual(final int i) {
        if (i == this.count) {
            return JExpr.TRUE;
        }
        return JExpr.FALSE;
    }
    
    public JExpression ifCountGte(final int i) {
        if (i <= this.count) {
            return JExpr.TRUE;
        }
        return JExpr.FALSE;
    }
    
    public JExpression ifCountLte(final int i) {
        if (i >= this.count) {
            return JExpr.TRUE;
        }
        return JExpr.FALSE;
    }
    
    public JExpression count() {
        return JExpr.lit(this.count);
    }
    
    private JExpression calcInitializer() {
        final FieldItem[] items = this.use.getItems();
        final ArrayList result = new ArrayList();
        items[0].exp.visit((ExpressionVisitorVoid)new ConstFieldRenderer$2(this, result));
        this.count = result.size();
        if (!this.isCollection) {
            return result.get(0);
        }
        final JInvocation inv = JExpr._new(this.getType().array());
        for (int i = 0; i < result.size(); ++i) {
            inv.arg(result.get(i));
        }
        return inv;
    }
    
    public FieldMarshallerGenerator createMarshaller(final JBlock block, final String uniqueId) {
        if (!this.isCollection) {
            return (FieldMarshallerGenerator)new SingleFMGImpl(this, (ConstFieldRenderer$1)null);
        }
        final JVar $idx = block.decl(this.codeModel.INT, "idx" + uniqueId, JExpr.lit(0));
        return (FieldMarshallerGenerator)new CollectionFMGImpl(this, $idx);
    }
    
    static {
        ConstFieldRenderer.theFactory = (FieldRendererFactory)new ConstFieldRenderer$1();
    }
    
    private final class SingleFMGImpl implements FieldMarshallerGenerator
    {
        private SingleFMGImpl(final ConstFieldRenderer this$0) {
            this.this$0 = this$0;
        }
        
        public JExpression hasMore() {
            return JExpr.TRUE;
        }
        
        public JExpression peek(final boolean increment) {
            return this.this$0.$ref;
        }
        
        public void increment(final BlockReference block) {
        }
        
        public FieldMarshallerGenerator clone(final JBlock block, final String uniqueId) {
            return (FieldMarshallerGenerator)this;
        }
        
        public FieldRenderer owner() {
            return (FieldRenderer)this.this$0;
        }
    }
    
    private class CollectionFMGImpl implements FieldMarshallerGenerator
    {
        private final JVar $idx;
        
        CollectionFMGImpl(final ConstFieldRenderer this$0, final JVar _$idx) {
            this.this$0 = this$0;
            this.$idx = _$idx;
        }
        
        public JExpression hasMore() {
            return this.$idx.ne(this.this$0.$ref.ref("length"));
        }
        
        public JExpression peek(final boolean increment) {
            return this.this$0.$ref.component(increment ? this.$idx.incr() : this.$idx);
        }
        
        public void increment(final BlockReference block) {
            block.get(true).assignPlus(this.$idx, JExpr.lit(1));
        }
        
        public FieldMarshallerGenerator clone(final JBlock block, final String uniqueId) {
            final JVar $newidx = block.decl(this.this$0.codeModel.INT, "idx" + uniqueId, this.$idx);
            return (FieldMarshallerGenerator)new CollectionFMGImpl(this.this$0, $newidx);
        }
        
        public FieldRenderer owner() {
            return (FieldRenderer)this.this$0;
        }
    }
}
