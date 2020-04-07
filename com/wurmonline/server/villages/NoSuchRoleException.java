// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.villages;

import com.wurmonline.shared.exceptions.WurmServerException;

public final class NoSuchRoleException extends WurmServerException
{
    private static final long serialVersionUID = -6630727392157751483L;
    
    public NoSuchRoleException(final String message) {
        super(message);
    }
    
    public NoSuchRoleException(final Throwable cause) {
        super(cause);
    }
    
    public NoSuchRoleException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
