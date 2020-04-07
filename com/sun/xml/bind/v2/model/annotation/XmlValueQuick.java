// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlValue;

final class XmlValueQuick extends Quick implements XmlValue
{
    private final XmlValue core;
    
    public XmlValueQuick(final Locatable upstream, final XmlValue core) {
        super(upstream);
        this.core = core;
    }
    
    protected Annotation getAnnotation() {
        return this.core;
    }
    
    protected Quick newInstance(final Locatable upstream, final Annotation core) {
        return new XmlValueQuick(upstream, (XmlValue)core);
    }
    
    public Class<XmlValue> annotationType() {
        return XmlValue.class;
    }
}
