// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.bean.field;

import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;

public class SinglePrimitiveAccessField extends SingleField
{
    protected SinglePrimitiveAccessField(final ClassOutlineImpl context, final CPropertyInfo prop) {
        super(context, prop, true);
    }
}
