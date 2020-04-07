// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.api.impl.s2j;

import com.sun.codemodel.JConditional;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JAssignmentTarget;
import com.sun.codemodel.JVar;
import com.sun.codemodel.JBlock;
import com.sun.tools.xjc.outline.FieldAccessor;
import com.sun.codemodel.JExpression;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.outline.FieldOutline;

final class ElementSingleAdapter extends ElementAdapter
{
    public ElementSingleAdapter(final FieldOutline core, final CElementInfo ei) {
        super(core, ei);
    }
    
    public JType getRawType() {
        return this.ei.getContentInMemoryType().toType(this.outline(), Aspect.EXPOSED);
    }
    
    public FieldAccessor create(final JExpression targetObject) {
        return new FieldAccessorImpl(targetObject);
    }
    
    final class FieldAccessorImpl extends ElementAdapter.FieldAccessorImpl
    {
        public FieldAccessorImpl(final JExpression target) {
            super(target);
        }
        
        public void toRawValue(final JBlock block, final JVar $var) {
            final JConditional cond = block._if(this.acc.hasSetValue());
            final JVar $v = cond._then().decl(ElementSingleAdapter.this.core.getRawType(), "v" + this.hashCode());
            this.acc.toRawValue(cond._then(), $v);
            cond._then().assign($var, $v.invoke("getValue"));
            cond._else().assign($var, JExpr._null());
        }
        
        public void fromRawValue(final JBlock block, final String uniqueName, final JExpression $var) {
            this.acc.fromRawValue(block, uniqueName, this.createJAXBElement($var));
        }
    }
}
