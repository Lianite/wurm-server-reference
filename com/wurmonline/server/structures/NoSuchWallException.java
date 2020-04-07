// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.structures;

import com.wurmonline.shared.exceptions.WurmServerException;

public final class NoSuchWallException extends WurmServerException
{
    private static final long serialVersionUID = 2443093162318322030L;
    
    public NoSuchWallException(final String message) {
        super(message);
    }
    
    NoSuchWallException(final Throwable cause) {
        super(cause);
    }
    
    NoSuchWallException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
