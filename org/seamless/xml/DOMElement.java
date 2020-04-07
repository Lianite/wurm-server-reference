// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.xml;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathConstants;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Document;
import java.util.Collection;
import org.w3c.dom.Node;
import java.util.List;
import org.w3c.dom.NodeList;
import java.util.ArrayList;
import org.w3c.dom.Element;
import javax.xml.xpath.XPath;

public abstract class DOMElement<CHILD extends DOMElement, PARENT extends DOMElement>
{
    public final Builder<PARENT> PARENT_BUILDER;
    public final ArrayBuilder<CHILD> CHILD_BUILDER;
    private final XPath xpath;
    private Element element;
    
    public DOMElement(final XPath xpath, final Element element) {
        this.xpath = xpath;
        this.element = element;
        this.PARENT_BUILDER = this.createParentBuilder(this);
        this.CHILD_BUILDER = this.createChildBuilder(this);
    }
    
    public Element getW3CElement() {
        return this.element;
    }
    
    public String getElementName() {
        return this.getW3CElement().getNodeName();
    }
    
    public String getContent() {
        return this.getW3CElement().getTextContent();
    }
    
    public DOMElement<CHILD, PARENT> setContent(final String content) {
        this.getW3CElement().setTextContent(content);
        return this;
    }
    
    public String getAttribute(final String attribute) {
        final String v = this.getW3CElement().getAttribute(attribute);
        return (v.length() > 0) ? v : null;
    }
    
    public DOMElement setAttribute(final String attribute, final String value) {
        this.getW3CElement().setAttribute(attribute, value);
        return this;
    }
    
    public PARENT getParent() {
        return this.PARENT_BUILDER.build((Element)this.getW3CElement().getParentNode());
    }
    
    public CHILD[] getChildren() {
        final NodeList nodes = this.getW3CElement().getChildNodes();
        final List<CHILD> children = new ArrayList<CHILD>();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            if (node.getNodeType() == 1) {
                children.add(this.CHILD_BUILDER.build((Element)node));
            }
        }
        return children.toArray(this.CHILD_BUILDER.newChildrenArray(children.size()));
    }
    
    public CHILD[] getChildren(final String name) {
        final Collection<CHILD> list = this.getXPathChildElements(this.CHILD_BUILDER, this.prefix(name));
        return list.toArray(this.CHILD_BUILDER.newChildrenArray(list.size()));
    }
    
    public CHILD getRequiredChild(final String name) throws ParserException {
        final CHILD[] children = this.getChildren(name);
        if (children.length != 1) {
            throw new ParserException("Required single child element of '" + this.getElementName() + "' not found: " + name);
        }
        return children[0];
    }
    
    public CHILD[] findChildren(final String name) {
        final Collection<CHILD> list = this.getXPathChildElements(this.CHILD_BUILDER, "descendant::" + this.prefix(name));
        return list.toArray(this.CHILD_BUILDER.newChildrenArray(list.size()));
    }
    
    public CHILD findChildWithIdentifier(final String id) {
        final Collection<CHILD> list = this.getXPathChildElements(this.CHILD_BUILDER, "descendant::" + this.prefix("*") + "[@id=\"" + id + "\"]");
        if (list.size() == 1) {
            return list.iterator().next();
        }
        return null;
    }
    
    public CHILD getFirstChild(final String name) {
        return this.getXPathChildElement(this.CHILD_BUILDER, this.prefix(name) + "[1]");
    }
    
    public CHILD createChild(final String name) {
        return this.createChild(name, null);
    }
    
    public CHILD createChild(final String name, final String namespaceURI) {
        final CHILD child = this.CHILD_BUILDER.build((namespaceURI == null) ? this.getW3CElement().getOwnerDocument().createElement(name) : this.getW3CElement().getOwnerDocument().createElementNS(namespaceURI, name));
        this.getW3CElement().appendChild(child.getW3CElement());
        return child;
    }
    
    public CHILD appendChild(CHILD el, final boolean copy) {
        el = this.adoptOrImport(this.getW3CElement().getOwnerDocument(), el, copy);
        this.getW3CElement().appendChild(el.getW3CElement());
        return el;
    }
    
    public CHILD replaceChild(final CHILD original, CHILD replacement, final boolean copy) {
        replacement = this.adoptOrImport(this.getW3CElement().getOwnerDocument(), replacement, copy);
        this.getW3CElement().replaceChild(replacement.getW3CElement(), original.getW3CElement());
        return replacement;
    }
    
    public void replaceEqualChild(final DOMElement source, final String identifier) {
        final DOMElement original = this.findChildWithIdentifier(identifier);
        final DOMElement replacement = source.findChildWithIdentifier(identifier);
        original.getParent().replaceChild(original, replacement, true);
    }
    
    public void removeChild(final CHILD el) {
        this.getW3CElement().removeChild(el.getW3CElement());
    }
    
    public void removeChildren() {
        final NodeList children = this.getW3CElement().getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            final Node child = children.item(i);
            this.getW3CElement().removeChild(child);
        }
    }
    
    protected CHILD adoptOrImport(final Document document, CHILD child, final boolean copy) {
        if (document != null) {
            if (copy) {
                child = this.CHILD_BUILDER.build((Element)document.importNode(child.getW3CElement(), true));
            }
            else {
                child = this.CHILD_BUILDER.build((Element)document.adoptNode(child.getW3CElement()));
            }
        }
        return child;
    }
    
    protected abstract Builder<PARENT> createParentBuilder(final DOMElement p0);
    
    protected abstract ArrayBuilder<CHILD> createChildBuilder(final DOMElement p0);
    
    public String toSimpleXMLString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<").append(this.getElementName());
        final NamedNodeMap map = this.getW3CElement().getAttributes();
        for (int i = 0; i < map.getLength(); ++i) {
            final Node attr = map.item(i);
            sb.append(" ").append(attr.getNodeName()).append("=\"").append(attr.getTextContent()).append("\"");
        }
        if (this.getContent().length() > 0) {
            sb.append(">").append(this.getContent()).append("</").append(this.getElementName()).append(">");
        }
        else {
            sb.append("/>");
        }
        return sb.toString();
    }
    
    public String toString() {
        return "(" + this.getClass().getSimpleName() + ") " + ((this.getW3CElement() == null) ? "UNBOUND" : this.getElementName());
    }
    
    public XPath getXpath() {
        return this.xpath;
    }
    
    protected String prefix(final String localName) {
        return localName;
    }
    
    public Collection<PARENT> getXPathParentElements(final Builder<CHILD> builder, final String expr) {
        return (Collection<PARENT>)this.getXPathElements(builder, expr);
    }
    
    public Collection<CHILD> getXPathChildElements(final Builder<CHILD> builder, final String expr) {
        return (Collection<CHILD>)this.getXPathElements(builder, expr);
    }
    
    public PARENT getXPathParentElement(final Builder<PARENT> builder, final String expr) {
        final Node node = (Node)this.getXPathResult(this.getW3CElement(), expr, XPathConstants.NODE);
        return (PARENT)((node != null && node.getNodeType() == 1) ? builder.build((Element)node) : null);
    }
    
    public CHILD getXPathChildElement(final Builder<CHILD> builder, final String expr) {
        final Node node = (Node)this.getXPathResult(this.getW3CElement(), expr, XPathConstants.NODE);
        return (CHILD)((node != null && node.getNodeType() == 1) ? builder.build((Element)node) : null);
    }
    
    public Collection getXPathElements(final Builder builder, final String expr) {
        final Collection col = new ArrayList();
        final NodeList result = (NodeList)this.getXPathResult(this.getW3CElement(), expr, XPathConstants.NODESET);
        for (int i = 0; i < result.getLength(); ++i) {
            final DOMElement e = builder.build((Element)result.item(i));
            col.add(e);
        }
        return col;
    }
    
    public String getXPathString(final XPath xpath, final String expr) {
        return this.getXPathResult(this.getW3CElement(), expr, null).toString();
    }
    
    public Object getXPathResult(final String expr, final QName result) {
        return this.getXPathResult(this.getW3CElement(), expr, result);
    }
    
    public Object getXPathResult(final Node context, final String expr, final QName result) {
        try {
            if (result == null) {
                return this.xpath.evaluate(expr, context);
            }
            return this.xpath.evaluate(expr, context, result);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public abstract class Builder<T extends DOMElement>
    {
        public DOMElement element;
        
        protected Builder(final DOMElement element) {
            this.element = element;
        }
        
        public abstract T build(final Element p0);
        
        public T firstChildOrNull(final String elementName) {
            final DOMElement el = this.element.getFirstChild(elementName);
            return (T)((el != null) ? this.build(el.getW3CElement()) : null);
        }
    }
    
    public abstract class ArrayBuilder<T extends DOMElement> extends Builder<T>
    {
        protected ArrayBuilder(final DOMElement element) {
            super(element);
        }
        
        public abstract T[] newChildrenArray(final int p0);
        
        public T[] getChildElements() {
            return this.buildArray(this.element.getChildren());
        }
        
        public T[] getChildElements(final String elementName) {
            return this.buildArray(this.element.getChildren(elementName));
        }
        
        protected T[] buildArray(final DOMElement[] list) {
            final T[] children = this.newChildrenArray(list.length);
            for (int i = 0; i < children.length; ++i) {
                children[i] = this.build(list[i].getW3CElement());
            }
            return children;
        }
    }
}
