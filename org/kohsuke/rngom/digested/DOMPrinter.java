// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.digested;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import org.w3c.dom.NodeList;
import javax.xml.stream.XMLStreamException;
import org.w3c.dom.Comment;
import org.w3c.dom.EntityReference;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Text;
import org.w3c.dom.Element;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import javax.xml.stream.XMLStreamWriter;

class DOMPrinter
{
    protected XMLStreamWriter out;
    
    public DOMPrinter(final XMLStreamWriter out) {
        this.out = out;
    }
    
    public void print(final Node node) throws XMLStreamException {
        switch (node.getNodeType()) {
            case 9: {
                this.visitDocument((Document)node);
                break;
            }
            case 11: {
                this.visitDocumentFragment((DocumentFragment)node);
                break;
            }
            case 1: {
                this.visitElement((Element)node);
                break;
            }
            case 3: {
                this.visitText((Text)node);
                break;
            }
            case 4: {
                this.visitCDATASection((CDATASection)node);
                break;
            }
            case 7: {
                this.visitProcessingInstruction((ProcessingInstruction)node);
                break;
            }
            case 5: {
                this.visitReference((EntityReference)node);
                break;
            }
            case 8: {
                this.visitComment((Comment)node);
                break;
            }
            case 10: {
                break;
            }
            default: {
                throw new XMLStreamException("Unexpected DOM Node Type " + node.getNodeType());
            }
        }
    }
    
    protected void visitChildren(final Node node) throws XMLStreamException {
        final NodeList nodeList = node.getChildNodes();
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); ++i) {
                this.print(nodeList.item(i));
            }
        }
    }
    
    protected void visitDocument(final Document document) throws XMLStreamException {
        this.out.writeStartDocument();
        this.print(document.getDocumentElement());
        this.out.writeEndDocument();
    }
    
    protected void visitDocumentFragment(final DocumentFragment documentFragment) throws XMLStreamException {
        this.visitChildren(documentFragment);
    }
    
    protected void visitElement(final Element node) throws XMLStreamException {
        this.out.writeStartElement(node.getPrefix(), node.getLocalName(), node.getNamespaceURI());
        final NamedNodeMap attrs = node.getAttributes();
        for (int i = 0; i < attrs.getLength(); ++i) {
            this.visitAttr((Attr)attrs.item(i));
        }
        this.visitChildren(node);
        this.out.writeEndElement();
    }
    
    protected void visitAttr(final Attr node) throws XMLStreamException {
        final String name = node.getLocalName();
        if (name.equals("xmlns")) {
            this.out.writeDefaultNamespace(node.getNamespaceURI());
        }
        else {
            final String prefix = node.getPrefix();
            if (prefix != null && prefix.equals("xmlns")) {
                this.out.writeNamespace(prefix, node.getNamespaceURI());
            }
            else {
                this.out.writeAttribute(prefix, node.getNamespaceURI(), name, node.getNodeValue());
            }
        }
    }
    
    protected void visitComment(final Comment comment) throws XMLStreamException {
        this.out.writeComment(comment.getData());
    }
    
    protected void visitText(final Text node) throws XMLStreamException {
        this.out.writeCharacters(node.getNodeValue());
    }
    
    protected void visitCDATASection(final CDATASection cdata) throws XMLStreamException {
        this.out.writeCData(cdata.getNodeValue());
    }
    
    protected void visitProcessingInstruction(final ProcessingInstruction processingInstruction) throws XMLStreamException {
        this.out.writeProcessingInstruction(processingInstruction.getNodeName(), processingInstruction.getData());
    }
    
    protected void visitReference(final EntityReference entityReference) throws XMLStreamException {
        this.visitChildren(entityReference);
    }
}
