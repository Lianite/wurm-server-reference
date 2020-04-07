// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.txw2;

public class IllegalAnnotationException extends TxwException
{
    private static final long serialVersionUID = 1L;
    
    public IllegalAnnotationException(final String message) {
        super(message);
    }
    
    public IllegalAnnotationException(final Throwable cause) {
        super(cause);
    }
    
    public IllegalAnnotationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
