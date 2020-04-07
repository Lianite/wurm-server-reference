// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures;

import com.wurmonline.shared.exceptions.WurmServerException;

public final class NoSuchCreatureException extends WurmServerException
{
    private static final long serialVersionUID = -843014199612164008L;
    
    public NoSuchCreatureException(final String message) {
        super(message);
    }
    
    NoSuchCreatureException(final Throwable cause) {
        super(cause);
    }
    
    NoSuchCreatureException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
