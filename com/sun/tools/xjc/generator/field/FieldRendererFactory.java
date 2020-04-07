// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.field;

import com.sun.tools.xjc.grammar.FieldUse;
import com.sun.tools.xjc.generator.ClassContext;

public interface FieldRendererFactory
{
    FieldRenderer create(final ClassContext p0, final FieldUse p1);
}
