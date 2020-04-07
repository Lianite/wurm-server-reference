// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.schemagen.episode;

import com.sun.xml.txw2.annotation.XmlAttribute;
import com.sun.xml.txw2.TypedXmlWriter;

public interface SchemaBindings extends TypedXmlWriter
{
    @XmlAttribute
    void map(final boolean p0);
}
