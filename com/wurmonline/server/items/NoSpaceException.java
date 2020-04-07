// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.items;

import com.wurmonline.shared.exceptions.WurmServerException;

public final class NoSpaceException extends WurmServerException
{
    private static final long serialVersionUID = -7007492502695022234L;
    
    public NoSpaceException(final String message) {
        super(message);
    }
    
    public NoSpaceException(final Throwable cause) {
        super(cause);
    }
    
    public NoSpaceException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
