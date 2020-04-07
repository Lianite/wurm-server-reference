// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.xhtml;

import java.util.List;
import java.util.ArrayList;
import org.w3c.dom.Element;
import javax.xml.xpath.XPath;
import org.seamless.xml.DOMElement;

public class XHTMLElement extends DOMElement<XHTMLElement, XHTMLElement>
{
    public static final String XPATH_PREFIX = "h";
    
    public XHTMLElement(final XPath xpath, final Element element) {
        super(xpath, element);
    }
    
    protected Builder<XHTMLElement> createParentBuilder(final DOMElement el) {
        return new Builder<XHTMLElement>(el) {
            public XHTMLElement build(final Element element) {
                return new XHTMLElement(XHTMLElement.this.getXpath(), element);
            }
        };
    }
    
    protected ArrayBuilder<XHTMLElement> createChildBuilder(final DOMElement el) {
        return new ArrayBuilder<XHTMLElement>(el) {
            public XHTMLElement[] newChildrenArray(final int length) {
                return new XHTMLElement[length];
            }
            
            public XHTMLElement build(final Element element) {
                return new XHTMLElement(XHTMLElement.this.getXpath(), element);
            }
        };
    }
    
    protected String prefix(final String localName) {
        return "h:" + localName;
    }
    
    public XHTML.ELEMENT getConstant() {
        return XHTML.ELEMENT.valueOf(this.getElementName());
    }
    
    public XHTMLElement[] getChildren(final XHTML.ELEMENT el) {
        return super.getChildren(el.name());
    }
    
    public XHTMLElement getFirstChild(final XHTML.ELEMENT el) {
        return super.getFirstChild(el.name());
    }
    
    public XHTMLElement[] findChildren(final XHTML.ELEMENT el) {
        return super.findChildren(el.name());
    }
    
    public XHTMLElement createChild(final XHTML.ELEMENT el) {
        return super.createChild(el.name(), "http://www.w3.org/1999/xhtml");
    }
    
    public String getAttribute(final XHTML.ATTR attribute) {
        return this.getAttribute(attribute.name());
    }
    
    public XHTMLElement setAttribute(final XHTML.ATTR attribute, final String value) {
        super.setAttribute(attribute.name(), value);
        return this;
    }
    
    public String getId() {
        return this.getAttribute(XHTML.ATTR.id);
    }
    
    public XHTMLElement setId(final String id) {
        this.setAttribute(XHTML.ATTR.id, id);
        return this;
    }
    
    public String getTitle() {
        return this.getAttribute(XHTML.ATTR.title);
    }
    
    public XHTMLElement setTitle(final String title) {
        this.setAttribute(XHTML.ATTR.title, title);
        return this;
    }
    
    public XHTMLElement setClasses(final String classes) {
        this.setAttribute("class", classes);
        return this;
    }
    
    public XHTMLElement setClasses(final String[] classes) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < classes.length; ++i) {
            sb.append(classes[i]);
            if (i != classes.length - 1) {
                sb.append(" ");
            }
        }
        this.setAttribute("class", sb.toString());
        return this;
    }
    
    public String[] getClasses() {
        final String v = this.getAttribute("class");
        if (v == null) {
            return new String[0];
        }
        return v.split(" ");
    }
    
    public Option[] getOptions() {
        return Option.fromString(this.getAttribute(XHTML.ATTR.style));
    }
    
    public Option getOption(final String key) {
        for (final Option option : this.getOptions()) {
            if (option.getKey().equals(key)) {
                return option;
            }
        }
        return null;
    }
    
    public Anchor[] findAllAnchors() {
        return this.findAllAnchors(null, null);
    }
    
    public Anchor[] findAllAnchors(final String requiredScheme) {
        return this.findAllAnchors(requiredScheme, null);
    }
    
    public Anchor[] findAllAnchors(final String requiredScheme, final String requiredClass) {
        final XHTMLElement[] elements = this.findChildrenWithClass(XHTML.ELEMENT.a, requiredClass);
        final List<Anchor> anchors = new ArrayList<Anchor>(elements.length);
        for (final XHTMLElement element : elements) {
            final String href = element.getAttribute(XHTML.ATTR.href);
            if (requiredScheme == null || (href != null && href.startsWith(requiredScheme))) {
                anchors.add(new Anchor(this.getXpath(), element.getW3CElement()));
            }
        }
        return anchors.toArray(new Anchor[anchors.size()]);
    }
    
    public XHTMLElement[] findChildrenWithClass(final XHTML.ELEMENT el, final String clazz) {
        final List<XHTMLElement> list = new ArrayList<XHTMLElement>();
        final XHTMLElement[] arr$;
        final XHTMLElement[] children = arr$ = this.findChildren(el);
        for (final XHTMLElement child : arr$) {
            if (clazz == null) {
                list.add(child);
            }
            else {
                for (final String c : child.getClasses()) {
                    if (c.matches(clazz)) {
                        list.add(child);
                        break;
                    }
                }
            }
        }
        return list.toArray((XHTMLElement[])this.CHILD_BUILDER.newChildrenArray(list.size()));
    }
    
    public XHTMLElement setContent(final String content) {
        super.setContent(content);
        return this;
    }
    
    public XHTMLElement setAttribute(final String attribute, final String value) {
        super.setAttribute(attribute, value);
        return this;
    }
}
