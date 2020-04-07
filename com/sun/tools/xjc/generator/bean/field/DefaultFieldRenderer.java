// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.bean.field;

import java.util.ArrayList;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;

final class DefaultFieldRenderer implements FieldRenderer
{
    private final FieldRendererFactory frf;
    private FieldRenderer defaultCollectionFieldRenderer;
    
    DefaultFieldRenderer(final FieldRendererFactory frf) {
        this.frf = frf;
    }
    
    public DefaultFieldRenderer(final FieldRendererFactory frf, final FieldRenderer defaultCollectionFieldRenderer) {
        this.frf = frf;
        this.defaultCollectionFieldRenderer = defaultCollectionFieldRenderer;
    }
    
    public FieldOutline generate(final ClassOutlineImpl outline, final CPropertyInfo prop) {
        return this.decideRenderer(outline, prop).generate(outline, prop);
    }
    
    private FieldRenderer decideRenderer(final ClassOutlineImpl outline, final CPropertyInfo prop) {
        if (!prop.isCollection()) {
            if (prop.isUnboxable()) {
                return this.frf.getRequiredUnboxed();
            }
            return this.frf.getSingle();
        }
        else {
            if (this.defaultCollectionFieldRenderer == null) {
                return this.frf.getList(outline.parent().getCodeModel().ref(ArrayList.class));
            }
            return this.defaultCollectionFieldRenderer;
        }
    }
}
