// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.schemagen.xmlschema;

import com.sun.xml.txw2.annotation.XmlAttribute;
import com.sun.xml.txw2.annotation.XmlElement;
import com.sun.xml.txw2.TypedXmlWriter;

@XmlElement("annotation")
public interface Annotation extends TypedXmlWriter
{
    @XmlElement
    Appinfo appinfo();
    
    @XmlElement
    Documentation documentation();
    
    @XmlAttribute
    Annotation id(final String p0);
}
