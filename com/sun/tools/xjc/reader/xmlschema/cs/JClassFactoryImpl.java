// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.cs;

import com.sun.codemodel.JDefinedClass;
import org.xml.sax.Locator;
import com.sun.codemodel.JClassContainer;
import com.sun.tools.xjc.reader.xmlschema.JClassFactory;

class JClassFactoryImpl implements JClassFactory
{
    private final ClassSelector owner;
    private final JClassFactory parent;
    private final JClassContainer container;
    
    JClassFactoryImpl(final ClassSelector owner, final JClassContainer _cont) {
        this.parent = owner.getClassFactory();
        this.container = _cont;
        this.owner = owner;
    }
    
    public JDefinedClass create(final String name, final Locator sourceLocation) {
        return this.owner.codeModelClassFactory.createInterface(this.container, name, sourceLocation);
    }
    
    public JClassFactory getParentFactory() {
        return this.parent;
    }
}
