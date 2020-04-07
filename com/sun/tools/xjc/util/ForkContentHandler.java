// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.util;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;
import org.xml.sax.ContentHandler;

public class ForkContentHandler implements ContentHandler
{
    private final ContentHandler lhs;
    private final ContentHandler rhs;
    
    public ForkContentHandler(final ContentHandler first, final ContentHandler second) {
        this.lhs = first;
        this.rhs = second;
    }
    
    public static ContentHandler create(final ContentHandler[] handlers) {
        if (handlers.length == 0) {
            throw new IllegalArgumentException();
        }
        ContentHandler result = handlers[0];
        for (int i = 1; i < handlers.length; ++i) {
            result = new ForkContentHandler(result, handlers[i]);
        }
        return result;
    }
    
    public void setDocumentLocator(final Locator locator) {
        this.lhs.setDocumentLocator(locator);
        this.rhs.setDocumentLocator(locator);
    }
    
    public void startDocument() throws SAXException {
        this.lhs.startDocument();
        this.rhs.startDocument();
    }
    
    public void endDocument() throws SAXException {
        this.lhs.endDocument();
        this.rhs.endDocument();
    }
    
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        this.lhs.startPrefixMapping(prefix, uri);
        this.rhs.startPrefixMapping(prefix, uri);
    }
    
    public void endPrefixMapping(final String prefix) throws SAXException {
        this.lhs.endPrefixMapping(prefix);
        this.rhs.endPrefixMapping(prefix);
    }
    
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        this.lhs.startElement(uri, localName, qName, attributes);
        this.rhs.startElement(uri, localName, qName, attributes);
    }
    
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        this.lhs.endElement(uri, localName, qName);
        this.rhs.endElement(uri, localName, qName);
    }
    
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        this.lhs.characters(ch, start, length);
        this.rhs.characters(ch, start, length);
    }
    
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        this.lhs.ignorableWhitespace(ch, start, length);
        this.rhs.ignorableWhitespace(ch, start, length);
    }
    
    public void processingInstruction(final String target, final String data) throws SAXException {
        this.lhs.processingInstruction(target, data);
        this.rhs.processingInstruction(target, data);
    }
    
    public void skippedEntity(final String name) throws SAXException {
        this.lhs.skippedEntity(name);
        this.rhs.skippedEntity(name);
    }
}
