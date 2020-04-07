// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.schemagen.xmlschema;

import javax.xml.namespace.QName;
import com.sun.xml.txw2.annotation.XmlAttribute;
import com.sun.xml.txw2.annotation.XmlElement;
import com.sun.xml.txw2.TypedXmlWriter;

@XmlElement("element")
public interface LocalElement extends Element, Occurs, TypedXmlWriter
{
    @XmlAttribute
    LocalElement form(final String p0);
    
    @XmlAttribute
    LocalElement name(final String p0);
    
    @XmlAttribute
    LocalElement ref(final QName p0);
}
