// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.messagebox.parser;

import org.seamless.xml.DOMElement;
import javax.xml.xpath.XPath;
import org.w3c.dom.Document;
import org.seamless.xml.DOM;

public class MessageDOM extends DOM
{
    public static final String NAMESPACE_URI = "urn:samsung-com:messagebox-1-0";
    
    public MessageDOM(final Document dom) {
        super(dom);
    }
    
    @Override
    public String getRootElementNamespace() {
        return "urn:samsung-com:messagebox-1-0";
    }
    
    @Override
    public MessageElement getRoot(final XPath xPath) {
        return new MessageElement(xPath, this.getW3CDocument().getDocumentElement());
    }
    
    @Override
    public MessageDOM copy() {
        return new MessageDOM((Document)this.getW3CDocument().cloneNode(true));
    }
    
    public MessageElement createRoot(final XPath xpath, final String element) {
        super.createRoot(element);
        return this.getRoot(xpath);
    }
}
