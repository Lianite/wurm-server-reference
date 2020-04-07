// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.annotation.spec;

import javax.xml.bind.annotation.XmlAttribute;
import com.sun.codemodel.JAnnotationWriter;

public interface XmlAttributeWriter extends JAnnotationWriter<XmlAttribute>
{
    XmlAttributeWriter name(final String p0);
    
    XmlAttributeWriter namespace(final String p0);
    
    XmlAttributeWriter required(final boolean p0);
}
