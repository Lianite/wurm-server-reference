// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.xhtml;

import org.seamless.xml.DOM;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import org.seamless.xml.NamespaceContextMap;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import java.util.Set;
import java.util.HashSet;
import org.w3c.dom.Document;
import org.seamless.xml.DOMParser;

public class XHTMLParser extends DOMParser<XHTML>
{
    public XHTMLParser() {
        super(XHTML.createSchemaSources());
    }
    
    protected XHTML createDOM(final Document document) {
        return (document != null) ? new XHTML(document) : null;
    }
    
    public void checkDuplicateIdentifiers(final XHTML document) throws IllegalStateException {
        final Set<String> identifiers = new HashSet<String>();
        DOMParser.accept(document.getW3CDocument().getDocumentElement(), new NodeVisitor(1) {
            public void visit(final Node node) {
                final Element element = (Element)node;
                final String id = element.getAttribute(XHTML.ATTR.id.name());
                if (!"".equals(id)) {
                    if (identifiers.contains(id)) {
                        throw new IllegalStateException("Duplicate identifier, override/change value: " + id);
                    }
                    identifiers.add(id);
                }
            }
        });
    }
    
    public NamespaceContextMap createDefaultNamespaceContext(final String... optionalPrefixes) {
        final NamespaceContextMap ctx = new NamespaceContextMap() {
            protected String getDefaultNamespaceURI() {
                return "http://www.w3.org/1999/xhtml";
            }
        };
        for (final String optionalPrefix : optionalPrefixes) {
            ctx.put(optionalPrefix, "http://www.w3.org/1999/xhtml");
        }
        return ctx;
    }
    
    public XPath createXPath() {
        return super.createXPath(this.createDefaultNamespaceContext("h"));
    }
}
