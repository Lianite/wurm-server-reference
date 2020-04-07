// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.annotation.spec;

import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import com.sun.codemodel.JAnnotationWriter;

public interface XmlSchemaWriter extends JAnnotationWriter<XmlSchema>
{
    XmlSchemaWriter location(final String p0);
    
    XmlSchemaWriter namespace(final String p0);
    
    XmlNsWriter xmlns();
    
    XmlSchemaWriter elementFormDefault(final XmlNsForm p0);
    
    XmlSchemaWriter attributeFormDefault(final XmlNsForm p0);
}
