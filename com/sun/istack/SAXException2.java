// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.istack;

import org.xml.sax.SAXException;

public class SAXException2 extends SAXException
{
    public SAXException2(final String message) {
        super(message);
    }
    
    public SAXException2(final Exception e) {
        super(e);
    }
    
    public SAXException2(final String message, final Exception e) {
        super(message, e);
    }
    
    public Throwable getCause() {
        return this.getException();
    }
}
