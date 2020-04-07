// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator;

import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.tools.xjc.generator.cls.ImplStructureStrategy;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JClass;
import com.sun.tools.xjc.grammar.ClassItem;

public final class ClassContext
{
    public final GeneratorContext parent;
    public final PackageContext _package;
    public final ClassItem target;
    public final JClass implRef;
    public final JDefinedClass implClass;
    public final JDefinedClass ref;
    private final ImplStructureStrategy strategy;
    
    public MethodWriter createMethodWriter() {
        return this.strategy.createMethodWriter(this);
    }
    
    protected ClassContext(final GeneratorContext _parent, final ImplStructureStrategy _strategy, final ClassItem _target) {
        this.parent = _parent;
        this.target = _target;
        this.strategy = _strategy;
        this.ref = _target.getTypeAsDefined();
        this._package = this.parent.getPackageContext(this.ref._package());
        this.implClass = _strategy.createImplClass(_target);
        if (this.target.getUserSpecifiedImplClass() != null) {
            JDefinedClass usr;
            try {
                usr = this.parent.getCodeModel()._class(this.target.getUserSpecifiedImplClass());
                usr.hide();
            }
            catch (JClassAlreadyExistsException e) {
                usr = e.getExistingClass();
            }
            usr._extends(this.implClass);
            this.implRef = usr;
        }
        else {
            this.implRef = this.implClass;
        }
    }
}
