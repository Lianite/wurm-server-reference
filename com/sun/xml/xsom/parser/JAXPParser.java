// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.parser;

import com.sun.xml.xsom.impl.parser.Messages;
import java.net.URL;
import org.xml.sax.helpers.XMLFilterImpl;
import java.io.IOException;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import javax.xml.parsers.SAXParserFactory;

public class JAXPParser implements XMLParser
{
    private final SAXParserFactory factory;
    
    public JAXPParser(final SAXParserFactory factory) {
        factory.setNamespaceAware(true);
        this.factory = factory;
    }
    
    public JAXPParser() {
        this(SAXParserFactory.newInstance());
    }
    
    public void parse(final InputSource source, final ContentHandler handler, final ErrorHandler errorHandler, final EntityResolver entityResolver) throws SAXException, IOException {
        try {
            XMLReader reader = this.factory.newSAXParser().getXMLReader();
            reader = new XMLReaderEx(reader);
            reader.setContentHandler(handler);
            if (errorHandler != null) {
                reader.setErrorHandler(errorHandler);
            }
            if (entityResolver != null) {
                reader.setEntityResolver(entityResolver);
            }
            reader.parse(source);
        }
        catch (ParserConfigurationException e) {
            final SAXParseException spe = new SAXParseException(e.getMessage(), null, e);
            errorHandler.fatalError(spe);
            throw spe;
        }
    }
    
    private static class XMLReaderEx extends XMLFilterImpl
    {
        private Locator locator;
        
        XMLReaderEx(final XMLReader parent) {
            this.setParent(parent);
        }
        
        public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException {
            try {
                InputSource is = null;
                if (this.getEntityResolver() != null) {
                    is = this.getEntityResolver().resolveEntity(publicId, systemId);
                }
                if (is != null) {
                    return is;
                }
                is = new InputSource(new URL(systemId).openStream());
                is.setSystemId(systemId);
                is.setPublicId(publicId);
                return is;
            }
            catch (IOException e) {
                final SAXParseException spe = new SAXParseException(Messages.format("EntityResolutionFailure", systemId, e.toString()), this.locator, e);
                if (this.getErrorHandler() != null) {
                    this.getErrorHandler().fatalError(spe);
                }
                throw spe;
            }
        }
        
        public void setDocumentLocator(final Locator locator) {
            super.setDocumentLocator(locator);
            this.locator = locator;
        }
    }
}
