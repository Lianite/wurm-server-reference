// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.zones;

import com.wurmonline.shared.exceptions.WurmServerException;

public final class NoSuchTileException extends WurmServerException
{
    private static final long serialVersionUID = -974263427293465936L;
    
    NoSuchTileException(final String message) {
        super(message);
    }
    
    NoSuchTileException(final Throwable cause) {
        super(cause);
    }
    
    NoSuchTileException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
