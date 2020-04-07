// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.outline;

import java.util.List;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.istack.NotNull;
import com.sun.tools.xjc.model.CClassInfo;

public abstract class ClassOutline
{
    @NotNull
    public final CClassInfo target;
    @NotNull
    public final JDefinedClass ref;
    @NotNull
    public final JDefinedClass implClass;
    @NotNull
    public final JClass implRef;
    
    @NotNull
    public abstract Outline parent();
    
    @NotNull
    public PackageOutline _package() {
        return this.parent().getPackageContext(this.ref._package());
    }
    
    protected ClassOutline(final CClassInfo _target, final JDefinedClass exposedClass, final JClass implRef, final JDefinedClass _implClass) {
        this.target = _target;
        this.ref = exposedClass;
        this.implRef = implRef;
        this.implClass = _implClass;
    }
    
    public final FieldOutline[] getDeclaredFields() {
        final List<CPropertyInfo> props = this.target.getProperties();
        final FieldOutline[] fr = new FieldOutline[props.size()];
        for (int i = 0; i < fr.length; ++i) {
            fr[i] = this.parent().getField(props.get(i));
        }
        return fr;
    }
    
    public final ClassOutline getSuperClass() {
        final CClassInfo s = this.target.getBaseClass();
        if (s == null) {
            return null;
        }
        return this.parent().getClazz(s);
    }
}
