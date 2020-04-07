// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.shared.exceptions.WurmServerException;

public final class NoSuchActionException extends WurmServerException
{
    private static final long serialVersionUID = 7185872169527936353L;
    
    public NoSuchActionException(final String message) {
        super(message);
    }
    
    public NoSuchActionException(final Throwable cause) {
        super(cause);
    }
    
    public NoSuchActionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
