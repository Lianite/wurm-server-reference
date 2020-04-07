// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.api.impl.s2j;

import javax.xml.namespace.QName;
import com.sun.codemodel.JExpr;
import javax.xml.bind.JAXBElement;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpression;
import com.sun.tools.xjc.outline.FieldAccessor;
import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.outline.FieldOutline;

abstract class ElementAdapter implements FieldOutline
{
    protected final FieldOutline core;
    protected final CElementInfo ei;
    
    public ElementAdapter(final FieldOutline core, final CElementInfo ei) {
        this.core = core;
        this.ei = ei;
    }
    
    public ClassOutline parent() {
        return this.core.parent();
    }
    
    public CPropertyInfo getPropertyInfo() {
        return this.core.getPropertyInfo();
    }
    
    protected final Outline outline() {
        return this.core.parent().parent();
    }
    
    protected final JCodeModel codeModel() {
        return this.outline().getCodeModel();
    }
    
    protected abstract class FieldAccessorImpl implements FieldAccessor
    {
        final FieldAccessor acc;
        
        public FieldAccessorImpl(final JExpression target) {
            this.acc = ElementAdapter.this.core.create(target);
        }
        
        public void unsetValues(final JBlock body) {
            this.acc.unsetValues(body);
        }
        
        public JExpression hasSetValue() {
            return this.acc.hasSetValue();
        }
        
        public FieldOutline owner() {
            return ElementAdapter.this;
        }
        
        public CPropertyInfo getPropertyInfo() {
            return ElementAdapter.this.core.getPropertyInfo();
        }
        
        protected final JInvocation createJAXBElement(final JExpression $var) {
            final JCodeModel cm = ElementAdapter.this.codeModel();
            return JExpr._new(cm.ref(JAXBElement.class)).arg(JExpr._new(cm.ref(QName.class)).arg(ElementAdapter.this.ei.getElementName().getNamespaceURI()).arg(ElementAdapter.this.ei.getElementName().getLocalPart())).arg(ElementAdapter.this.getRawType().boxify().erasure().dotclass()).arg($var);
        }
    }
}
