// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.bean;

import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.outline.PackageOutline;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.outline.ClassOutline;

public final class ClassOutlineImpl extends ClassOutline
{
    private final BeanGenerator _parent;
    
    public MethodWriter createMethodWriter() {
        return this._parent.getModel().strategy.createMethodWriter(this);
    }
    
    public PackageOutlineImpl _package() {
        return (PackageOutlineImpl)super._package();
    }
    
    ClassOutlineImpl(final BeanGenerator _parent, final CClassInfo _target, final JDefinedClass exposedClass, final JDefinedClass _implClass, final JClass _implRef) {
        super(_target, exposedClass, _implRef, _implClass);
        this._parent = _parent;
        this._package().classes.add(this);
    }
    
    public BeanGenerator parent() {
        return this._parent;
    }
}
