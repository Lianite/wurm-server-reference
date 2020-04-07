// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.cls;

import com.sun.tools.xjc.generator.MethodWriter;
import com.sun.tools.xjc.generator.ClassContext;
import com.sun.codemodel.JClass;
import com.sun.tools.xjc.grammar.ClassItem;
import com.sun.codemodel.JClassContainer;
import org.xml.sax.Locator;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JDefinedClass;
import java.util.HashMap;
import com.sun.tools.xjc.util.CodeModelClassFactory;
import java.util.Map;

public final class PararellStructureStrategy implements ImplStructureStrategy
{
    private final Map intf2impl;
    private final CodeModelClassFactory codeModelClassFactory;
    
    public PararellStructureStrategy(final CodeModelClassFactory _codeModelClassFactory) {
        this.intf2impl = new HashMap();
        this.codeModelClassFactory = _codeModelClassFactory;
    }
    
    private JDefinedClass determineImplClass(final JDefinedClass intf) {
        JDefinedClass d = this.intf2impl.get(intf);
        if (d != null) {
            return d;
        }
        JClassContainer parent = intf.parentContainer();
        int mod = 1;
        if (parent instanceof JPackage) {
            parent = ((JPackage)parent).subPackage("impl");
        }
        else {
            parent = this.determineImplClass((JDefinedClass)parent);
            mod |= 0x10;
        }
        d = this.codeModelClassFactory.createClass(parent, mod, intf.name() + "Impl", (Locator)intf.metadata);
        this.intf2impl.put(intf, d);
        return d;
    }
    
    public JDefinedClass createImplClass(final ClassItem ci) {
        final JDefinedClass impl = this.determineImplClass(ci.getTypeAsDefined());
        impl._implements(ci.getTypeAsDefined());
        impl.method(28, (PararellStructureStrategy.class$java$lang$Class == null) ? (PararellStructureStrategy.class$java$lang$Class = class$("java.lang.Class")) : PararellStructureStrategy.class$java$lang$Class, "PRIMARY_INTERFACE_CLASS").body()._return(ci.getTypeAsDefined().dotclass());
        return impl;
    }
    
    public MethodWriter createMethodWriter(final ClassContext target) {
        return (MethodWriter)new PararellStructureStrategy$1(this, target, target);
    }
}
