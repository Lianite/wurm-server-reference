// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.istack;

import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

public class FragmentContentHandler extends XMLFilterImpl
{
    public FragmentContentHandler() {
    }
    
    public FragmentContentHandler(final XMLReader parent) {
        super(parent);
    }
    
    public FragmentContentHandler(final ContentHandler handler) {
        this.setContentHandler(handler);
    }
    
    public void startDocument() throws SAXException {
    }
    
    public void endDocument() throws SAXException {
    }
}
