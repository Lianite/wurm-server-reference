// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlTransient;

final class XmlTransientQuick extends Quick implements XmlTransient
{
    private final XmlTransient core;
    
    public XmlTransientQuick(final Locatable upstream, final XmlTransient core) {
        super(upstream);
        this.core = core;
    }
    
    protected Annotation getAnnotation() {
        return this.core;
    }
    
    protected Quick newInstance(final Locatable upstream, final Annotation core) {
        return new XmlTransientQuick(upstream, (XmlTransient)core);
    }
    
    public Class<XmlTransient> annotationType() {
        return XmlTransient.class;
    }
}
