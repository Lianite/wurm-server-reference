// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.outline;

import java.util.ArrayList;
import com.sun.istack.NotNull;
import java.util.List;
import com.sun.codemodel.JDefinedClass;
import com.sun.tools.xjc.model.CEnumLeafInfo;

public abstract class EnumOutline
{
    public final CEnumLeafInfo target;
    public final JDefinedClass clazz;
    public final List<EnumConstantOutline> constants;
    
    @NotNull
    public PackageOutline _package() {
        return this.parent().getPackageContext(this.clazz._package());
    }
    
    @NotNull
    public abstract Outline parent();
    
    protected EnumOutline(final CEnumLeafInfo target, final JDefinedClass clazz) {
        this.constants = new ArrayList<EnumConstantOutline>();
        this.target = target;
        this.clazz = clazz;
    }
}
