// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model.nav;

import com.sun.codemodel.JType;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.outline.Outline;
import com.sun.codemodel.JClass;

class NClassByJClass implements NClass
{
    final JClass clazz;
    
    NClassByJClass(final JClass clazz) {
        this.clazz = clazz;
    }
    
    public JClass toType(final Outline o, final Aspect aspect) {
        return this.clazz;
    }
    
    public boolean isAbstract() {
        return this.clazz.isAbstract();
    }
    
    public boolean isBoxedType() {
        return this.clazz.getPrimitiveType() != null;
    }
    
    public String fullName() {
        return this.clazz.fullName();
    }
}
