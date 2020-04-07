// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.runtime;

import org.xml.sax.Locator;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import java.util.ArrayList;
import org.xml.sax.ContentHandler;

public class ContentHandlerAdaptor implements ContentHandler
{
    private final ArrayList prefixMap;
    private final XMLSerializer serializer;
    private final StringBuffer text;
    
    public ContentHandlerAdaptor(final XMLSerializer _serializer) {
        this.prefixMap = new ArrayList();
        this.text = new StringBuffer();
        this.serializer = _serializer;
    }
    
    public void startDocument() throws SAXException {
        this.prefixMap.clear();
    }
    
    public void endDocument() throws SAXException {
    }
    
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        this.prefixMap.add(prefix);
        this.prefixMap.add(uri);
    }
    
    public void endPrefixMapping(final String prefix) throws SAXException {
    }
    
    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException {
        this.flushText();
        final int len = atts.getLength();
        this.serializer.startElement(namespaceURI, localName);
        for (int i = 0; i < len; ++i) {
            final String qname = atts.getQName(i);
            final int idx = qname.indexOf(58);
            final String prefix = (idx == -1) ? qname : qname.substring(0, idx);
            this.serializer.getNamespaceContext().declareNamespace(atts.getURI(i), prefix, true);
        }
        for (int i = 0; i < this.prefixMap.size(); i += 2) {
            final String prefix2 = this.prefixMap.get(i);
            this.serializer.getNamespaceContext().declareNamespace((String)this.prefixMap.get(i + 1), prefix2, prefix2.length() != 0);
        }
        this.serializer.endNamespaceDecls();
        for (int i = 0; i < len; ++i) {
            this.serializer.startAttribute(atts.getURI(i), atts.getLocalName(i));
            this.serializer.text(atts.getValue(i), (String)null);
            this.serializer.endAttribute();
        }
        this.prefixMap.clear();
        this.serializer.endAttributes();
    }
    
    public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
        this.flushText();
        this.serializer.endElement();
    }
    
    private void flushText() throws SAXException {
        if (this.text.length() != 0) {
            this.serializer.text(this.text.toString(), (String)null);
            this.text.setLength(0);
        }
    }
    
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        this.text.append(ch, start, length);
    }
    
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        this.text.append(ch, start, length);
    }
    
    public void setDocumentLocator(final Locator locator) {
    }
    
    public void processingInstruction(final String target, final String data) throws SAXException {
    }
    
    public void skippedEntity(final String name) throws SAXException {
    }
}
