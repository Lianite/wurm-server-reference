// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.util;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.XMLFilterImpl;

public abstract class SubtreeCutter extends XMLFilterImpl
{
    private int cutDepth;
    private static final ContentHandler stub;
    private ContentHandler next;
    
    public SubtreeCutter() {
        this.cutDepth = 0;
    }
    
    public void startDocument() throws SAXException {
        this.cutDepth = 0;
        super.startDocument();
    }
    
    public boolean isCutting() {
        return this.cutDepth > 0;
    }
    
    public void startCutting() {
        super.setContentHandler(SubtreeCutter.stub);
        this.cutDepth = 1;
    }
    
    public void setContentHandler(final ContentHandler handler) {
        this.next = handler;
        if (this.getContentHandler() != SubtreeCutter.stub) {
            super.setContentHandler(handler);
        }
    }
    
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts) throws SAXException {
        if (this.cutDepth > 0) {
            ++this.cutDepth;
        }
        super.startElement(uri, localName, qName, atts);
    }
    
    public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
        super.endElement(namespaceURI, localName, qName);
        if (this.cutDepth != 0) {
            --this.cutDepth;
            if (this.cutDepth == 1) {
                super.setContentHandler(this.next);
                this.cutDepth = 0;
            }
        }
    }
    
    static {
        stub = new DefaultHandler();
    }
}
