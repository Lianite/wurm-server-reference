// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.schemagen.xmlschema;

import com.sun.xml.txw2.annotation.XmlAttribute;
import com.sun.xml.txw2.TypedXmlWriter;

public interface FixedOrDefault extends TypedXmlWriter
{
    @XmlAttribute("default")
    FixedOrDefault _default(final String p0);
    
    @XmlAttribute
    FixedOrDefault fixed(final String p0);
}
