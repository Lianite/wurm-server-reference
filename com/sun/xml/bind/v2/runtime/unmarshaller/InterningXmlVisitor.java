// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.unmarshaller;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import javax.xml.namespace.NamespaceContext;

public final class InterningXmlVisitor implements XmlVisitor
{
    private final XmlVisitor next;
    private final AttributesImpl attributes;
    
    public InterningXmlVisitor(final XmlVisitor next) {
        this.attributes = new AttributesImpl();
        this.next = next;
    }
    
    public void startDocument(final LocatorEx locator, final NamespaceContext nsContext) throws SAXException {
        this.next.startDocument(locator, nsContext);
    }
    
    public void endDocument() throws SAXException {
        this.next.endDocument();
    }
    
    public void startElement(final TagName tagName) throws SAXException {
        this.attributes.setAttributes(tagName.atts);
        tagName.atts = this.attributes;
        tagName.uri = intern(tagName.uri);
        tagName.local = intern(tagName.local);
        this.next.startElement(tagName);
    }
    
    public void endElement(final TagName tagName) throws SAXException {
        tagName.uri = intern(tagName.uri);
        tagName.local = intern(tagName.local);
        this.next.endElement(tagName);
    }
    
    public void startPrefixMapping(final String prefix, final String nsUri) throws SAXException {
        this.next.startPrefixMapping(intern(prefix), intern(nsUri));
    }
    
    public void endPrefixMapping(final String prefix) throws SAXException {
        this.next.endPrefixMapping(intern(prefix));
    }
    
    public void text(final CharSequence pcdata) throws SAXException {
        this.next.text(pcdata);
    }
    
    public UnmarshallingContext getContext() {
        return this.next.getContext();
    }
    
    public TextPredictor getPredictor() {
        return this.next.getPredictor();
    }
    
    private static String intern(final String s) {
        if (s == null) {
            return null;
        }
        return s.intern();
    }
    
    private static class AttributesImpl implements Attributes
    {
        private Attributes core;
        
        void setAttributes(final Attributes att) {
            this.core = att;
        }
        
        public int getIndex(final String qName) {
            return this.core.getIndex(qName);
        }
        
        public int getIndex(final String uri, final String localName) {
            return this.core.getIndex(uri, localName);
        }
        
        public int getLength() {
            return this.core.getLength();
        }
        
        public String getLocalName(final int index) {
            return intern(this.core.getLocalName(index));
        }
        
        public String getQName(final int index) {
            return intern(this.core.getQName(index));
        }
        
        public String getType(final int index) {
            return intern(this.core.getType(index));
        }
        
        public String getType(final String qName) {
            return intern(this.core.getType(qName));
        }
        
        public String getType(final String uri, final String localName) {
            return intern(this.core.getType(uri, localName));
        }
        
        public String getURI(final int index) {
            return intern(this.core.getURI(index));
        }
        
        public String getValue(final int index) {
            return this.core.getValue(index);
        }
        
        public String getValue(final String qName) {
            return this.core.getValue(qName);
        }
        
        public String getValue(final String uri, final String localName) {
            return this.core.getValue(uri, localName);
        }
    }
}
