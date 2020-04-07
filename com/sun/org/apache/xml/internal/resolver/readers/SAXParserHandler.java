// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.org.apache.xml.internal.resolver.readers;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.xml.sax.InputSource;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.helpers.DefaultHandler;

public class SAXParserHandler extends DefaultHandler
{
    private EntityResolver er;
    private ContentHandler ch;
    
    public SAXParserHandler() {
        this.er = null;
        this.ch = null;
    }
    
    public void setEntityResolver(final EntityResolver er) {
        this.er = er;
    }
    
    public void setContentHandler(final ContentHandler ch) {
        this.ch = ch;
    }
    
    public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException {
        if (this.er != null) {
            try {
                return this.er.resolveEntity(publicId, systemId);
            }
            catch (IOException e) {
                System.out.println("resolveEntity threw IOException!");
                return null;
            }
        }
        return null;
    }
    
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        if (this.ch != null) {
            this.ch.characters(ch, start, length);
        }
    }
    
    public void endDocument() throws SAXException {
        if (this.ch != null) {
            this.ch.endDocument();
        }
    }
    
    public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
        if (this.ch != null) {
            this.ch.endElement(namespaceURI, localName, qName);
        }
    }
    
    public void endPrefixMapping(final String prefix) throws SAXException {
        if (this.ch != null) {
            this.ch.endPrefixMapping(prefix);
        }
    }
    
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        if (this.ch != null) {
            this.ch.ignorableWhitespace(ch, start, length);
        }
    }
    
    public void processingInstruction(final String target, final String data) throws SAXException {
        if (this.ch != null) {
            this.ch.processingInstruction(target, data);
        }
    }
    
    public void setDocumentLocator(final Locator locator) {
        if (this.ch != null) {
            this.ch.setDocumentLocator(locator);
        }
    }
    
    public void skippedEntity(final String name) throws SAXException {
        if (this.ch != null) {
            this.ch.skippedEntity(name);
        }
    }
    
    public void startDocument() throws SAXException {
        if (this.ch != null) {
            this.ch.startDocument();
        }
    }
    
    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException {
        if (this.ch != null) {
            this.ch.startElement(namespaceURI, localName, qName, atts);
        }
    }
    
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        if (this.ch != null) {
            this.ch.startPrefixMapping(prefix, uri);
        }
    }
}
