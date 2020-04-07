// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.bean;

import com.sun.codemodel.JDefinedClass;
import com.sun.tools.xjc.model.CElementInfo;

public abstract class ObjectFactoryGenerator
{
    abstract void populate(final CElementInfo p0);
    
    abstract void populate(final ClassOutlineImpl p0);
    
    public abstract JDefinedClass getObjectFactory();
}
