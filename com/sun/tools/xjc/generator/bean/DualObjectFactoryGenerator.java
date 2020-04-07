// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.bean;

import com.sun.codemodel.JDefinedClass;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JPackage;
import com.sun.tools.xjc.model.Model;

public final class DualObjectFactoryGenerator extends ObjectFactoryGenerator
{
    public final ObjectFactoryGenerator publicOFG;
    public final ObjectFactoryGenerator privateOFG;
    
    DualObjectFactoryGenerator(final BeanGenerator outline, final Model model, final JPackage targetPackage) {
        this.publicOFG = new PublicObjectFactoryGenerator(outline, model, targetPackage);
        this.privateOFG = new PrivateObjectFactoryGenerator(outline, model, targetPackage);
        this.publicOFG.getObjectFactory().field(28, Void.class, "_useJAXBProperties", JExpr._null());
    }
    
    void populate(final CElementInfo ei) {
        this.publicOFG.populate(ei);
        this.privateOFG.populate(ei);
    }
    
    void populate(final ClassOutlineImpl cc) {
        this.publicOFG.populate(cc);
        this.privateOFG.populate(cc);
    }
    
    public JDefinedClass getObjectFactory() {
        return this.privateOFG.getObjectFactory();
    }
}
