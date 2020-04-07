// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.unmarshaller;

import org.xml.sax.SAXException;
import org.xml.sax.Locator;
import javax.xml.bind.helpers.ValidationEventLocatorImpl;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

abstract class StAXConnector
{
    protected final XmlVisitor visitor;
    protected final UnmarshallingContext context;
    protected final XmlVisitor.TextPredictor predictor;
    protected final TagName tagName;
    
    public abstract void bridge() throws XMLStreamException;
    
    protected StAXConnector(final XmlVisitor visitor) {
        this.tagName = new TagNameImpl();
        this.visitor = visitor;
        this.context = visitor.getContext();
        this.predictor = visitor.getPredictor();
    }
    
    protected abstract Location getCurrentLocation();
    
    protected abstract String getCurrentQName();
    
    protected final void handleStartDocument(final NamespaceContext nsc) throws SAXException {
        this.visitor.startDocument(new LocatorEx() {
            public ValidationEventLocator getLocation() {
                return new ValidationEventLocatorImpl(this);
            }
            
            public int getColumnNumber() {
                return StAXConnector.this.getCurrentLocation().getColumnNumber();
            }
            
            public int getLineNumber() {
                return StAXConnector.this.getCurrentLocation().getLineNumber();
            }
            
            public String getPublicId() {
                return StAXConnector.this.getCurrentLocation().getPublicId();
            }
            
            public String getSystemId() {
                return StAXConnector.this.getCurrentLocation().getSystemId();
            }
        }, nsc);
    }
    
    protected final void handleEndDocument() throws SAXException {
        this.visitor.endDocument();
    }
    
    protected static String fixNull(final String s) {
        if (s == null) {
            return "";
        }
        return s;
    }
    
    protected final String getQName(final String prefix, final String localName) {
        if (prefix == null || prefix.length() == 0) {
            return localName;
        }
        return prefix + ':' + localName;
    }
    
    private final class TagNameImpl extends TagName
    {
        public String getQname() {
            return StAXConnector.this.getCurrentQName();
        }
    }
}
