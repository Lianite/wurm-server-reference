// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.xml;

import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.InputSource;
import org.xml.sax.ErrorHandler;
import org.w3c.dom.ls.LSResourceResolver;
import java.util.Map;
import java.util.HashMap;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;
import javax.xml.transform.Source;
import org.xml.sax.helpers.XMLReaderFactory;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.XMLReader;
import java.net.URL;
import java.net.URI;
import java.util.logging.Logger;

public class SAXParser
{
    private static final Logger log;
    public static final URI XML_SCHEMA_NAMESPACE;
    public static final URL XML_SCHEMA_RESOURCE;
    private final XMLReader xr;
    
    public SAXParser() {
        this(null);
    }
    
    public SAXParser(final DefaultHandler handler) {
        this.xr = this.create();
        if (handler != null) {
            this.xr.setContentHandler(handler);
        }
    }
    
    public void setContentHandler(final ContentHandler handler) {
        this.xr.setContentHandler(handler);
    }
    
    protected XMLReader create() {
        try {
            if (this.getSchemaSources() != null) {
                final SAXParserFactory factory = SAXParserFactory.newInstance();
                factory.setNamespaceAware(true);
                factory.setSchema(this.createSchema(this.getSchemaSources()));
                final XMLReader xmlReader = factory.newSAXParser().getXMLReader();
                xmlReader.setErrorHandler(this.getErrorHandler());
                return xmlReader;
            }
            return XMLReaderFactory.createXMLReader();
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    protected Schema createSchema(final Source[] schemaSources) {
        try {
            final SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            schemaFactory.setResourceResolver(new CatalogResourceResolver(new HashMap<URI, URL>() {
                {
                    this.put(SAXParser.XML_SCHEMA_NAMESPACE, SAXParser.XML_SCHEMA_RESOURCE);
                }
            }));
            return schemaFactory.newSchema(schemaSources);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    protected Source[] getSchemaSources() {
        return null;
    }
    
    protected ErrorHandler getErrorHandler() {
        return new SimpleErrorHandler();
    }
    
    public void parse(final InputSource source) throws ParserException {
        try {
            this.xr.parse(source);
        }
        catch (Exception ex) {
            throw new ParserException(ex);
        }
    }
    
    static {
        log = Logger.getLogger(SAXParser.class.getName());
        XML_SCHEMA_NAMESPACE = URI.create("http://www.w3.org/2001/xml.xsd");
        XML_SCHEMA_RESOURCE = Thread.currentThread().getContextClassLoader().getResource("org/seamless/schemas/xml.xsd");
    }
    
    public class SimpleErrorHandler implements ErrorHandler
    {
        public void warning(final SAXParseException e) throws SAXException {
            throw new SAXException(e);
        }
        
        public void error(final SAXParseException e) throws SAXException {
            throw new SAXException(e);
        }
        
        public void fatalError(final SAXParseException e) throws SAXException {
            throw new SAXException(e);
        }
    }
    
    public static class Handler<I> extends DefaultHandler
    {
        protected SAXParser parser;
        protected I instance;
        protected Handler parent;
        protected StringBuilder characters;
        protected Attributes attributes;
        
        public Handler(final I instance) {
            this(instance, null, null);
        }
        
        public Handler(final I instance, final SAXParser parser) {
            this(instance, parser, null);
        }
        
        public Handler(final I instance, final Handler parent) {
            this(instance, parent.getParser(), parent);
        }
        
        public Handler(final I instance, final SAXParser parser, final Handler parent) {
            this.characters = new StringBuilder();
            this.instance = instance;
            this.parser = parser;
            this.parent = parent;
            if (parser != null) {
                parser.setContentHandler(this);
            }
        }
        
        public I getInstance() {
            return this.instance;
        }
        
        public SAXParser getParser() {
            return this.parser;
        }
        
        public Handler getParent() {
            return this.parent;
        }
        
        protected void switchToParent() {
            if (this.parser != null && this.parent != null) {
                this.parser.setContentHandler(this.parent);
                this.attributes = null;
            }
        }
        
        public String getCharacters() {
            return this.characters.toString();
        }
        
        public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
            this.characters = new StringBuilder();
            this.attributes = new AttributesImpl(attributes);
            SAXParser.log.finer(this.getClass().getSimpleName() + " starting: " + localName);
        }
        
        public void characters(final char[] ch, final int start, final int length) throws SAXException {
            this.characters.append(ch, start, length);
        }
        
        public void endElement(final String uri, final String localName, final String qName) throws SAXException {
            if (this.isLastElement(uri, localName, qName)) {
                SAXParser.log.finer(this.getClass().getSimpleName() + ": last element, switching to parent: " + localName);
                this.switchToParent();
                return;
            }
            SAXParser.log.finer(this.getClass().getSimpleName() + " ending: " + localName);
        }
        
        protected boolean isLastElement(final String uri, final String localName, final String qName) {
            return false;
        }
        
        protected Attributes getAttributes() {
            return this.attributes;
        }
    }
}
