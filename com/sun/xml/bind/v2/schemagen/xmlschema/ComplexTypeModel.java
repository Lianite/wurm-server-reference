// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.schemagen.xmlschema;

import com.sun.xml.txw2.annotation.XmlAttribute;
import com.sun.xml.txw2.annotation.XmlElement;
import com.sun.xml.txw2.TypedXmlWriter;

public interface ComplexTypeModel extends AttrDecls, TypeDefParticle, TypedXmlWriter
{
    @XmlElement
    SimpleContent simpleContent();
    
    @XmlElement
    ComplexContent complexContent();
    
    @XmlAttribute
    ComplexTypeModel mixed(final boolean p0);
}
