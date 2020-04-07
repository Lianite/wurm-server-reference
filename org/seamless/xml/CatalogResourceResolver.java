// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.xml;

import java.io.Reader;
import java.io.InputStream;
import org.w3c.dom.ls.LSInput;
import java.net.URL;
import java.net.URI;
import java.util.Map;
import java.util.logging.Logger;
import org.w3c.dom.ls.LSResourceResolver;

public class CatalogResourceResolver implements LSResourceResolver
{
    private static Logger log;
    private final Map<URI, URL> catalog;
    
    public CatalogResourceResolver(final Map<URI, URL> catalog) {
        this.catalog = catalog;
    }
    
    public LSInput resolveResource(final String type, final String namespaceURI, final String publicId, final String systemId, final String baseURI) {
        CatalogResourceResolver.log.finest("Trying to resolve system identifier URI in catalog: " + systemId);
        final URL systemURL;
        if ((systemURL = this.catalog.get(URI.create(systemId))) != null) {
            CatalogResourceResolver.log.finest("Loading catalog resource: " + systemURL);
            try {
                final Input i = new Input(systemURL.openStream());
                i.setBaseURI(baseURI);
                i.setSystemId(systemId);
                i.setPublicId(publicId);
                return i;
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        CatalogResourceResolver.log.info("System identifier not found in catalog, continuing with default resolution (this most likely means remote HTTP request!): " + systemId);
        return null;
    }
    
    static {
        CatalogResourceResolver.log = Logger.getLogger(CatalogResourceResolver.class.getName());
    }
    
    private static final class Input implements LSInput
    {
        InputStream in;
        
        public Input(final InputStream in) {
            this.in = in;
        }
        
        public Reader getCharacterStream() {
            return null;
        }
        
        public void setCharacterStream(final Reader characterStream) {
        }
        
        public InputStream getByteStream() {
            return this.in;
        }
        
        public void setByteStream(final InputStream byteStream) {
        }
        
        public String getStringData() {
            return null;
        }
        
        public void setStringData(final String stringData) {
        }
        
        public String getSystemId() {
            return null;
        }
        
        public void setSystemId(final String systemId) {
        }
        
        public String getPublicId() {
            return null;
        }
        
        public void setPublicId(final String publicId) {
        }
        
        public String getBaseURI() {
            return null;
        }
        
        public void setBaseURI(final String baseURI) {
        }
        
        public String getEncoding() {
            return null;
        }
        
        public void setEncoding(final String encoding) {
        }
        
        public boolean getCertifiedText() {
            return false;
        }
        
        public void setCertifiedText(final boolean certifiedText) {
        }
    }
}
