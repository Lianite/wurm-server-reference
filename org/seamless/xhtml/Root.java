// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.xhtml;

import org.seamless.xml.DOMElement;
import org.w3c.dom.Element;
import javax.xml.xpath.XPath;

public class Root extends XHTMLElement
{
    public Root(final XPath xpath, final Element element) {
        super(xpath, element);
    }
    
    public Head getHead() {
        return new Builder<Head>(this) {
            public Head build(final Element element) {
                return new Head(Root.this.getXpath(), element);
            }
        }.firstChildOrNull(XHTML.ELEMENT.head.name());
    }
    
    public Body getBody() {
        return new Builder<Body>(this) {
            public Body build(final Element element) {
                return new Body(Root.this.getXpath(), element);
            }
        }.firstChildOrNull(XHTML.ELEMENT.body.name());
    }
}
