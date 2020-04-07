// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.runtime;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import org.dom4j.io.SAXContentHandler;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;

public class Dom4jUnmarshallingEventHandler extends UnmarshallingEventHandlerAdaptor
{
    private Element owner;
    
    public Dom4jUnmarshallingEventHandler(final UnmarshallingContext _ctxt) throws SAXException {
        super(_ctxt, (ContentHandler)new SAXContentHandler(new DocumentFactory()));
    }
    
    public void enterElement(final String uri, final String local, final String qname, final Attributes atts) throws SAXException {
        super.enterElement(uri, local, qname, atts);
        if (this.owner == null) {
            this.owner = ((SAXContentHandler)this.handler).getDocument().getRootElement();
        }
    }
    
    public Object owner() {
        return this.owner;
    }
    
    public Element getOwner() {
        return this.owner;
    }
}
