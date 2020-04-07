// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.schemagen.xmlschema;

import com.sun.xml.txw2.annotation.XmlAttribute;
import com.sun.xml.txw2.annotation.XmlElement;
import com.sun.xml.txw2.TypedXmlWriter;

@XmlElement("complexContent")
public interface ComplexContent extends Annotated, TypedXmlWriter
{
    @XmlElement
    ComplexExtension extension();
    
    @XmlElement
    ComplexRestriction restriction();
    
    @XmlAttribute
    ComplexContent mixed(final boolean p0);
}
