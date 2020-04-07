// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.api.impl.s2j;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;

final class DowngradingErrorHandler implements ErrorHandler
{
    private final ErrorHandler core;
    
    public DowngradingErrorHandler(final ErrorHandler core) {
        this.core = core;
    }
    
    public void warning(final SAXParseException exception) throws SAXException {
        this.core.warning(exception);
    }
    
    public void error(final SAXParseException exception) throws SAXException {
        this.core.warning(exception);
    }
    
    public void fatalError(final SAXParseException exception) throws SAXException {
        this.core.warning(exception);
    }
}
