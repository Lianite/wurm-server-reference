// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.structures;

import com.wurmonline.shared.exceptions.WurmServerException;

public final class NoSuchLockException extends WurmServerException
{
    private static final long serialVersionUID = 2894616265258932169L;
    
    NoSuchLockException(final String message) {
        super(message);
    }
    
    NoSuchLockException(final Throwable cause) {
        super(cause);
    }
    
    NoSuchLockException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
