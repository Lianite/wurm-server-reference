// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.schemagen.xmlschema;

import com.sun.xml.txw2.annotation.XmlAttribute;
import javax.xml.namespace.QName;
import com.sun.xml.txw2.TypedXmlWriter;

public interface Element extends Annotated, ComplexTypeHost, FixedOrDefault, SimpleTypeHost, TypedXmlWriter
{
    @XmlAttribute
    Element type(final QName p0);
    
    @XmlAttribute
    Element block(final String p0);
    
    @XmlAttribute
    Element block(final String[] p0);
    
    @XmlAttribute
    Element nillable(final boolean p0);
}
