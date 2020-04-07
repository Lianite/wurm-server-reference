// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.cls;

import com.sun.tools.xjc.generator.MethodWriter;
import com.sun.tools.xjc.generator.ClassContext;
import com.sun.codemodel.JDefinedClass;
import com.sun.tools.xjc.grammar.ClassItem;

public interface ImplStructureStrategy
{
    JDefinedClass createImplClass(final ClassItem p0);
    
    MethodWriter createMethodWriter(final ClassContext p0);
}
