// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.villages;

import com.wurmonline.shared.exceptions.WurmServerException;

public final class NoSuchVillageException extends WurmServerException
{
    private static final long serialVersionUID = 1L;
    
    public NoSuchVillageException(final String message) {
        super(message);
    }
    
    NoSuchVillageException(final Throwable cause) {
        super(cause);
    }
    
    NoSuchVillageException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
