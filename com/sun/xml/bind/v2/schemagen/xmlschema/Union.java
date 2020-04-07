// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.schemagen.xmlschema;

import com.sun.xml.txw2.annotation.XmlAttribute;
import javax.xml.namespace.QName;
import com.sun.xml.txw2.annotation.XmlElement;
import com.sun.xml.txw2.TypedXmlWriter;

@XmlElement("union")
public interface Union extends Annotated, SimpleTypeHost, TypedXmlWriter
{
    @XmlAttribute
    Union memberTypes(final QName[] p0);
}
