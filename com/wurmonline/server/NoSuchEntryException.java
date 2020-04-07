// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import com.wurmonline.shared.exceptions.WurmServerException;

public final class NoSuchEntryException extends WurmServerException
{
    private static final long serialVersionUID = 5813764704231108263L;
    
    public NoSuchEntryException(final String message) {
        super(message);
    }
    
    public NoSuchEntryException(final Throwable cause) {
        super(cause);
    }
    
    public NoSuchEntryException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
