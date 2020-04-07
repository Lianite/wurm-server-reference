// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.marshaller;

import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.w3c.dom.Text;
import com.sun.xml.bind.util.Which;
import org.xml.sax.Attributes;
import org.w3c.dom.Element;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import com.sun.istack.FinalArrayList;
import java.util.Stack;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

public class SAX2DOMEx implements ContentHandler
{
    private Node node;
    private final Stack<Node> nodeStack;
    private final FinalArrayList<String> unprocessedNamespaces;
    private final Document document;
    
    public SAX2DOMEx(final Node node) {
        this.node = null;
        this.nodeStack = new Stack<Node>();
        this.unprocessedNamespaces = new FinalArrayList<String>();
        this.node = node;
        this.nodeStack.push(this.node);
        if (node instanceof Document) {
            this.document = (Document)node;
        }
        else {
            this.document = node.getOwnerDocument();
        }
    }
    
    public SAX2DOMEx() throws ParserConfigurationException {
        this.node = null;
        this.nodeStack = new Stack<Node>();
        this.unprocessedNamespaces = new FinalArrayList<String>();
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        this.document = factory.newDocumentBuilder().newDocument();
        this.node = this.document;
        this.nodeStack.push(this.document);
    }
    
    public final Element getCurrentElement() {
        return this.nodeStack.peek();
    }
    
    public Node getDOM() {
        return this.node;
    }
    
    public void startDocument() {
    }
    
    public void endDocument() {
    }
    
    public void startElement(final String namespace, final String localName, final String qName, final Attributes attrs) {
        final Node parent = this.nodeStack.peek();
        final Element element = this.document.createElementNS(namespace, qName);
        if (element == null) {
            throw new AssertionError((Object)Messages.format("SAX2DOMEx.DomImplDoesntSupportCreateElementNs", this.document.getClass().getName(), Which.which(this.document.getClass())));
        }
        for (int i = 0; i < this.unprocessedNamespaces.size(); i += 2) {
            final String prefix = this.unprocessedNamespaces.get(i + 0);
            final String uri = this.unprocessedNamespaces.get(i + 1);
            String qname;
            if ("".equals(prefix) || prefix == null) {
                qname = "xmlns";
            }
            else {
                qname = "xmlns:" + prefix;
            }
            if (element.hasAttributeNS("http://www.w3.org/2000/xmlns/", qname)) {
                element.removeAttributeNS("http://www.w3.org/2000/xmlns/", qname);
            }
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", qname, uri);
        }
        this.unprocessedNamespaces.clear();
        for (int length = attrs.getLength(), j = 0; j < length; ++j) {
            final String namespaceuri = attrs.getURI(j);
            final String value = attrs.getValue(j);
            final String qname2 = attrs.getQName(j);
            element.setAttributeNS(namespaceuri, qname2, value);
        }
        parent.appendChild(element);
        this.nodeStack.push(element);
    }
    
    public void endElement(final String namespace, final String localName, final String qName) {
        this.nodeStack.pop();
    }
    
    public void characters(final char[] ch, final int start, final int length) {
        final Node parent = this.nodeStack.peek();
        final Text text = this.document.createTextNode(new String(ch, start, length));
        parent.appendChild(text);
    }
    
    public void ignorableWhitespace(final char[] ch, final int start, final int length) {
    }
    
    public void processingInstruction(final String target, final String data) throws SAXException {
        final Node parent = this.nodeStack.peek();
        final Node node = this.document.createProcessingInstruction(target, data);
        parent.appendChild(node);
    }
    
    public void setDocumentLocator(final Locator locator) {
    }
    
    public void skippedEntity(final String name) {
    }
    
    public void startPrefixMapping(final String prefix, final String uri) {
        this.unprocessedNamespaces.add(prefix);
        this.unprocessedNamespaces.add(uri);
    }
    
    public void endPrefixMapping(final String prefix) {
    }
}
