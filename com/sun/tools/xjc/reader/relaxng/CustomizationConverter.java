// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.relaxng;

import com.sun.tools.xjc.util.DOMUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import com.sun.tools.xjc.reader.internalizer.DOMForest;
import com.sun.tools.xjc.Options;

public class CustomizationConverter
{
    private final Options options;
    
    public CustomizationConverter(final Options opts) {
        this.options = opts;
    }
    
    public void fixup(final DOMForest forest) {
        final Document[] docs = forest.listDocuments();
        for (int i = 0; i < docs.length; ++i) {
            this.fixup(docs[i].getDocumentElement());
        }
    }
    
    private void fixup(final Element e) {
        if (!e.getNamespaceURI().equals("http://relaxng.org/ns/structure/1.0")) {
            return;
        }
        final NodeList nl = e.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            final Node c = nl.item(i);
            if (c instanceof Element) {
                final Element child = (Element)c;
                final String childNS = child.getNamespaceURI();
                if (childNS.equals("http://relaxng.org/ns/structure/1.0")) {
                    this.fixup(child);
                }
                else if (childNS.equals("http://java.sun.com/xml/ns/jaxb") || childNS.equals("http://java.sun.com/xml/ns/jaxb/xjc")) {
                    this.fixup(e, child);
                }
            }
        }
    }
    
    private void fixup(final Element parent, final Element child) {
        final String name = child.getLocalName().intern();
        if (name == "schemaBindings") {
            final Element p = DOMUtils.getFirstChildElement(child, "http://java.sun.com/xml/ns/jaxb", "package");
            if (p == null) {
                return;
            }
            parent.setAttributeNS("http://java.sun.com/xml/ns/jaxb", "package", p.getAttribute("name"));
        }
        else {
            if (name == "class") {
                parent.setAttributeNS("http://java.sun.com/xml/ns/jaxb", "role", "class");
                copyAttribute(parent, child, "name");
                return;
            }
            if (name == "property") {
                parent.setAttributeNS("http://java.sun.com/xml/ns/jaxb", "role", "field");
                copyAttribute(parent, child, "name");
                return;
            }
            if (name == "javaType") {
                parent.setAttributeNS("http://java.sun.com/xml/ns/jaxb", "role", "primitive");
                copyAttribute(parent, child, "name");
                copyAttribute(parent, child, "parseMethod");
                copyAttribute(parent, child, "printMethod");
                copyAttribute(parent, child, "hasNsContext");
                return;
            }
            if (name == "interface") {
                parent.setAttributeNS("http://java.sun.com/xml/ns/jaxb", "role", "interface");
                copyAttribute(parent, child, "name");
                return;
            }
            if (name == "ignore") {
                parent.setAttributeNS("http://java.sun.com/xml/ns/jaxb", "role", "ignore");
                return;
            }
            if (name == "super") {
                parent.setAttributeNS("http://java.sun.com/xml/ns/jaxb", "role", "superClass");
                return;
            }
            if (name == "dom") {
                parent.setAttributeNS("http://java.sun.com/xml/ns/jaxb", "role", "dom");
                copyAttribute(parent, child, "type");
                return;
            }
            if (name == "noMarshaller") {
                this.options.generateMarshallingCode = false;
                return;
            }
            if (name == "noUnmarshaller") {
                this.options.generateUnmarshallingCode = false;
                this.options.generateValidatingUnmarshallingCode = false;
                return;
            }
            if (name == "noValidator") {
                this.options.generateValidationCode = false;
                return;
            }
            if (name == "noValidatingUnmarshaller") {
                this.options.generateValidatingUnmarshallingCode = false;
            }
        }
    }
    
    private static void copyAttribute(final Element dst, final Element src, final String attName) {
        if (src.getAttributeNode(attName) != null) {
            dst.setAttributeNS("http://java.sun.com/xml/ns/jaxb", attName, src.getAttribute(attName));
        }
    }
}
