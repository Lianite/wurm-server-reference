// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.schemagen.episode;

import com.sun.xml.txw2.annotation.XmlAttribute;
import com.sun.xml.txw2.annotation.XmlElement;
import com.sun.xml.txw2.TypedXmlWriter;

@XmlElement("bindings")
public interface Bindings extends TypedXmlWriter
{
    @XmlElement
    Bindings bindings();
    
    @XmlElement("class")
    Klass klass();
    
    @XmlElement
    SchemaBindings schemaBindings();
    
    @XmlAttribute
    void scd(final String p0);
    
    @XmlAttribute
    void version(final String p0);
}
