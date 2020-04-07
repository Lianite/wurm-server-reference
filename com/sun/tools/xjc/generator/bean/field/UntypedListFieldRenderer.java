// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.bean.field;

import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;
import com.sun.codemodel.JClass;

public final class UntypedListFieldRenderer implements FieldRenderer
{
    private JClass coreList;
    
    protected UntypedListFieldRenderer(final JClass coreList) {
        this.coreList = coreList;
    }
    
    public FieldOutline generate(final ClassOutlineImpl context, final CPropertyInfo prop) {
        return new UntypedListField(context, prop, this.coreList);
    }
}
