// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.xml;

import javax.xml.xpath.XPath;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import java.net.URI;

public abstract class DOM
{
    public static final URI XML_SCHEMA_NAMESPACE;
    public static final String CDATA_BEGIN = "<![CDATA[";
    public static final String CDATA_END = "]]>";
    private Document dom;
    
    public DOM(final Document dom) {
        this.dom = dom;
    }
    
    public Document getW3CDocument() {
        return this.dom;
    }
    
    public Element createRoot(final String name) {
        final Element el = this.getW3CDocument().createElementNS(this.getRootElementNamespace(), name);
        this.getW3CDocument().appendChild(el);
        return el;
    }
    
    public abstract String getRootElementNamespace();
    
    public abstract DOMElement getRoot(final XPath p0);
    
    public abstract DOM copy();
    
    static {
        XML_SCHEMA_NAMESPACE = URI.create("http://www.w3.org/2001/xml.xsd");
    }
}
