// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.effects;

import com.wurmonline.shared.exceptions.WurmServerException;

public final class NoSuchEffectException extends WurmServerException
{
    private static final long serialVersionUID = 1247908282339057518L;
    
    public NoSuchEffectException(final String message) {
        super(message);
    }
    
    public NoSuchEffectException(final Throwable cause) {
        super(cause);
    }
    
    public NoSuchEffectException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
