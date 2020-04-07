// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model;

import org.w3c.dom.NodeList;
import java.util.Iterator;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import java.util.Map;
import java.util.HashMap;
import org.w3c.dom.Element;
import java.util.Set;
import org.w3c.dom.Node;
import java.util.HashSet;
import org.w3c.dom.Document;

public class XMLUtil
{
    public static String documentToString(final Document document) throws Exception {
        return documentToString(document, true);
    }
    
    public static String documentToString(final Document document, final boolean standalone) throws Exception {
        final String prol = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"" + (standalone ? "yes" : "no") + "\"?>";
        return prol + nodeToString(document.getDocumentElement(), new HashSet<String>(), document.getDocumentElement().getNamespaceURI());
    }
    
    public static String documentToFragmentString(final Document document) throws Exception {
        return nodeToString(document.getDocumentElement(), new HashSet<String>(), document.getDocumentElement().getNamespaceURI());
    }
    
    protected static String nodeToString(final Node node, final Set<String> parentPrefixes, final String namespaceURI) throws Exception {
        final StringBuilder b = new StringBuilder();
        if (node == null) {
            return "";
        }
        if (node instanceof Element) {
            final Element element = (Element)node;
            b.append("<");
            b.append(element.getNodeName());
            final Map<String, String> thisLevelPrefixes = new HashMap<String, String>();
            if (element.getPrefix() != null && !parentPrefixes.contains(element.getPrefix())) {
                thisLevelPrefixes.put(element.getPrefix(), element.getNamespaceURI());
            }
            if (element.hasAttributes()) {
                final NamedNodeMap map = element.getAttributes();
                for (int i = 0; i < map.getLength(); ++i) {
                    final Node attr = map.item(i);
                    if (!attr.getNodeName().startsWith("xmlns")) {
                        if (attr.getPrefix() != null && !parentPrefixes.contains(attr.getPrefix())) {
                            thisLevelPrefixes.put(attr.getPrefix(), element.getNamespaceURI());
                        }
                        b.append(" ");
                        b.append(attr.getNodeName());
                        b.append("=\"");
                        b.append(attr.getNodeValue());
                        b.append("\"");
                    }
                }
            }
            if (namespaceURI != null && !thisLevelPrefixes.containsValue(namespaceURI) && !namespaceURI.equals(element.getParentNode().getNamespaceURI())) {
                b.append(" xmlns=\"").append(namespaceURI).append("\"");
            }
            for (final Map.Entry<String, String> entry : thisLevelPrefixes.entrySet()) {
                b.append(" xmlns:").append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
                parentPrefixes.add(entry.getKey());
            }
            final NodeList children = element.getChildNodes();
            boolean hasOnlyAttributes = true;
            for (int j = 0; j < children.getLength(); ++j) {
                final Node child = children.item(j);
                if (child.getNodeType() != 2) {
                    hasOnlyAttributes = false;
                    break;
                }
            }
            if (!hasOnlyAttributes) {
                b.append(">");
                for (int j = 0; j < children.getLength(); ++j) {
                    b.append(nodeToString(children.item(j), parentPrefixes, children.item(j).getNamespaceURI()));
                }
                b.append("</");
                b.append(element.getNodeName());
                b.append(">");
            }
            else {
                b.append("/>");
            }
            for (final String thisLevelPrefix : thisLevelPrefixes.keySet()) {
                parentPrefixes.remove(thisLevelPrefix);
            }
        }
        else if (node.getNodeValue() != null) {
            b.append(encodeText(node.getNodeValue(), node instanceof Attr));
        }
        return b.toString();
    }
    
    public static String encodeText(final String s) {
        return encodeText(s, true);
    }
    
    public static String encodeText(String s, final boolean encodeQuotes) {
        s = s.replaceAll("&", "&amp;");
        s = s.replaceAll("<", "&lt;");
        s = s.replaceAll(">", "&gt;");
        if (encodeQuotes) {
            s = s.replaceAll("'", "&apos;");
            s = s.replaceAll("\"", "&quot;");
        }
        return s;
    }
    
    public static Element appendNewElement(final Document document, final Element parent, final Enum el) {
        return appendNewElement(document, parent, el.toString());
    }
    
    public static Element appendNewElement(final Document document, final Element parent, final String element) {
        final Element child = document.createElement(element);
        parent.appendChild(child);
        return child;
    }
    
    public static Element appendNewElementIfNotNull(final Document document, final Element parent, final Enum el, final Object content) {
        return appendNewElementIfNotNull(document, parent, el, content, null);
    }
    
    public static Element appendNewElementIfNotNull(final Document document, final Element parent, final Enum el, final Object content, final String namespace) {
        return appendNewElementIfNotNull(document, parent, el.toString(), content, namespace);
    }
    
    public static Element appendNewElementIfNotNull(final Document document, final Element parent, final String element, final Object content) {
        return appendNewElementIfNotNull(document, parent, element, content, null);
    }
    
    public static Element appendNewElementIfNotNull(final Document document, final Element parent, final String element, final Object content, final String namespace) {
        if (content == null) {
            return parent;
        }
        return appendNewElement(document, parent, element, content, namespace);
    }
    
    public static Element appendNewElement(final Document document, final Element parent, final String element, final Object content) {
        return appendNewElement(document, parent, element, content, null);
    }
    
    public static Element appendNewElement(final Document document, final Element parent, final String element, final Object content, final String namespace) {
        Element childElement;
        if (namespace != null) {
            childElement = document.createElementNS(namespace, element);
        }
        else {
            childElement = document.createElement(element);
        }
        if (content != null) {
            childElement.appendChild(document.createTextNode(content.toString()));
        }
        parent.appendChild(childElement);
        return childElement;
    }
    
    public static String getTextContent(final Node node) {
        final StringBuffer buffer = new StringBuffer();
        final NodeList childList = node.getChildNodes();
        for (int i = 0; i < childList.getLength(); ++i) {
            final Node child = childList.item(i);
            if (child.getNodeType() == 3) {
                buffer.append(child.getNodeValue());
            }
        }
        return buffer.toString();
    }
}
