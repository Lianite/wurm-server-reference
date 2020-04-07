// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema;

import com.sun.codemodel.JDefinedClass;
import org.xml.sax.Locator;

public interface JClassFactory
{
    JDefinedClass create(final String p0, final Locator p1);
    
    JClassFactory getParentFactory();
}
