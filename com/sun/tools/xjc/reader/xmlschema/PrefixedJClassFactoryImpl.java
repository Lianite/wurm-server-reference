// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema;

import org.xml.sax.Locator;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JClassContainer;

public class PrefixedJClassFactoryImpl implements JClassFactory
{
    private final JClassFactory parent;
    private final String prefix;
    private final JClassContainer pkg;
    private final BGMBuilder builder;
    
    public PrefixedJClassFactoryImpl(final BGMBuilder builder, final JDefinedClass parentClass) {
        this.builder = builder;
        this.parent = builder.selector.getClassFactory();
        this.prefix = parentClass.name();
        this.pkg = parentClass.parentContainer();
    }
    
    public PrefixedJClassFactoryImpl(final BGMBuilder builder, final XSModelGroupDecl decl) {
        if (decl.isLocal()) {
            throw new IllegalArgumentException();
        }
        this.builder = builder;
        this.parent = builder.selector.getClassFactory();
        this.prefix = builder.getNameConverter().toClassName(decl.getName());
        this.pkg = builder.selector.getPackage(decl.getTargetNamespace());
    }
    
    public JDefinedClass create(final String name, final Locator sourceLocation) {
        return this.builder.selector.codeModelClassFactory.createInterface(this.pkg, this.prefix + name, sourceLocation);
    }
    
    public JClassFactory getParentFactory() {
        return this.parent;
    }
}
