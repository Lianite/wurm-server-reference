// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.annotation.spec;

import javax.xml.bind.annotation.XmlRootElement;
import com.sun.codemodel.JAnnotationWriter;

public interface XmlRootElementWriter extends JAnnotationWriter<XmlRootElement>
{
    XmlRootElementWriter name(final String p0);
    
    XmlRootElementWriter namespace(final String p0);
}
