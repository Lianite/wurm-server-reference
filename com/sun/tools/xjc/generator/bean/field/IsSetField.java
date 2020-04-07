// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.bean.field;

import com.sun.codemodel.JVar;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpression;
import com.sun.tools.xjc.outline.FieldAccessor;
import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.generator.bean.MethodWriter;
import com.sun.codemodel.JType;
import com.sun.codemodel.JExpr;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.xjc.outline.FieldOutline;

public class IsSetField extends AbstractField
{
    private final FieldOutline core;
    private final boolean generateUnSetMethod;
    private final boolean generateIsSetMethod;
    
    protected IsSetField(final ClassOutlineImpl outline, final CPropertyInfo prop, final FieldOutline core, final boolean unsetMethod, final boolean issetMethod) {
        super(outline, prop);
        this.core = core;
        this.generateIsSetMethod = issetMethod;
        this.generateUnSetMethod = unsetMethod;
        this.generate(outline, prop);
    }
    
    private void generate(final ClassOutlineImpl outline, final CPropertyInfo prop) {
        final MethodWriter writer = outline.createMethodWriter();
        final JCodeModel codeModel = outline.parent().getCodeModel();
        final FieldAccessor acc = this.core.create(JExpr._this());
        if (this.generateIsSetMethod) {
            final JExpression hasSetValue = acc.hasSetValue();
            if (hasSetValue == null) {
                throw new UnsupportedOperationException();
            }
            writer.declareMethod(codeModel.BOOLEAN, "isSet" + this.prop.getName(true)).body()._return(hasSetValue);
        }
        if (this.generateUnSetMethod) {
            acc.unsetValues(writer.declareMethod(codeModel.VOID, "unset" + this.prop.getName(true)).body());
        }
    }
    
    public JType getRawType() {
        return this.core.getRawType();
    }
    
    public FieldAccessor create(final JExpression targetObject) {
        return new Accessor(targetObject);
    }
    
    private class Accessor extends AbstractField.Accessor
    {
        private final FieldAccessor core;
        
        Accessor(final JExpression $target) {
            super($target);
            this.core = IsSetField.this.core.create($target);
        }
        
        public void unsetValues(final JBlock body) {
            this.core.unsetValues(body);
        }
        
        public JExpression hasSetValue() {
            return this.core.hasSetValue();
        }
        
        public void toRawValue(final JBlock block, final JVar $var) {
            this.core.toRawValue(block, $var);
        }
        
        public void fromRawValue(final JBlock block, final String uniqueName, final JExpression $var) {
            this.core.fromRawValue(block, uniqueName, $var);
        }
    }
}
