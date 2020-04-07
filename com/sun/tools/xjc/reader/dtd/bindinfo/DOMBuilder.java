// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.dtd.bindinfo;

import org.xml.sax.Attributes;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.Locator;
import com.sun.xml.bind.marshaller.SAX2DOMEx;

final class DOMBuilder extends SAX2DOMEx
{
    private Locator locator;
    
    public void setDocumentLocator(final Locator locator) {
        super.setDocumentLocator(locator);
        this.locator = locator;
    }
    
    public void startElement(final String namespace, final String localName, final String qName, final Attributes attrs) {
        super.startElement(namespace, localName, qName, attrs);
        DOMLocator.setLocationInfo(this.getCurrentElement(), this.locator);
    }
}
