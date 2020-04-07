// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.bean;

import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpression;
import javax.xml.namespace.QName;
import com.sun.codemodel.JType;
import com.sun.codemodel.JExpr;
import com.sun.tools.xjc.outline.Outline;
import javax.xml.bind.JAXBElement;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.outline.ElementOutline;

final class ElementOutlineImpl extends ElementOutline
{
    private final BeanGenerator parent;
    
    public BeanGenerator parent() {
        return this.parent;
    }
    
    ElementOutlineImpl(final BeanGenerator parent, final CElementInfo ei) {
        super(ei, parent.getClassFactory().createClass(parent.getContainer(ei.parent, Aspect.EXPOSED), ei.shortName(), ei.getLocator()));
        this.parent = parent;
        parent.elements.put(ei, this);
        final JCodeModel cm = parent.getCodeModel();
        this.implClass._extends(cm.ref(JAXBElement.class).narrow(this.target.getContentInMemoryType().toType(parent, Aspect.EXPOSED).boxify()));
        if (ei.hasClass()) {
            final JType implType = ei.getContentInMemoryType().toType(parent, Aspect.IMPLEMENTATION);
            final JExpression declaredType = JExpr.cast(cm.ref(Class.class), implType.boxify().dotclass());
            JClass scope = null;
            if (ei.getScope() != null) {
                scope = parent.getClazz(ei.getScope()).implRef;
            }
            final JExpression scopeClass = (scope == null) ? JExpr._null() : scope.dotclass();
            final JMethod cons = this.implClass.constructor(1);
            cons.body().invoke("super").arg(this.implClass.field(26, QName.class, "NAME", this.createQName(cm, ei.getElementName()))).arg(declaredType).arg(scopeClass).arg(cons.param(implType, "value"));
        }
    }
    
    private JInvocation createQName(final JCodeModel codeModel, final QName name) {
        return JExpr._new(codeModel.ref(QName.class)).arg(name.getNamespaceURI()).arg(name.getLocalPart());
    }
}
