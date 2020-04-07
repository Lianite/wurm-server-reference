// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.internalizer;

import java.util.Iterator;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import javax.xml.namespace.NamespaceContext;

final class NamespaceContextImpl implements NamespaceContext
{
    private final Element e;
    
    public NamespaceContextImpl(final Element e) {
        this.e = e;
    }
    
    public String getNamespaceURI(final String prefix) {
        Node parent = this.e;
        String namespace = null;
        final String prefixColon = prefix + ':';
        if (prefix.equals("xml")) {
            namespace = "http://www.w3.org/XML/1998/namespace";
        }
        else {
            int type;
            while (null != parent && null == namespace && ((type = parent.getNodeType()) == 1 || type == 5)) {
                if (type == 1) {
                    if (parent.getNodeName().startsWith(prefixColon)) {
                        return parent.getNamespaceURI();
                    }
                    final NamedNodeMap nnm = parent.getAttributes();
                    for (int i = 0; i < nnm.getLength(); ++i) {
                        final Node attr = nnm.item(i);
                        final String aname = attr.getNodeName();
                        final boolean isPrefix = aname.startsWith("xmlns:");
                        if (isPrefix || aname.equals("xmlns")) {
                            final int index = aname.indexOf(58);
                            final String p = isPrefix ? aname.substring(index + 1) : "";
                            if (p.equals(prefix)) {
                                namespace = attr.getNodeValue();
                                break;
                            }
                        }
                    }
                }
                parent = parent.getParentNode();
            }
        }
        if (prefix.equals("")) {
            return "";
        }
        return namespace;
    }
    
    public String getPrefix(final String namespaceURI) {
        throw new UnsupportedOperationException();
    }
    
    public Iterator getPrefixes(final String namespaceURI) {
        throw new UnsupportedOperationException();
    }
}
