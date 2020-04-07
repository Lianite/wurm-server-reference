// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.shared.exceptions;

public class WurmClientException extends WurmException
{
    private static final long serialVersionUID = 1268608703615765075L;
    
    public WurmClientException(final String message) {
        super(message);
    }
    
    public WurmClientException(final Throwable cause) {
        super(cause);
    }
    
    public WurmClientException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
