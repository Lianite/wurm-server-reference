// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.bindinfo;

import com.sun.codemodel.JDefinedClass;

public class BIXSuperClass
{
    private final JDefinedClass cls;
    
    public BIXSuperClass(final JDefinedClass _cls) {
        (this.cls = _cls).hide();
    }
    
    public JDefinedClass getRootClass() {
        return this.cls;
    }
}
