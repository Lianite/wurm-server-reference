// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.outline;

import com.sun.codemodel.JEnumConstant;
import com.sun.tools.xjc.model.CEnumConstant;

public abstract class EnumConstantOutline
{
    public final CEnumConstant target;
    public final JEnumConstant constRef;
    
    protected EnumConstantOutline(final CEnumConstant target, final JEnumConstant constRef) {
        this.target = target;
        this.constRef = constRef;
    }
}
