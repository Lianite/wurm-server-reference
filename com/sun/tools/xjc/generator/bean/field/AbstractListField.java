// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.bean.field;

import com.sun.codemodel.JOp;
import com.sun.codemodel.JVar;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JAssignmentTarget;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JAnnotatable;
import com.sun.codemodel.JType;
import java.util.List;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JPrimitiveType;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JFieldVar;

abstract class AbstractListField extends AbstractField
{
    protected JFieldVar field;
    private JMethod internalGetter;
    protected final JPrimitiveType primitiveType;
    protected final JClass listT;
    private final boolean eagerInstanciation;
    
    protected AbstractListField(final ClassOutlineImpl outline, final CPropertyInfo prop, final boolean eagerInstanciation) {
        super(outline, prop);
        this.listT = this.codeModel.ref(List.class).narrow(this.exposedType.boxify());
        this.eagerInstanciation = eagerInstanciation;
        if (this.implType instanceof JPrimitiveType) {
            assert this.implType == this.exposedType;
            this.primitiveType = (JPrimitiveType)this.implType;
        }
        else {
            this.primitiveType = null;
        }
    }
    
    protected final void generate() {
        this.field = this.outline.implClass.field(2, this.listT, this.prop.getName(false));
        if (this.eagerInstanciation) {
            this.field.init(this.newCoreList());
        }
        this.annotate(this.field);
        this.generateAccessors();
    }
    
    private void generateInternalGetter() {
        this.internalGetter = this.outline.implClass.method(2, this.listT, "_get" + this.prop.getName(true));
        if (!this.eagerInstanciation) {
            this.fixNullRef(this.internalGetter.body());
        }
        this.internalGetter.body()._return(this.field);
    }
    
    protected final void fixNullRef(final JBlock block) {
        block._if(this.field.eq(JExpr._null()))._then().assign(this.field, this.newCoreList());
    }
    
    public final JType getRawType() {
        return this.codeModel.ref(List.class).narrow(this.exposedType.boxify());
    }
    
    private JExpression newCoreList() {
        return JExpr._new(this.getCoreListType());
    }
    
    protected abstract JClass getCoreListType();
    
    protected abstract void generateAccessors();
    
    protected abstract class Accessor extends AbstractField.Accessor
    {
        protected final JFieldRef field;
        
        protected Accessor(final JExpression $target) {
            super($target);
            this.field = $target.ref(AbstractListField.this.field);
        }
        
        protected final JExpression unbox(final JExpression exp) {
            if (AbstractListField.this.primitiveType == null) {
                return exp;
            }
            return AbstractListField.this.primitiveType.unwrap(exp);
        }
        
        protected final JExpression box(final JExpression exp) {
            if (AbstractListField.this.primitiveType == null) {
                return exp;
            }
            return AbstractListField.this.primitiveType.wrap(exp);
        }
        
        protected final JExpression ref(final boolean canBeNull) {
            if (canBeNull) {
                return this.field;
            }
            if (AbstractListField.this.internalGetter == null) {
                AbstractListField.this.generateInternalGetter();
            }
            return this.$target.invoke(AbstractListField.this.internalGetter);
        }
        
        public JExpression count() {
            return JOp.cond(this.field.eq(JExpr._null()), JExpr.lit(0), this.field.invoke("size"));
        }
        
        public void unsetValues(final JBlock body) {
            body.assign(this.field, JExpr._null());
        }
        
        public JExpression hasSetValue() {
            return this.field.ne(JExpr._null()).cand(this.field.invoke("isEmpty").not());
        }
    }
}
