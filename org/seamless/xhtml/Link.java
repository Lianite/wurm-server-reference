// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.xhtml;

import org.w3c.dom.Element;
import javax.xml.xpath.XPath;

public class Link extends XHTMLElement
{
    public Link(final XPath xpath, final Element element) {
        super(xpath, element);
    }
    
    public Href getHref() {
        return Href.fromString(this.getAttribute(XHTML.ATTR.href));
    }
    
    public String getRel() {
        return this.getAttribute(XHTML.ATTR.rel);
    }
    
    public String getRev() {
        return this.getAttribute(XHTML.ATTR.rev);
    }
}
