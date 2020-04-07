// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.dtd.bindinfo;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

public final class DOMUtil
{
    static final String getAttribute(final Element e, final String attName) {
        if (e.getAttributeNode(attName) == null) {
            return null;
        }
        return e.getAttribute(attName);
    }
    
    public static String getAttribute(final Element e, final String nsUri, final String local) {
        if (e.getAttributeNodeNS(nsUri, local) == null) {
            return null;
        }
        return e.getAttributeNS(nsUri, local);
    }
    
    public static Element getElement(final Element e, final String nsUri, final String localName) {
        final NodeList l = e.getChildNodes();
        for (int i = 0; i < l.getLength(); ++i) {
            final Node n = l.item(i);
            if (n.getNodeType() == 1) {
                final Element r = (Element)n;
                if (equals(r.getLocalName(), localName) && equals(fixNull(r.getNamespaceURI()), nsUri)) {
                    return r;
                }
            }
        }
        return null;
    }
    
    private static boolean equals(final String a, final String b) {
        return a == b || (a != null && b != null && a.equals(b));
    }
    
    private static String fixNull(final String s) {
        if (s == null) {
            return "";
        }
        return s;
    }
    
    public static Element getElement(final Element e, final String localName) {
        return getElement(e, "", localName);
    }
    
    public static List<Element> getChildElements(final Element e) {
        final List<Element> r = new ArrayList<Element>();
        final NodeList l = e.getChildNodes();
        for (int i = 0; i < l.getLength(); ++i) {
            final Node n = l.item(i);
            if (n.getNodeType() == 1) {
                r.add((Element)n);
            }
        }
        return r;
    }
    
    public static List<Element> getChildElements(final Element e, final String localName) {
        final List<Element> r = new ArrayList<Element>();
        final NodeList l = e.getChildNodes();
        for (int i = 0; i < l.getLength(); ++i) {
            final Node n = l.item(i);
            if (n.getNodeType() == 1) {
                final Element c = (Element)n;
                if (c.getLocalName().equals(localName)) {
                    r.add(c);
                }
            }
        }
        return r;
    }
}
