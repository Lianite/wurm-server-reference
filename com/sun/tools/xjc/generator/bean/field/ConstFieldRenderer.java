// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.bean.field;

import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;

final class ConstFieldRenderer implements FieldRenderer
{
    private final FieldRenderer fallback;
    
    protected ConstFieldRenderer(final FieldRenderer fallback) {
        this.fallback = fallback;
    }
    
    public FieldOutline generate(final ClassOutlineImpl outline, final CPropertyInfo prop) {
        if (prop.defaultValue.compute(outline.parent()) == null) {
            return this.fallback.generate(outline, prop);
        }
        return new ConstField(outline, prop);
    }
}
