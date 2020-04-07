// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures.ai;

import com.wurmonline.shared.exceptions.WurmServerException;

public final class NoPathException extends WurmServerException
{
    private static final long serialVersionUID = 372320709229086812L;
    
    public NoPathException(final String message) {
        super(message);
    }
    
    NoPathException(final Throwable cause) {
        super(cause);
    }
    
    NoPathException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
