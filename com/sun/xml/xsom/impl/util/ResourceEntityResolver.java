// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl.util;

import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;

public class ResourceEntityResolver implements EntityResolver
{
    private final Class base;
    
    public ResourceEntityResolver(final Class _base) {
        this.base = _base;
    }
    
    public InputSource resolveEntity(final String publicId, final String systemId) {
        return new InputSource(this.base.getResourceAsStream(systemId));
    }
}
