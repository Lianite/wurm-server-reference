// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.items;

import com.wurmonline.shared.exceptions.WurmServerException;

public final class NotOwnedException extends WurmServerException
{
    private static final long serialVersionUID = 5160458755595540264L;
    
    public NotOwnedException(final String message) {
        super(message);
    }
    
    public NotOwnedException(final Throwable cause) {
        super(cause);
    }
    
    public NotOwnedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
