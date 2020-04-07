// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.schemagen.xmlschema;

import com.sun.xml.txw2.annotation.XmlAttribute;
import com.sun.xml.txw2.TypedXmlWriter;

public interface Wildcard extends Annotated, TypedXmlWriter
{
    @XmlAttribute
    Wildcard processContents(final String p0);
    
    @XmlAttribute
    Wildcard namespace(final String p0);
    
    @XmlAttribute
    Wildcard namespace(final String[] p0);
}
