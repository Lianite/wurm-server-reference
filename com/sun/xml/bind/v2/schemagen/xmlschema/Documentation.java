// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.schemagen.xmlschema;

import com.sun.xml.txw2.annotation.XmlAttribute;
import com.sun.xml.txw2.annotation.XmlElement;
import com.sun.xml.txw2.TypedXmlWriter;

@XmlElement("documentation")
public interface Documentation extends TypedXmlWriter
{
    @XmlAttribute
    Documentation source(final String p0);
    
    @XmlAttribute(ns = "http://www.w3.org/XML/1998/namespace")
    Documentation lang(final String p0);
}
