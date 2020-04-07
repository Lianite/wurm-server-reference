// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.annotation.spec;

import com.sun.codemodel.JType;
import javax.xml.bind.annotation.XmlAnyElement;
import com.sun.codemodel.JAnnotationWriter;

public interface XmlAnyElementWriter extends JAnnotationWriter<XmlAnyElement>
{
    XmlAnyElementWriter value(final Class p0);
    
    XmlAnyElementWriter value(final JType p0);
    
    XmlAnyElementWriter lax(final boolean p0);
}
