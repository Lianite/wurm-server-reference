// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.util;

import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import javax.xml.transform.Result;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import com.sun.xml.xsom.parser.AnnotationContext;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.TransformerFactory;
import com.sun.xml.xsom.parser.AnnotationParser;
import javax.xml.transform.sax.SAXTransformerFactory;
import com.sun.xml.xsom.parser.AnnotationParserFactory;

public class DomAnnotationParserFactory implements AnnotationParserFactory
{
    private static final SAXTransformerFactory stf;
    
    public AnnotationParser create() {
        return new AnnotationParserImpl();
    }
    
    static {
        stf = (SAXTransformerFactory)TransformerFactory.newInstance();
    }
    
    private static class AnnotationParserImpl extends AnnotationParser
    {
        private final TransformerHandler transformer;
        private DOMResult result;
        
        AnnotationParserImpl() {
            try {
                this.transformer = DomAnnotationParserFactory.stf.newTransformerHandler();
            }
            catch (TransformerConfigurationException e) {
                throw new Error(e);
            }
        }
        
        public ContentHandler getContentHandler(final AnnotationContext context, final String parentElementName, final ErrorHandler errorHandler, final EntityResolver entityResolver) {
            this.result = new DOMResult();
            this.transformer.setResult(this.result);
            return this.transformer;
        }
        
        public Object getResult(final Object existing) {
            final Document dom = (Document)this.result.getNode();
            final Element e = dom.getDocumentElement();
            if (existing instanceof Element) {
                final Element prev = (Element)existing;
                final Node anchor = e.getFirstChild();
                while (prev.getFirstChild() != null) {
                    final Node move = prev.getFirstChild();
                    e.insertBefore(e.getOwnerDocument().adoptNode(move), anchor);
                }
            }
            return e;
        }
    }
}
