// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlSchemaType;

final class XmlSchemaTypeQuick extends Quick implements XmlSchemaType
{
    private final XmlSchemaType core;
    
    public XmlSchemaTypeQuick(final Locatable upstream, final XmlSchemaType core) {
        super(upstream);
        this.core = core;
    }
    
    protected Annotation getAnnotation() {
        return this.core;
    }
    
    protected Quick newInstance(final Locatable upstream, final Annotation core) {
        return new XmlSchemaTypeQuick(upstream, (XmlSchemaType)core);
    }
    
    public Class<XmlSchemaType> annotationType() {
        return XmlSchemaType.class;
    }
    
    public String name() {
        return this.core.name();
    }
    
    public Class type() {
        return this.core.type();
    }
    
    public String namespace() {
        return this.core.namespace();
    }
}
