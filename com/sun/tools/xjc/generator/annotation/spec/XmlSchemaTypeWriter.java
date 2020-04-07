// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.annotation.spec;

import com.sun.codemodel.JType;
import javax.xml.bind.annotation.XmlSchemaType;
import com.sun.codemodel.JAnnotationWriter;

public interface XmlSchemaTypeWriter extends JAnnotationWriter<XmlSchemaType>
{
    XmlSchemaTypeWriter name(final String p0);
    
    XmlSchemaTypeWriter type(final Class p0);
    
    XmlSchemaTypeWriter type(final JType p0);
    
    XmlSchemaTypeWriter namespace(final String p0);
}
