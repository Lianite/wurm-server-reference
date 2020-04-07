// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.internalizer;

import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.xml.sax.Locator;
import java.util.Set;
import com.sun.xml.bind.marshaller.SAX2DOMEx;

class DOMBuilder extends SAX2DOMEx
{
    private final LocatorTable locatorTable;
    private final Set outerMostBindings;
    private Locator locator;
    
    public DOMBuilder(final Document dom, final LocatorTable ltable, final Set outerMostBindings) {
        super(dom);
        this.locatorTable = ltable;
        this.outerMostBindings = outerMostBindings;
    }
    
    public void setDocumentLocator(final Locator locator) {
        super.setDocumentLocator(this.locator = locator);
    }
    
    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) {
        super.startElement(namespaceURI, localName, qName, atts);
        final Element e = this.getCurrentElement();
        this.locatorTable.storeStartLocation(e, this.locator);
        if ("http://java.sun.com/xml/ns/jaxb".equals(e.getNamespaceURI()) && "bindings".equals(e.getLocalName())) {
            final Node p = e.getParentNode();
            if (p instanceof Document || (p instanceof Element && !e.getNamespaceURI().equals(p.getNamespaceURI()))) {
                this.outerMostBindings.add(e);
            }
        }
    }
    
    public void endElement(final String namespaceURI, final String localName, final String qName) {
        this.locatorTable.storeEndLocation(this.getCurrentElement(), this.locator);
        super.endElement(namespaceURI, localName, qName);
    }
}
