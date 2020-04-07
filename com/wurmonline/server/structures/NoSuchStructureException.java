// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.structures;

import com.wurmonline.shared.exceptions.WurmServerException;

public final class NoSuchStructureException extends WurmServerException
{
    private static final long serialVersionUID = 7841234936326217783L;
    
    public NoSuchStructureException(final String message) {
        super(message);
    }
    
    NoSuchStructureException(final Throwable cause) {
        super(cause);
    }
    
    NoSuchStructureException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
