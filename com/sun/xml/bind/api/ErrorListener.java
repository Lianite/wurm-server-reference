// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.api;

import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;

public interface ErrorListener extends ErrorHandler
{
    void error(final SAXParseException p0);
    
    void fatalError(final SAXParseException p0);
    
    void warning(final SAXParseException p0);
    
    void info(final SAXParseException p0);
}
