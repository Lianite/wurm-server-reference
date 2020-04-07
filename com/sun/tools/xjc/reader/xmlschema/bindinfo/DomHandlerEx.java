// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.bindinfo;

import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.helpers.XMLFilterImpl;
import javax.xml.parsers.ParserConfigurationException;
import com.sun.xml.bind.marshaller.SAX2DOMEx;
import javax.xml.transform.sax.SAXResult;
import org.xml.sax.Locator;
import org.w3c.dom.Element;
import javax.xml.transform.Result;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.Source;
import org.w3c.dom.Document;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.DomHandler;

final class DomHandlerEx implements DomHandler<DomAndLocation, ResultImpl>
{
    public ResultImpl createUnmarshaller(final ValidationEventHandler errorHandler) {
        return new ResultImpl();
    }
    
    public DomAndLocation getElement(final ResultImpl r) {
        return new DomAndLocation(((Document)r.s2d.getDOM()).getDocumentElement(), r.location);
    }
    
    public Source marshal(final DomAndLocation domAndLocation, final ValidationEventHandler errorHandler) {
        return new DOMSource(domAndLocation.element);
    }
    
    public static final class DomAndLocation
    {
        public final Element element;
        public final Locator loc;
        
        public DomAndLocation(final Element element, final Locator loc) {
            this.element = element;
            this.loc = loc;
        }
    }
    
    public static final class ResultImpl extends SAXResult
    {
        final SAX2DOMEx s2d;
        Locator location;
        
        ResultImpl() {
            this.location = null;
            try {
                this.s2d = new SAX2DOMEx();
            }
            catch (ParserConfigurationException e) {
                throw new AssertionError((Object)e);
            }
            final XMLFilterImpl f = new XMLFilterImpl() {
                public void setDocumentLocator(final Locator locator) {
                    super.setDocumentLocator(locator);
                    ResultImpl.this.location = new LocatorImpl(locator);
                }
            };
            f.setContentHandler(this.s2d);
            this.setHandler(f);
        }
    }
}
