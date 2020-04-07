// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.bean;

import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JResourceFile;
import com.sun.codemodel.fmt.JPropertyFile;
import com.sun.tools.xjc.runtime.JAXBContextFactory;
import com.sun.codemodel.JPackage;
import com.sun.tools.xjc.model.Model;

final class PrivateObjectFactoryGenerator extends ObjectFactoryGeneratorImpl
{
    public PrivateObjectFactoryGenerator(final BeanGenerator outline, final Model model, final JPackage targetPackage) {
        super(outline, model, targetPackage.subPackage("impl"));
        final JPackage implPkg = targetPackage.subPackage("impl");
        final JClass factory = outline.generateStaticClass(JAXBContextFactory.class, implPkg);
        final JPropertyFile jaxbProperties = new JPropertyFile("jaxb.properties");
        targetPackage.addResourceFile(jaxbProperties);
        jaxbProperties.add("javax.xml.bind.context.factory", factory.fullName());
    }
    
    void populate(final CElementInfo ei) {
        this.populate(ei, Aspect.IMPLEMENTATION, Aspect.IMPLEMENTATION);
    }
    
    void populate(final ClassOutlineImpl cc) {
        this.populate(cc, cc.implRef);
    }
}
