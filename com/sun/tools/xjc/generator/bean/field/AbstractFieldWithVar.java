// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.bean.field;

import com.sun.codemodel.JAssignmentTarget;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JVar;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JType;
import com.sun.codemodel.JAnnotatable;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;
import com.sun.codemodel.JFieldVar;

abstract class AbstractFieldWithVar extends AbstractField
{
    private JFieldVar field;
    
    AbstractFieldWithVar(final ClassOutlineImpl outline, final CPropertyInfo prop) {
        super(outline, prop);
    }
    
    protected final void createField() {
        this.annotate(this.field = this.outline.implClass.field(2, this.getFieldType(), this.prop.getName(false)));
    }
    
    protected String getGetterMethod() {
        return ((this.getFieldType().boxify().getPrimitiveType() == this.codeModel.BOOLEAN) ? "is" : "get") + this.prop.getName(true);
    }
    
    protected abstract JType getFieldType();
    
    protected JFieldVar ref() {
        return this.field;
    }
    
    public final JType getRawType() {
        return this.exposedType;
    }
    
    protected abstract class Accessor extends AbstractField.Accessor
    {
        protected final JFieldRef $ref;
        
        protected Accessor(final JExpression $target) {
            super($target);
            this.$ref = $target.ref(AbstractFieldWithVar.this.ref());
        }
        
        public final void toRawValue(final JBlock block, final JVar $var) {
            block.assign($var, this.$target.invoke(AbstractFieldWithVar.this.getGetterMethod()));
        }
        
        public final void fromRawValue(final JBlock block, final String uniqueName, final JExpression $var) {
            block.invoke(this.$target, "set" + AbstractFieldWithVar.this.prop.getName(true)).arg($var);
        }
    }
}
