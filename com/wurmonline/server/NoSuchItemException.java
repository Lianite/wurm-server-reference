// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import com.wurmonline.shared.exceptions.WurmServerException;

public final class NoSuchItemException extends WurmServerException
{
    private static final long serialVersionUID = -4699460609829035442L;
    
    public NoSuchItemException(final String message) {
        super(message);
    }
    
    public NoSuchItemException(final Throwable cause) {
        super(cause);
    }
    
    public NoSuchItemException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
