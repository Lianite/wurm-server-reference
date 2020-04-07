// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.dtd.bindinfo;

import org.dom4j.ElementPath;
import org.dom4j.ElementHandler;
import org.dom4j.DocumentFactory;
import org.xml.sax.Locator;
import org.dom4j.io.SAXContentHandler;

class SAXContentHandlerEx extends SAXContentHandler
{
    private final Locator[] loc;
    
    public static SAXContentHandlerEx create() {
        return new SAXContentHandlerEx(new Locator[1]);
    }
    
    private SAXContentHandlerEx(final Locator[] loc) {
        super(DocumentFactory.getInstance(), (ElementHandler)new MyElementHandler(loc));
        this.loc = loc;
    }
    
    public void setDocumentLocator(final Locator _loc) {
        super.setDocumentLocator(this.loc[0] = _loc);
    }
    
    static class MyElementHandler implements ElementHandler
    {
        private final Locator[] loc;
        
        MyElementHandler(final Locator[] loc) {
            this.loc = loc;
        }
        
        public void onStart(final ElementPath path) {
            DOM4JLocator.setLocationInfo(path.getCurrent(), this.loc[0]);
        }
        
        public void onEnd(final ElementPath path) {
        }
    }
}
