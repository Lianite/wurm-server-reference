// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.zones;

import com.wurmonline.shared.exceptions.WurmServerException;

public final class NoSuchZoneException extends WurmServerException
{
    private static final long serialVersionUID = 7094119477458750028L;
    
    NoSuchZoneException(final String message) {
        super(message);
    }
    
    NoSuchZoneException(final Throwable cause) {
        super(cause);
    }
    
    NoSuchZoneException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
