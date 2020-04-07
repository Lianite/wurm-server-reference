// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.runtime;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.helpers.ValidationEventImpl;
import javax.xml.bind.helpers.ValidationEventLocatorImpl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;

public class UnmarshallingEventHandlerAdaptor implements UnmarshallingEventHandler
{
    protected final UnmarshallingContext context;
    protected final ContentHandler handler;
    private int depth;
    
    public UnmarshallingEventHandlerAdaptor(final UnmarshallingContext _ctxt, final ContentHandler _handler) throws SAXException {
        this.depth = 0;
        this.context = _ctxt;
        this.handler = _handler;
        try {
            this.handler.setDocumentLocator(this.context.getLocator());
            this.handler.startDocument();
            this.declarePrefixes(this.context.getAllDeclaredPrefixes());
        }
        catch (SAXException e) {
            this.error(e);
        }
    }
    
    public Object owner() {
        return null;
    }
    
    public void enterAttribute(final String uri, final String local, final String qname) throws SAXException {
    }
    
    public void enterElement(final String uri, final String local, final String qname, final Attributes atts) throws SAXException {
        ++this.depth;
        this.context.pushAttributes(atts, true);
        try {
            this.declarePrefixes(this.context.getNewlyDeclaredPrefixes());
            this.handler.startElement(uri, local, qname, atts);
        }
        catch (SAXException e) {
            this.error(e);
        }
    }
    
    public void leaveAttribute(final String uri, final String local, final String qname) throws SAXException {
    }
    
    public void leaveElement(final String uri, final String local, final String qname) throws SAXException {
        try {
            this.handler.endElement(uri, local, qname);
            this.undeclarePrefixes(this.context.getNewlyDeclaredPrefixes());
        }
        catch (SAXException e) {
            this.error(e);
        }
        this.context.popAttributes();
        --this.depth;
        if (this.depth == 0) {
            try {
                this.undeclarePrefixes(this.context.getAllDeclaredPrefixes());
                this.handler.endDocument();
            }
            catch (SAXException e) {
                this.error(e);
            }
            this.context.popContentHandler();
        }
    }
    
    private void declarePrefixes(final String[] prefixes) throws SAXException {
        for (int i = prefixes.length - 1; i >= 0; --i) {
            this.handler.startPrefixMapping(prefixes[i], this.context.getNamespaceURI(prefixes[i]));
        }
    }
    
    private void undeclarePrefixes(final String[] prefixes) throws SAXException {
        for (int i = prefixes.length - 1; i >= 0; --i) {
            this.handler.endPrefixMapping(prefixes[i]);
        }
    }
    
    public void text(final String s) throws SAXException {
        try {
            this.handler.characters(s.toCharArray(), 0, s.length());
        }
        catch (SAXException e) {
            this.error(e);
        }
    }
    
    private void error(final SAXException e) throws SAXException {
        this.context.handleEvent((ValidationEvent)new ValidationEventImpl(1, e.getMessage(), new ValidationEventLocatorImpl(this.context.getLocator()), e), false);
    }
    
    public void leaveChild(final int nextState) throws SAXException {
    }
}
