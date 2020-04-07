// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.schemagen.xmlschema;

import com.sun.xml.txw2.annotation.XmlAttribute;
import com.sun.xml.txw2.annotation.XmlElement;
import com.sun.xml.txw2.TypedXmlWriter;

@XmlElement("import")
public interface Import extends Annotated, TypedXmlWriter
{
    @XmlAttribute
    Import namespace(final String p0);
    
    @XmlAttribute
    Import schemaLocation(final String p0);
}
