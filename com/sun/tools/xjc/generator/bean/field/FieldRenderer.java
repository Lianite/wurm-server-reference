// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.bean.field;

import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;

public interface FieldRenderer
{
    FieldOutline generate(final ClassOutlineImpl p0, final CPropertyInfo p1);
}
