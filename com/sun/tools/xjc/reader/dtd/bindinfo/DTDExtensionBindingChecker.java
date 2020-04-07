// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.dtd.bindinfo;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.reader.AbstractExtensionBindingChecker;

final class DTDExtensionBindingChecker extends AbstractExtensionBindingChecker
{
    public DTDExtensionBindingChecker(final String schemaLanguage, final Options options, final ErrorHandler handler) {
        super(schemaLanguage, options, handler);
    }
    
    private boolean needsToBePruned(final String uri) {
        return !uri.equals(this.schemaLanguage) && !uri.equals("http://java.sun.com/xml/ns/jaxb") && !uri.equals("http://java.sun.com/xml/ns/jaxb/xjc") && this.enabledExtensions.contains(uri);
    }
    
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts) throws SAXException {
        if (!this.isCutting() && !uri.equals("")) {
            this.checkAndEnable(uri);
            this.verifyTagName(uri, localName, qName);
            if (this.needsToBePruned(uri)) {
                this.startCutting();
            }
        }
        super.startElement(uri, localName, qName, atts);
    }
}
