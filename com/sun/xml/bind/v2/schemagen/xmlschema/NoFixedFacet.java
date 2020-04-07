// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.schemagen.xmlschema;

import com.sun.xml.txw2.annotation.XmlAttribute;
import com.sun.xml.txw2.TypedXmlWriter;

public interface NoFixedFacet extends Annotated, TypedXmlWriter
{
    @XmlAttribute
    NoFixedFacet value(final String p0);
}
