// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.annotation.spec;

import javax.xml.bind.annotation.XmlElementWrapper;
import com.sun.codemodel.JAnnotationWriter;

public interface XmlElementWrapperWriter extends JAnnotationWriter<XmlElementWrapper>
{
    XmlElementWrapperWriter name(final String p0);
    
    XmlElementWrapperWriter namespace(final String p0);
    
    XmlElementWrapperWriter required(final boolean p0);
    
    XmlElementWrapperWriter nillable(final boolean p0);
}
