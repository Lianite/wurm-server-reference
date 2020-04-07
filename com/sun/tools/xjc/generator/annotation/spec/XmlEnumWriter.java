// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.annotation.spec;

import com.sun.codemodel.JType;
import javax.xml.bind.annotation.XmlEnum;
import com.sun.codemodel.JAnnotationWriter;

public interface XmlEnumWriter extends JAnnotationWriter<XmlEnum>
{
    XmlEnumWriter value(final Class p0);
    
    XmlEnumWriter value(final JType p0);
}
