// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.api.impl.s2j;

import com.sun.codemodel.JConditional;
import com.sun.codemodel.JForEach;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JAssignmentTarget;
import com.sun.codemodel.JExpr;
import java.util.ArrayList;
import com.sun.codemodel.JVar;
import com.sun.codemodel.JBlock;
import com.sun.tools.xjc.outline.FieldAccessor;
import com.sun.codemodel.JExpression;
import com.sun.tools.xjc.outline.Aspect;
import java.util.List;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.outline.FieldOutline;

final class ElementCollectionAdapter extends ElementAdapter
{
    public ElementCollectionAdapter(final FieldOutline core, final CElementInfo ei) {
        super(core, ei);
    }
    
    public JType getRawType() {
        return this.codeModel().ref(List.class).narrow(this.itemType().boxify());
    }
    
    private JType itemType() {
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
            final JCodeModel cm = ElementCollectionAdapter.this.outline().getCodeModel();
            final JClass elementType = ElementCollectionAdapter.this.ei.toType(ElementCollectionAdapter.this.outline(), Aspect.EXPOSED).boxify();
            block.assign($var, JExpr._new(cm.ref(ArrayList.class).narrow(ElementCollectionAdapter.this.itemType().boxify())));
            final JVar $col = block.decl(ElementCollectionAdapter.this.core.getRawType(), "col" + this.hashCode());
            this.acc.toRawValue(block, $col);
            final JForEach loop = block.forEach(elementType, "v" + this.hashCode(), $col);
            final JConditional cond = loop.body()._if(loop.var().eq(JExpr._null()));
            cond._then().invoke($var, "add").arg(JExpr._null());
            cond._else().invoke($var, "add").arg(loop.var().invoke("getValue"));
        }
        
        public void fromRawValue(final JBlock block, final String uniqueName, final JExpression $var) {
            final JCodeModel cm = ElementCollectionAdapter.this.outline().getCodeModel();
            final JClass elementType = ElementCollectionAdapter.this.ei.toType(ElementCollectionAdapter.this.outline(), Aspect.EXPOSED).boxify();
            final JClass col = cm.ref(ArrayList.class).narrow(elementType);
            final JVar $t = block.decl(col, uniqueName + "_col", JExpr._new(col));
            final JForEach loop = block.forEach(ElementCollectionAdapter.this.itemType(), uniqueName + "_i", $t);
            loop.body().invoke($var, "add").arg(this.createJAXBElement(loop.var()));
            this.acc.fromRawValue(block, uniqueName, $t);
        }
    }
}
