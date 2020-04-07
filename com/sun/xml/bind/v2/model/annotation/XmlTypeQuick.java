// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlType;

final class XmlTypeQuick extends Quick implements XmlType
{
    private final XmlType core;
    
    public XmlTypeQuick(final Locatable upstream, final XmlType core) {
        super(upstream);
        this.core = core;
    }
    
    protected Annotation getAnnotation() {
        return this.core;
    }
    
    protected Quick newInstance(final Locatable upstream, final Annotation core) {
        return new XmlTypeQuick(upstream, (XmlType)core);
    }
    
    public Class<XmlType> annotationType() {
        return XmlType.class;
    }
    
    public String name() {
        return this.core.name();
    }
    
    public String namespace() {
        return this.core.namespace();
    }
    
    public String[] propOrder() {
        return this.core.propOrder();
    }
    
    public Class factoryClass() {
        return this.core.factoryClass();
    }
    
    public String factoryMethod() {
        return this.core.factoryMethod();
    }
}
