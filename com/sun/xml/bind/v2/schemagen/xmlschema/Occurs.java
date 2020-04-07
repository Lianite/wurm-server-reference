// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.schemagen.xmlschema;

import com.sun.xml.txw2.annotation.XmlAttribute;
import com.sun.xml.txw2.TypedXmlWriter;

public interface Occurs extends TypedXmlWriter
{
    @XmlAttribute
    Occurs minOccurs(final int p0);
    
    @XmlAttribute
    Occurs maxOccurs(final String p0);
    
    @XmlAttribute
    Occurs maxOccurs(final int p0);
}
