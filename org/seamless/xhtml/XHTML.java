// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.xhtml;

import org.seamless.xml.DOMElement;
import javax.xml.xpath.XPath;
import org.w3c.dom.Document;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Source;
import org.seamless.xml.DOM;

public class XHTML extends DOM
{
    public static final String NAMESPACE_URI = "http://www.w3.org/1999/xhtml";
    public static final String SCHEMA_RESOURCE = "org/seamless/schemas/xhtml1-strict.xsd";
    
    public static Source[] createSchemaSources() {
        return new Source[] { new StreamSource(XHTML.class.getClassLoader().getResourceAsStream("org/seamless/schemas/xhtml1-strict.xsd")) };
    }
    
    public XHTML(final Document dom) {
        super(dom);
    }
    
    public Root createRoot(final XPath xpath, final ELEMENT elememt) {
        super.createRoot(elememt.name());
        return this.getRoot(xpath);
    }
    
    public String getRootElementNamespace() {
        return "http://www.w3.org/1999/xhtml";
    }
    
    public Root getRoot(final XPath xpath) {
        return new Root(xpath, this.getW3CDocument().getDocumentElement());
    }
    
    public XHTML copy() {
        return new XHTML((Document)this.getW3CDocument().cloneNode(true));
    }
    
    public enum ELEMENT
    {
        html, 
        head, 
        title, 
        meta, 
        link, 
        script, 
        style, 
        body, 
        div, 
        span, 
        p, 
        object, 
        a, 
        img, 
        pre, 
        h1, 
        h2, 
        h3, 
        h4, 
        h5, 
        h6, 
        table, 
        thead, 
        tfoot, 
        tbody, 
        tr, 
        th, 
        td, 
        ul, 
        ol, 
        li, 
        dl, 
        dt, 
        dd, 
        form, 
        input, 
        select, 
        option;
    }
    
    public enum ATTR
    {
        id, 
        style, 
        title, 
        type, 
        href, 
        name, 
        content, 
        scheme, 
        rel, 
        rev, 
        colspan, 
        rowspan, 
        src, 
        alt, 
        action, 
        method;
        
        public static final String CLASS = "class";
    }
}
