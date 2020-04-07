// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.schemagen.xmlschema;

import com.sun.xml.txw2.annotation.XmlAttribute;
import com.sun.xml.txw2.annotation.XmlElement;
import com.sun.xml.txw2.TypedXmlWriter;

@XmlElement("schema")
public interface Schema extends SchemaTop, TypedXmlWriter
{
    @XmlElement
    Annotation annotation();
    
    @XmlElement("import")
    Import _import();
    
    @XmlAttribute
    Schema targetNamespace(final String p0);
    
    @XmlAttribute(ns = "http://www.w3.org/XML/1998/namespace")
    Schema lang(final String p0);
    
    @XmlAttribute
    Schema id(final String p0);
    
    @XmlAttribute
    Schema elementFormDefault(final String p0);
    
    @XmlAttribute
    Schema attributeFormDefault(final String p0);
    
    @XmlAttribute
    Schema blockDefault(final String p0);
    
    @XmlAttribute
    Schema blockDefault(final String[] p0);
    
    @XmlAttribute
    Schema finalDefault(final String p0);
    
    @XmlAttribute
    Schema finalDefault(final String[] p0);
    
    @XmlAttribute
    Schema version(final String p0);
}
