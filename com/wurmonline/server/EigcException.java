// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import com.wurmonline.shared.exceptions.WurmServerException;

public final class EigcException extends WurmServerException
{
    private static final long serialVersionUID = 5813764704231108263L;
    
    public EigcException(final String message) {
        super(message);
    }
    
    public EigcException(final Throwable cause) {
        super(cause);
    }
    
    public EigcException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
