// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.xml.resolver.readers;

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
    
    public InputSource resolveEntity(final String s, final String s2) throws SAXException {
        if (this.er != null) {
            try {
                return this.er.resolveEntity(s, s2);
            }
            catch (IOException ex) {
                System.out.println("resolveEntity threw IOException!");
                return null;
            }
        }
        return null;
    }
    
    public void characters(final char[] array, final int n, final int n2) throws SAXException {
        if (this.ch != null) {
            this.ch.characters(array, n, n2);
        }
    }
    
    public void endDocument() throws SAXException {
        if (this.ch != null) {
            this.ch.endDocument();
        }
    }
    
    public void endElement(final String s, final String s2, final String s3) throws SAXException {
        if (this.ch != null) {
            this.ch.endElement(s, s2, s3);
        }
    }
    
    public void endPrefixMapping(final String s) throws SAXException {
        if (this.ch != null) {
            this.ch.endPrefixMapping(s);
        }
    }
    
    public void ignorableWhitespace(final char[] array, final int n, final int n2) throws SAXException {
        if (this.ch != null) {
            this.ch.ignorableWhitespace(array, n, n2);
        }
    }
    
    public void processingInstruction(final String s, final String s2) throws SAXException {
        if (this.ch != null) {
            this.ch.processingInstruction(s, s2);
        }
    }
    
    public void setDocumentLocator(final Locator documentLocator) {
        if (this.ch != null) {
            this.ch.setDocumentLocator(documentLocator);
        }
    }
    
    public void skippedEntity(final String s) throws SAXException {
        if (this.ch != null) {
            this.ch.skippedEntity(s);
        }
    }
    
    public void startDocument() throws SAXException {
        if (this.ch != null) {
            this.ch.startDocument();
        }
    }
    
    public void startElement(final String s, final String s2, final String s3, final Attributes attributes) throws SAXException {
        if (this.ch != null) {
            this.ch.startElement(s, s2, s3, attributes);
        }
    }
    
    public void startPrefixMapping(final String s, final String s2) throws SAXException {
        if (this.ch != null) {
            this.ch.startPrefixMapping(s, s2);
        }
    }
}
