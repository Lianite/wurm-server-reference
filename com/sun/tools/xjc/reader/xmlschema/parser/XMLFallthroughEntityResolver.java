// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.parser;

import java.io.IOException;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;

public class XMLFallthroughEntityResolver implements XMLEntityResolver
{
    private final XMLEntityResolver first;
    private final XMLEntityResolver second;
    
    public XMLFallthroughEntityResolver(final XMLEntityResolver _first, final XMLEntityResolver _second) {
        this.first = _first;
        this.second = _second;
    }
    
    public XMLInputSource resolveEntity(final XMLResourceIdentifier resourceIdentifier) throws XNIException, IOException {
        final XMLInputSource xis = this.first.resolveEntity(resourceIdentifier);
        if (xis != null) {
            return xis;
        }
        return this.second.resolveEntity(resourceIdentifier);
    }
}
