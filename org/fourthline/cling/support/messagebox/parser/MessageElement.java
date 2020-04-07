// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.messagebox.parser;

import org.w3c.dom.Element;
import javax.xml.xpath.XPath;
import org.seamless.xml.DOMElement;

public class MessageElement extends DOMElement<MessageElement, MessageElement>
{
    public static final String XPATH_PREFIX = "m";
    
    public MessageElement(final XPath xpath, final Element element) {
        super(xpath, element);
    }
    
    @Override
    protected String prefix(final String localName) {
        return "m:" + localName;
    }
    
    @Override
    protected Builder<MessageElement> createParentBuilder(final DOMElement el) {
        return new Builder<MessageElement>(el) {
            @Override
            public MessageElement build(final Element element) {
                return new MessageElement(MessageElement.this.getXpath(), element);
            }
        };
    }
    
    @Override
    protected ArrayBuilder<MessageElement> createChildBuilder(final DOMElement el) {
        return new ArrayBuilder<MessageElement>(el) {
            @Override
            public MessageElement[] newChildrenArray(final int length) {
                return new MessageElement[length];
            }
            
            @Override
            public MessageElement build(final Element element) {
                return new MessageElement(MessageElement.this.getXpath(), element);
            }
        };
    }
}
