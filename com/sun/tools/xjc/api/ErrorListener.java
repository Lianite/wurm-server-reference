// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.api;

import org.xml.sax.SAXParseException;

public interface ErrorListener extends com.sun.xml.bind.api.ErrorListener
{
    void error(final SAXParseException p0);
    
    void fatalError(final SAXParseException p0);
    
    void warning(final SAXParseException p0);
    
    void info(final SAXParseException p0);
}
