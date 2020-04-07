// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.bean.field;

import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;

public class IsSetFieldRenderer implements FieldRenderer
{
    private final FieldRenderer core;
    private final boolean generateUnSetMethod;
    private final boolean generateIsSetMethod;
    
    public IsSetFieldRenderer(final FieldRenderer core, final boolean generateUnSetMethod, final boolean generateIsSetMethod) {
        this.core = core;
        this.generateUnSetMethod = generateUnSetMethod;
        this.generateIsSetMethod = generateIsSetMethod;
    }
    
    public FieldOutline generate(final ClassOutlineImpl context, final CPropertyInfo prop) {
        return new IsSetField(context, prop, this.core.generate(context, prop), this.generateUnSetMethod, this.generateIsSetMethod);
    }
}
