// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.support;

public class JSONException extends RuntimeException
{
    private static final long serialVersionUID = 0L;
    
    public JSONException(final String message) {
        super(message);
    }
    
    public JSONException(final Throwable cause) {
        super(cause.getMessage(), cause);
    }
    
    public JSONException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
