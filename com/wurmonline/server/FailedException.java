// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import com.wurmonline.shared.exceptions.WurmServerException;

public final class FailedException extends WurmServerException
{
    private static final long serialVersionUID = 3728193914548210778L;
    
    public FailedException(final String message) {
        super(message);
    }
    
    public FailedException(final Throwable cause) {
        super(cause);
    }
    
    public FailedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
