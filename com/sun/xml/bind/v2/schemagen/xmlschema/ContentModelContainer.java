// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.schemagen.xmlschema;

import com.sun.xml.txw2.annotation.XmlElement;
import com.sun.xml.txw2.TypedXmlWriter;

public interface ContentModelContainer extends TypedXmlWriter
{
    @XmlElement
    LocalElement element();
    
    @XmlElement
    Any any();
    
    @XmlElement
    ExplicitGroup all();
    
    @XmlElement
    ExplicitGroup sequence();
    
    @XmlElement
    ExplicitGroup choice();
}
