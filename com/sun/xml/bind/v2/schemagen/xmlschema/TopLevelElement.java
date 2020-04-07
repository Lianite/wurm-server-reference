// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.schemagen.xmlschema;

import javax.xml.namespace.QName;
import com.sun.xml.txw2.annotation.XmlAttribute;
import com.sun.xml.txw2.annotation.XmlElement;
import com.sun.xml.txw2.TypedXmlWriter;

@XmlElement("element")
public interface TopLevelElement extends Element, TypedXmlWriter
{
    @XmlAttribute("final")
    TopLevelElement _final(final String p0);
    
    @XmlAttribute("final")
    TopLevelElement _final(final String[] p0);
    
    @XmlAttribute("abstract")
    TopLevelElement _abstract(final boolean p0);
    
    @XmlAttribute
    TopLevelElement substitutionGroup(final QName p0);
    
    @XmlAttribute
    TopLevelElement name(final String p0);
}
