// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl.util;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;

public class DraconianErrorHandler implements ErrorHandler
{
    public void error(final SAXParseException e) throws SAXException {
        throw e;
    }
    
    public void fatalError(final SAXParseException e) throws SAXException {
        throw e;
    }
    
    public void warning(final SAXParseException e) {
    }
}
