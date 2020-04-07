// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.schemagen.xmlschema;

import com.sun.xml.txw2.annotation.XmlAttribute;
import com.sun.xml.txw2.annotation.XmlElement;
import com.sun.xml.txw2.TypedXmlWriter;

@XmlElement("simpleType")
public interface SimpleType extends Annotated, SimpleDerivation, TypedXmlWriter
{
    @XmlAttribute("final")
    SimpleType _final(final String p0);
    
    @XmlAttribute("final")
    SimpleType _final(final String[] p0);
    
    @XmlAttribute
    SimpleType name(final String p0);
}
