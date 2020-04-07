// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures;

import com.wurmonline.shared.exceptions.WurmServerException;

public final class NoArmourException extends WurmServerException
{
    private static final long serialVersionUID = 9021493151024263335L;
    
    public NoArmourException(final String message) {
        super(message);
    }
    
    NoArmourException(final Throwable cause) {
        super(cause);
    }
    
    NoArmourException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
