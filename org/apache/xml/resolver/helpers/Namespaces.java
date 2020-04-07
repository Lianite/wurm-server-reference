// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.xml.resolver.helpers;

import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class Namespaces
{
    public static String getPrefix(final Element element) {
        final String tagName = element.getTagName();
        String substring = "";
        if (tagName.indexOf(58) > 0) {
            substring = tagName.substring(0, tagName.indexOf(58));
        }
        return substring;
    }
    
    public static String getLocalName(final Element element) {
        String s = element.getTagName();
        if (s.indexOf(58) > 0) {
            s = s.substring(s.indexOf(58) + 1);
        }
        return s;
    }
    
    public static String getNamespaceURI(final Node node, final String s) {
        if (node == null || node.getNodeType() != 1) {
            return null;
        }
        if (s.equals("")) {
            if (((Element)node).hasAttribute("xmlns")) {
                return ((Element)node).getAttribute("xmlns");
            }
        }
        else {
            final String string = "xmlns:" + s;
            if (((Element)node).hasAttribute(string)) {
                return ((Element)node).getAttribute(string);
            }
        }
        return getNamespaceURI(node.getParentNode(), s);
    }
    
    public static String getNamespaceURI(final Element element) {
        return getNamespaceURI(element, getPrefix(element));
    }
}
