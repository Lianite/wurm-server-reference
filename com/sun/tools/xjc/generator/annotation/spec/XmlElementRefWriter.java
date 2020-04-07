// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.annotation.spec;

import com.sun.codemodel.JType;
import javax.xml.bind.annotation.XmlElementRef;
import com.sun.codemodel.JAnnotationWriter;

public interface XmlElementRefWriter extends JAnnotationWriter<XmlElementRef>
{
    XmlElementRefWriter name(final String p0);
    
    XmlElementRefWriter type(final Class p0);
    
    XmlElementRefWriter type(final JType p0);
    
    XmlElementRefWriter namespace(final String p0);
}
