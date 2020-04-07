// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.parser;

import java.io.IOException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import org.xml.sax.EntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;

class XMLEntityResolverImpl implements XMLEntityResolver
{
    private final EntityResolver entityResolver;
    
    XMLEntityResolverImpl(final EntityResolver er) {
        if (er == null) {
            throw new NullPointerException();
        }
        this.entityResolver = er;
    }
    
    public XMLInputSource resolveEntity(final XMLResourceIdentifier r) throws XNIException, IOException {
        String publicId = r.getPublicId();
        final String systemId = r.getExpandedSystemId();
        if (publicId == null) {
            publicId = r.getNamespace();
        }
        try {
            final InputSource is = this.entityResolver.resolveEntity(publicId, systemId);
            if (is == null) {
                return null;
            }
            final XMLInputSource xis = new XMLInputSource(is.getPublicId(), is.getSystemId(), r.getBaseSystemId(), is.getByteStream(), is.getEncoding());
            xis.setCharacterStream(is.getCharacterStream());
            return xis;
        }
        catch (SAXException e) {
            throw new XNIException(e);
        }
    }
}
