// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.messagebox.parser;

import org.seamless.xml.DOM;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import org.seamless.xml.NamespaceContextMap;
import org.w3c.dom.Document;
import org.seamless.xml.DOMParser;

public class MessageDOMParser extends DOMParser<MessageDOM>
{
    @Override
    protected MessageDOM createDOM(final Document document) {
        return new MessageDOM(document);
    }
    
    public NamespaceContextMap createDefaultNamespaceContext(final String... optionalPrefixes) {
        final NamespaceContextMap ctx = new NamespaceContextMap() {
            @Override
            protected String getDefaultNamespaceURI() {
                return "urn:samsung-com:messagebox-1-0";
            }
        };
        for (final String optionalPrefix : optionalPrefixes) {
            ctx.put(optionalPrefix, "urn:samsung-com:messagebox-1-0");
        }
        return ctx;
    }
    
    public XPath createXPath() {
        return super.createXPath(this.createDefaultNamespaceContext("m"));
    }
}
