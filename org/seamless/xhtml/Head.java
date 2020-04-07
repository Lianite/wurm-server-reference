// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.xhtml;

import org.seamless.xml.DOMElement;
import org.w3c.dom.Element;
import javax.xml.xpath.XPath;

public class Head extends XHTMLElement
{
    public Head(final XPath xpath, final Element element) {
        super(xpath, element);
    }
    
    public XHTMLElement getHeadTitle() {
        return this.CHILD_BUILDER.firstChildOrNull(XHTML.ELEMENT.title.name());
    }
    
    public Link[] getLinks() {
        return new ArrayBuilder<Link>(this) {
            public Link build(final Element element) {
                return new Link(Head.this.getXpath(), element);
            }
            
            public Link[] newChildrenArray(final int length) {
                return new Link[length];
            }
        }.getChildElements(XHTML.ELEMENT.link.name());
    }
    
    public Meta[] getMetas() {
        return new ArrayBuilder<Meta>(this) {
            public Meta build(final Element element) {
                return new Meta(Head.this.getXpath(), element);
            }
            
            public Meta[] newChildrenArray(final int length) {
                return new Meta[length];
            }
        }.getChildElements(XHTML.ELEMENT.meta.name());
    }
    
    public XHTMLElement[] getDocumentStyles() {
        return (XHTMLElement[])this.CHILD_BUILDER.getChildElements(XHTML.ELEMENT.style.name());
    }
    
    public XHTMLElement[] getScripts() {
        return (XHTMLElement[])this.CHILD_BUILDER.getChildElements(XHTML.ELEMENT.script.name());
    }
}
