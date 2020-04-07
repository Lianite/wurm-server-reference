// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.shared.exceptions.WurmServerException;

public final class NoSuchBehaviourException extends WurmServerException
{
    private static final long serialVersionUID = -7889104664023078651L;
    
    public NoSuchBehaviourException(final String message) {
        super(message);
    }
    
    public NoSuchBehaviourException(final Throwable cause) {
        super(cause);
    }
    
    public NoSuchBehaviourException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
