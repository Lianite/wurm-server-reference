// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.outline;

import com.sun.codemodel.JDefinedClass;
import com.sun.tools.xjc.model.CElementInfo;

public abstract class ElementOutline
{
    public final CElementInfo target;
    public final JDefinedClass implClass;
    
    public abstract Outline parent();
    
    public PackageOutline _package() {
        return this.parent().getPackageContext(this.implClass._package());
    }
    
    protected ElementOutline(final CElementInfo target, final JDefinedClass implClass) {
        this.target = target;
        this.implClass = implClass;
    }
}
