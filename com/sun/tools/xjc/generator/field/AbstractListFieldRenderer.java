// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.field;

import com.sun.tools.xjc.generator.util.BlockReference;
import com.sun.tools.xjc.generator.marshaller.FieldMarshallerGenerator;
import com.sun.codemodel.JOp;
import com.sun.codemodel.JForLoop;
import com.sun.codemodel.JInvocation;
import com.sun.tools.xjc.grammar.DefaultValue;
import com.sun.codemodel.JAssignmentTarget;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JType;
import com.sun.codemodel.JExpr;
import com.sun.tools.xjc.grammar.FieldUse;
import com.sun.tools.xjc.generator.ClassContext;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JPrimitiveType;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JVar;

abstract class AbstractListFieldRenderer extends AbstractFieldRenderer
{
    protected JVar $defValues;
    private final JClass coreList;
    protected JPrimitiveType primitiveType;
    private JBlock onSetHandler;
    private JExpression newListObjectExp;
    private JFieldVar field;
    private JMethod internalGetter;
    private JExpression lazyInitializer;
    
    protected AbstractListFieldRenderer(final ClassContext context, final FieldUse fu, final JClass coreList) {
        super(context, fu);
        this.$defValues = null;
        this.lazyInitializer = (JExpression)new AbstractListFieldRenderer$1(this);
        this.coreList = coreList;
        if (fu.type instanceof JPrimitiveType) {
            this.primitiveType = (JPrimitiveType)fu.type;
        }
    }
    
    protected final JExpression unbox(final JExpression exp) {
        if (this.primitiveType == null) {
            return exp;
        }
        return this.primitiveType.unwrap(exp);
    }
    
    protected final JExpression box(final JExpression exp) {
        if (this.primitiveType == null) {
            return exp;
        }
        return this.primitiveType.wrap(exp);
    }
    
    public JBlock getOnSetEventHandler() {
        if (this.onSetHandler != null) {
            return this.onSetHandler;
        }
        final JDefinedClass anonymousClass = this.codeModel.newAnonymousClass(this.codeModel.ref((AbstractListFieldRenderer.class$com$sun$xml$bind$util$ListImpl == null) ? (AbstractListFieldRenderer.class$com$sun$xml$bind$util$ListImpl = class$("com.sun.xml.bind.util.ListImpl")) : AbstractListFieldRenderer.class$com$sun$xml$bind$util$ListImpl));
        this.newListObjectExp = JExpr._new(anonymousClass).arg(JExpr._new(this.coreList));
        final JMethod method = anonymousClass.method(1, this.codeModel.VOID, "setModified");
        final JVar $f = method.param(this.codeModel.BOOLEAN, "f");
        method.body().invoke(JExpr._super(), "setModified").arg($f);
        return this.onSetHandler = method.body()._if($f)._then();
    }
    
    public JClass getValueType() {
        return this.codeModel.ref((AbstractListFieldRenderer.class$java$util$List == null) ? (AbstractListFieldRenderer.class$java$util$List = class$("java.util.List")) : AbstractListFieldRenderer.class$java$util$List);
    }
    
    public final void generate() {
        this.field = this.generateField();
        this.internalGetter = this.context.implClass.method(2, (AbstractListFieldRenderer.class$com$sun$xml$bind$util$ListImpl == null) ? (AbstractListFieldRenderer.class$com$sun$xml$bind$util$ListImpl = class$("com.sun.xml.bind.util.ListImpl")) : AbstractListFieldRenderer.class$com$sun$xml$bind$util$ListImpl, "_get" + this.fu.name);
        this.internalGetter.body()._if(this.field.eq(JExpr._null()))._then().assign(this.field, this.lazyInitializer);
        this.internalGetter.body()._return(this.field);
        this.generateAccessors();
    }
    
    protected final JExpression ref(final boolean canBeNull) {
        if (canBeNull) {
            return this.field;
        }
        return JExpr.invoke(this.internalGetter);
    }
    
    public abstract void generateAccessors();
    
    protected final JFieldVar generateField() {
        final DefaultValue[] defaultValues = this.fu.getDefaultValues();
        final JClass list = this.codeModel.ref((AbstractListFieldRenderer.class$com$sun$xml$bind$util$ListImpl == null) ? (AbstractListFieldRenderer.class$com$sun$xml$bind$util$ListImpl = class$("com.sun.xml.bind.util.ListImpl")) : AbstractListFieldRenderer.class$com$sun$xml$bind$util$ListImpl);
        final JFieldVar ref = this.generateField(list);
        this.newListObjectExp = JExpr._new(list).arg(JExpr._new(this.coreList));
        if (defaultValues != null) {
            final JType arrayType = this.fu.type.array();
            final JInvocation initializer;
            this.$defValues = this.context.implClass.field(26, arrayType, this.fu.name + "_defaultValues", initializer = JExpr._new(arrayType));
            for (int i = 0; i < defaultValues.length; ++i) {
                initializer.arg(defaultValues[i].generateConstant());
            }
        }
        return ref;
    }
    
    public void setter(final JBlock body, JExpression newValue) {
        if (this.primitiveType != null) {
            newValue = this.primitiveType.wrap(newValue);
        }
        body.invoke(this.ref(false), "add").arg(newValue);
    }
    
    public void toArray(JBlock block, final JExpression $array) {
        block = block._if(this.field.ne(JExpr._null()))._then();
        if (this.primitiveType == null) {
            block.invoke(this.ref(true), "toArray").arg($array);
        }
        else {
            final JForLoop $for = block._for();
            final JVar $idx = $for.init(this.codeModel.INT, "q" + this.hashCode(), this.count().minus(JExpr.lit(1)));
            $for.test($idx.gte(JExpr.lit(0)));
            $for.update($idx.decr());
            $for.body().assign($array.component($idx), this.primitiveType.unwrap(JExpr.cast(this.primitiveType.getWrapperClass(), this.ref(true).invoke("get").arg($idx))));
        }
    }
    
    public JExpression count() {
        return JOp.cond(this.field.eq(JExpr._null()), JExpr.lit(0), this.field.invoke("size"));
    }
    
    public JExpression ifCountEqual(final int i) {
        return this.count().eq(JExpr.lit(i));
    }
    
    public JExpression ifCountGte(final int i) {
        return this.count().gte(JExpr.lit(i));
    }
    
    public JExpression ifCountLte(final int i) {
        return this.count().lte(JExpr.lit(i));
    }
    
    public FieldMarshallerGenerator createMarshaller(final JBlock block, final String uniqueId) {
        final JVar $idx = block.decl(this.codeModel.INT, "idx" + uniqueId, JExpr.lit(0));
        final JVar $len = block.decl(8, this.codeModel.INT, "len" + uniqueId, (this.$defValues != null) ? JOp.cond(this.field.ne(JExpr._null()).cand(this.field.invoke("isModified")), this.field.invoke("size"), JExpr.lit(0)) : this.count());
        return (FieldMarshallerGenerator)new FMGImpl(this, $idx, $len);
    }
    
    public void unsetValues(JBlock body) {
        body = body._if(this.field.ne(JExpr._null()))._then();
        body.invoke(this.field, "clear");
        body.invoke(this.field, "setModified").arg(JExpr.FALSE);
    }
    
    public JExpression hasSetValue() {
        return JOp.cond(this.field.eq(JExpr._null()), JExpr.FALSE, this.field.invoke("isModified"));
    }
    
    public JExpression getValue() {
        return this.ref(false);
    }
    
    private class FMGImpl implements FieldMarshallerGenerator
    {
        private final JVar $idx;
        private final JVar $len;
        
        FMGImpl(final AbstractListFieldRenderer this$0, final JVar _$idx, final JVar _$len) {
            this.this$0 = this$0;
            this.$idx = _$idx;
            this.$len = _$len;
        }
        
        public JExpression hasMore() {
            return this.$idx.ne(this.$len);
        }
        
        public JExpression peek(final boolean increment) {
            JExpression e = increment ? this.$idx.incr() : this.$idx;
            e = this.this$0.ref(true).invoke("get").arg(e);
            if (this.this$0.primitiveType != null) {
                return this.this$0.primitiveType.unwrap(JExpr.cast(this.this$0.primitiveType.getWrapperClass(), e));
            }
            return e;
        }
        
        public void increment(final BlockReference block) {
            block.get(true).assignPlus(this.$idx, JExpr.lit(1));
        }
        
        public FieldMarshallerGenerator clone(final JBlock block, final String uniqueId) {
            final JVar $newidx = block.decl(this.this$0.codeModel.INT, "idx" + uniqueId, this.$idx);
            return (FieldMarshallerGenerator)new FMGImpl(this.this$0, $newidx, this.$len);
        }
        
        public FieldRenderer owner() {
            return (FieldRenderer)this.this$0;
        }
    }
}
