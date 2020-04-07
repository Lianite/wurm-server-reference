// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.runtime;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.ContentHandler;
import com.sun.xml.bind.marshaller.SAX2DOMEx;
import org.w3c.dom.Element;

public class W3CDOMUnmarshallingEventHandler extends UnmarshallingEventHandlerAdaptor
{
    private Element owner;
    
    public W3CDOMUnmarshallingEventHandler(final UnmarshallingContext _ctxt) throws ParserConfigurationException, SAXException {
        super(_ctxt, (ContentHandler)new SAX2DOMEx());
    }
    
    public void enterElement(final String uri, final String local, final String qname, final Attributes atts) throws SAXException {
        super.enterElement(uri, local, qname, atts);
        if (this.owner == null) {
            this.owner = ((SAX2DOMEx)this.handler).getCurrentElement();
        }
    }
    
    public Object owner() {
        return this.owner;
    }
    
    public Element getOwner() {
        return this.owner;
    }
}
