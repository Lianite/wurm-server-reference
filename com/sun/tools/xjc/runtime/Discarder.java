// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.runtime;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

class Discarder implements UnmarshallingEventHandler
{
    private final UnmarshallingContext context;
    private int depth;
    
    public Discarder(final UnmarshallingContext _ctxt) {
        this.depth = 0;
        this.context = _ctxt;
    }
    
    public void enterAttribute(final String uri, final String local, final String qname) throws SAXException {
    }
    
    public void enterElement(final String uri, final String local, final String qname, final Attributes atts) throws SAXException {
        ++this.depth;
    }
    
    public void leaveAttribute(final String uri, final String local, final String qname) throws SAXException {
    }
    
    public void leaveElement(final String uri, final String local, final String qname) throws SAXException {
        --this.depth;
        if (this.depth == 0) {
            this.context.popContentHandler();
        }
    }
    
    public Object owner() {
        return null;
    }
    
    public void text(final String s) throws SAXException {
    }
    
    public void leaveChild(final int nextState) throws SAXException {
    }
}
