// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import com.wurmonline.shared.exceptions.WurmServerException;

public final class NoSuchPlayerException extends WurmServerException
{
    private static final long serialVersionUID = 8878640068891218711L;
    
    public NoSuchPlayerException(final String message) {
        super(message);
    }
    
    public NoSuchPlayerException(final Throwable cause) {
        super(cause);
    }
    
    public NoSuchPlayerException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
