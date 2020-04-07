// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.annotation.spec;

import com.sun.codemodel.JType;
import javax.xml.bind.annotation.XmlSeeAlso;
import com.sun.codemodel.JAnnotationWriter;

public interface XmlSeeAlsoWriter extends JAnnotationWriter<XmlSeeAlso>
{
    XmlSeeAlsoWriter value(final Class p0);
    
    XmlSeeAlsoWriter value(final JType p0);
}
