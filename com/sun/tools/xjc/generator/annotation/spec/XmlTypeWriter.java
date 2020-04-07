// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.annotation.spec;

import com.sun.codemodel.JType;
import javax.xml.bind.annotation.XmlType;
import com.sun.codemodel.JAnnotationWriter;

public interface XmlTypeWriter extends JAnnotationWriter<XmlType>
{
    XmlTypeWriter name(final String p0);
    
    XmlTypeWriter namespace(final String p0);
    
    XmlTypeWriter propOrder(final String p0);
    
    XmlTypeWriter factoryClass(final Class p0);
    
    XmlTypeWriter factoryClass(final JType p0);
    
    XmlTypeWriter factoryMethod(final String p0);
}
