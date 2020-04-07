// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.annotation.spec;

import com.sun.codemodel.JType;
import javax.xml.bind.annotation.XmlElement;
import com.sun.codemodel.JAnnotationWriter;

public interface XmlElementWriter extends JAnnotationWriter<XmlElement>
{
    XmlElementWriter name(final String p0);
    
    XmlElementWriter type(final Class p0);
    
    XmlElementWriter type(final JType p0);
    
    XmlElementWriter namespace(final String p0);
    
    XmlElementWriter defaultValue(final String p0);
    
    XmlElementWriter required(final boolean p0);
    
    XmlElementWriter nillable(final boolean p0);
}
