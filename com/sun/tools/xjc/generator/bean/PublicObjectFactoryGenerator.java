// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.bean;

import com.sun.codemodel.JClass;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.codemodel.JPackage;
import com.sun.tools.xjc.model.Model;

final class PublicObjectFactoryGenerator extends ObjectFactoryGeneratorImpl
{
    public PublicObjectFactoryGenerator(final BeanGenerator outline, final Model model, final JPackage targetPackage) {
        super(outline, model, targetPackage);
    }
    
    void populate(final CElementInfo ei) {
        this.populate(ei, Aspect.IMPLEMENTATION, Aspect.EXPOSED);
    }
    
    void populate(final ClassOutlineImpl cc) {
        this.populate(cc, cc.ref);
    }
}
