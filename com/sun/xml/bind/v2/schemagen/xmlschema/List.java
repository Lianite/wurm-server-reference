// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.schemagen.xmlschema;

import com.sun.xml.txw2.annotation.XmlAttribute;
import javax.xml.namespace.QName;
import com.sun.xml.txw2.annotation.XmlElement;
import com.sun.xml.txw2.TypedXmlWriter;

@XmlElement("list")
public interface List extends Annotated, SimpleTypeHost, TypedXmlWriter
{
    @XmlAttribute
    List itemType(final QName p0);
}
