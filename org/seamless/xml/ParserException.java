// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.xml;

import org.xml.sax.SAXParseException;

public class ParserException extends Exception
{
    public ParserException() {
    }
    
    public ParserException(final String s) {
        super(s);
    }
    
    public ParserException(final String s, final Throwable throwable) {
        super(s, throwable);
    }
    
    public ParserException(final Throwable throwable) {
        super(throwable);
    }
    
    public ParserException(final SAXParseException ex) {
        super("(Line/Column: " + ex.getLineNumber() + ":" + ex.getColumnNumber() + ") " + ex.getMessage());
    }
}
