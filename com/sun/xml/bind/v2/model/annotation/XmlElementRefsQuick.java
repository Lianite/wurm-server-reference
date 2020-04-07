// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.annotation;

import javax.xml.bind.annotation.XmlElementRef;
import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlElementRefs;

final class XmlElementRefsQuick extends Quick implements XmlElementRefs
{
    private final XmlElementRefs core;
    
    public XmlElementRefsQuick(final Locatable upstream, final XmlElementRefs core) {
        super(upstream);
        this.core = core;
    }
    
    protected Annotation getAnnotation() {
        return this.core;
    }
    
    protected Quick newInstance(final Locatable upstream, final Annotation core) {
        return new XmlElementRefsQuick(upstream, (XmlElementRefs)core);
    }
    
    public Class<XmlElementRefs> annotationType() {
        return XmlElementRefs.class;
    }
    
    public XmlElementRef[] value() {
        return this.core.value();
    }
}
