// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.runtime;

import org.xml.sax.SAXException;
import javax.xml.namespace.QName;
import com.sun.xml.bind.JAXBObject;
import javax.xml.bind.Element;

public class ElementWrapper implements Element, JAXBObject, XMLSerializable
{
    private QName tagName;
    private Object body;
    
    public ElementWrapper(final QName tagName, final Object body) {
        if (tagName == null) {
            throw new IllegalArgumentException("tag name is null");
        }
        this.setBody(body);
        this.tagName = tagName;
    }
    
    public QName getTagName() {
        return this.tagName;
    }
    
    public void setTagName(final QName tagName) {
        if (tagName == null) {
            throw new IllegalArgumentException("tag name is null");
        }
        this.tagName = tagName;
    }
    
    public Object getBody() {
        return this.body;
    }
    
    public void setBody(final Object body) {
        if (body == null) {
            throw new IllegalArgumentException("body is null");
        }
        if (!(body instanceof JAXBObject)) {
            throw new IllegalArgumentException(body.getClass().getName() + " is not a JAXB-generated class");
        }
        this.body = body;
    }
    
    public void serializeBody(final XMLSerializer target) throws SAXException {
        target.startElement(this.tagName.getNamespaceURI(), this.tagName.getLocalPart());
        target.childAsURIs((JAXBObject)this.body, "body");
        target.endNamespaceDecls();
        target.childAsAttributes((JAXBObject)this.body, "body");
        target.endAttributes();
        target.childAsBody((JAXBObject)this.body, "body");
        target.endElement();
    }
    
    public void serializeAttributes(final XMLSerializer target) throws SAXException {
    }
    
    public void serializeURIs(final XMLSerializer target) throws SAXException {
    }
}
