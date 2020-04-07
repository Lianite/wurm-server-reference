// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.util;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;

public class FatalAdapter implements ErrorHandler
{
    private final ErrorHandler core;
    
    public FatalAdapter(final ErrorHandler handler) {
        this.core = handler;
    }
    
    public void warning(final SAXParseException exception) throws SAXException {
        this.core.warning(exception);
    }
    
    public void error(final SAXParseException exception) throws SAXException {
        this.core.fatalError(exception);
    }
    
    public void fatalError(final SAXParseException exception) throws SAXException {
        this.core.fatalError(exception);
    }
}
