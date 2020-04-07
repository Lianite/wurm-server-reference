// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.bindinfo;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;
import java.util.ArrayList;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.XMLFilterImpl;

public class ForkingFilter extends XMLFilterImpl
{
    private ContentHandler side;
    private int depth;
    private final ArrayList<String> namespaces;
    private Locator loc;
    
    public ForkingFilter() {
        this.namespaces = new ArrayList<String>();
    }
    
    public ForkingFilter(final ContentHandler next) {
        this.namespaces = new ArrayList<String>();
        this.setContentHandler(next);
    }
    
    public ContentHandler getSideHandler() {
        return this.side;
    }
    
    public void setDocumentLocator(final Locator locator) {
        super.setDocumentLocator(locator);
        this.loc = locator;
    }
    
    public Locator getDocumentLocator() {
        return this.loc;
    }
    
    public void startDocument() throws SAXException {
        this.reset();
        super.startDocument();
    }
    
    private void reset() {
        this.namespaces.clear();
        this.side = null;
        this.depth = 0;
    }
    
    public void endDocument() throws SAXException {
        this.loc = null;
        this.reset();
        super.endDocument();
    }
    
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        if (this.side != null) {
            this.side.startPrefixMapping(prefix, uri);
        }
        this.namespaces.add(prefix);
        this.namespaces.add(uri);
        super.startPrefixMapping(prefix, uri);
    }
    
    public void endPrefixMapping(final String prefix) throws SAXException {
        if (this.side != null) {
            this.side.endPrefixMapping(prefix);
        }
        super.endPrefixMapping(prefix);
        this.namespaces.remove(this.namespaces.size() - 1);
        this.namespaces.remove(this.namespaces.size() - 1);
    }
    
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts) throws SAXException {
        if (this.side != null) {
            this.side.startElement(uri, localName, qName, atts);
            ++this.depth;
        }
        super.startElement(uri, localName, qName, atts);
    }
    
    public void startForking(final String uri, final String localName, final String qName, final Attributes atts, final ContentHandler side) throws SAXException {
        if (this.side != null) {
            throw new IllegalStateException();
        }
        this.side = side;
        this.depth = 1;
        side.setDocumentLocator(this.loc);
        side.startDocument();
        for (int i = 0; i < this.namespaces.size(); i += 2) {
            side.startPrefixMapping(this.namespaces.get(i), this.namespaces.get(i + 1));
        }
        side.startElement(uri, localName, qName, atts);
    }
    
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if (this.side != null) {
            this.side.endElement(uri, localName, qName);
            --this.depth;
            if (this.depth == 0) {
                for (int i = this.namespaces.size() - 2; i >= 0; i -= 2) {
                    this.side.endPrefixMapping(this.namespaces.get(i));
                }
                this.side.endDocument();
                this.side = null;
            }
        }
        super.endElement(uri, localName, qName);
    }
    
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        if (this.side != null) {
            this.side.characters(ch, start, length);
        }
        super.characters(ch, start, length);
    }
    
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        if (this.side != null) {
            this.side.ignorableWhitespace(ch, start, length);
        }
        super.ignorableWhitespace(ch, start, length);
    }
}
