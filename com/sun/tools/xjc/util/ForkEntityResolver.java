// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.util;

import java.io.IOException;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;

public class ForkEntityResolver implements EntityResolver
{
    private final EntityResolver lhs;
    private final EntityResolver rhs;
    
    public ForkEntityResolver(final EntityResolver lhs, final EntityResolver rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }
    
    public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException, IOException {
        final InputSource is = this.lhs.resolveEntity(publicId, systemId);
        if (is != null) {
            return is;
        }
        return this.rhs.resolveEntity(publicId, systemId);
    }
}
