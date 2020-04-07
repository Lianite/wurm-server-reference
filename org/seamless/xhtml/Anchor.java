// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.xhtml;

import org.w3c.dom.Element;
import javax.xml.xpath.XPath;

public class Anchor extends XHTMLElement
{
    public Anchor(final XPath xpath, final Element element) {
        super(xpath, element);
    }
    
    public String getType() {
        return this.getAttribute(XHTML.ATTR.type);
    }
    
    public Anchor setType(final String type) {
        this.setAttribute(XHTML.ATTR.type, type);
        return this;
    }
    
    public Href getHref() {
        return Href.fromString(this.getAttribute(XHTML.ATTR.href));
    }
    
    public Anchor setHref(final String href) {
        this.setAttribute(XHTML.ATTR.href, href);
        return this;
    }
    
    public String toString() {
        return "(Anchor) " + this.getAttribute(XHTML.ATTR.href);
    }
}
