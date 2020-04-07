// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.unmarshaller;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import com.sun.xml.bind.WhiteSpaceProcessor;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import javax.xml.namespace.NamespaceContext;
import org.xml.sax.Locator;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshallerHandler;

public final class SAXConnector implements UnmarshallerHandler
{
    private LocatorEx loc;
    private final StringBuilder buffer;
    private final XmlVisitor next;
    private final UnmarshallingContext context;
    private final XmlVisitor.TextPredictor predictor;
    private final TagNameImpl tagName;
    
    public SAXConnector(final XmlVisitor next, final LocatorEx externalLocator) {
        this.buffer = new StringBuilder();
        this.tagName = new TagNameImpl();
        this.next = next;
        this.context = next.getContext();
        this.predictor = next.getPredictor();
        this.loc = externalLocator;
    }
    
    public Object getResult() throws JAXBException, IllegalStateException {
        return this.context.getResult();
    }
    
    public UnmarshallingContext getContext() {
        return this.context;
    }
    
    public void setDocumentLocator(final Locator locator) {
        if (this.loc != null) {
            return;
        }
        this.loc = new LocatorExWrapper(locator);
    }
    
    public void startDocument() throws SAXException {
        this.next.startDocument(this.loc, null);
    }
    
    public void endDocument() throws SAXException {
        this.next.endDocument();
    }
    
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        this.next.startPrefixMapping(prefix, uri);
    }
    
    public void endPrefixMapping(final String prefix) throws SAXException {
        this.next.endPrefixMapping(prefix);
    }
    
    public void startElement(String uri, String local, String qname, final Attributes atts) throws SAXException {
        if (uri == null || uri.length() == 0) {
            uri = "";
        }
        if (local == null || local.length() == 0) {
            local = qname;
        }
        if (qname == null || qname.length() == 0) {
            qname = local;
        }
        this.processText(true);
        this.tagName.uri = uri;
        this.tagName.local = local;
        this.tagName.qname = qname;
        this.tagName.atts = atts;
        this.next.startElement(this.tagName);
    }
    
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        this.processText(false);
        this.tagName.uri = uri;
        this.tagName.local = localName;
        this.tagName.qname = qName;
        this.next.endElement(this.tagName);
    }
    
    public final void characters(final char[] buf, final int start, final int len) {
        if (this.predictor.expectText()) {
            this.buffer.append(buf, start, len);
        }
    }
    
    public final void ignorableWhitespace(final char[] buf, final int start, final int len) {
        this.characters(buf, start, len);
    }
    
    public void processingInstruction(final String target, final String data) {
    }
    
    public void skippedEntity(final String name) {
    }
    
    private void processText(final boolean ignorable) throws SAXException {
        if (this.predictor.expectText() && (!ignorable || !WhiteSpaceProcessor.isWhiteSpace(this.buffer))) {
            this.next.text(this.buffer);
        }
        this.buffer.setLength(0);
    }
    
    private static final class TagNameImpl extends TagName
    {
        String qname;
        
        public String getQname() {
            return this.qname;
        }
    }
}
