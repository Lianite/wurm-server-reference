// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.annotation.spec;

import com.sun.codemodel.JType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import com.sun.codemodel.JAnnotationWriter;

public interface XmlJavaTypeAdapterWriter extends JAnnotationWriter<XmlJavaTypeAdapter>
{
    XmlJavaTypeAdapterWriter value(final Class p0);
    
    XmlJavaTypeAdapterWriter value(final JType p0);
    
    XmlJavaTypeAdapterWriter type(final Class p0);
    
    XmlJavaTypeAdapterWriter type(final JType p0);
}
