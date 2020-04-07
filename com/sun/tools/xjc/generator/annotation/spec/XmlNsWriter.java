// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.annotation.spec;

import javax.xml.bind.annotation.XmlNs;
import com.sun.codemodel.JAnnotationWriter;

public interface XmlNsWriter extends JAnnotationWriter<XmlNs>
{
    XmlNsWriter prefix(final String p0);
    
    XmlNsWriter namespaceURI(final String p0);
}
