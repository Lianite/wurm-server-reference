// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.bean.field;

import com.sun.codemodel.JVar;
import com.sun.codemodel.JBlock;
import com.sun.tools.xjc.outline.FieldAccessor;
import com.sun.codemodel.JType;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JPrimitiveType;
import com.sun.codemodel.JAnnotatable;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;
import com.sun.codemodel.JFieldVar;

final class ConstField extends AbstractField
{
    private final JFieldVar $ref;
    
    ConstField(final ClassOutlineImpl outline, final CPropertyInfo prop) {
        super(outline, prop);
        assert !prop.isCollection();
        final JPrimitiveType ptype = this.implType.boxify().getPrimitiveType();
        JExpression defaultValue = null;
        if (prop.defaultValue != null) {
            defaultValue = prop.defaultValue.compute(outline.parent());
        }
        this.$ref = outline.ref.field(25, (ptype != null) ? ptype : this.implType, prop.getName(true), defaultValue);
        this.$ref.javadoc().append(prop.javadoc);
        this.annotate(this.$ref);
    }
    
    public JType getRawType() {
        return this.exposedType;
    }
    
    public FieldAccessor create(final JExpression target) {
        return new Accessor(target);
    }
    
    private class Accessor extends AbstractField.Accessor
    {
        Accessor(final JExpression $target) {
            super($target);
        }
        
        public void unsetValues(final JBlock body) {
        }
        
        public JExpression hasSetValue() {
            return null;
        }
        
        public void toRawValue(final JBlock block, final JVar $var) {
            throw new UnsupportedOperationException();
        }
        
        public void fromRawValue(final JBlock block, final String uniqueName, final JExpression $var) {
            throw new UnsupportedOperationException();
        }
    }
}
