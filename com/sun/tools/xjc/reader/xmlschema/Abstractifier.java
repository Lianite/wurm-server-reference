// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema;

import com.sun.xml.xsom.XSElementDecl;
import com.sun.tools.xjc.model.CElement;
import com.sun.xml.xsom.XSComplexType;

class Abstractifier extends ClassBinderFilter
{
    public Abstractifier(final ClassBinder core) {
        super(core);
    }
    
    public CElement complexType(final XSComplexType xs) {
        final CElement ci = super.complexType(xs);
        if (ci != null && xs.isAbstract()) {
            ci.setAbstract();
        }
        return ci;
    }
    
    public CElement elementDecl(final XSElementDecl xs) {
        final CElement ci = super.elementDecl(xs);
        if (ci != null && xs.isAbstract()) {
            ci.setAbstract();
        }
        return ci;
    }
}
