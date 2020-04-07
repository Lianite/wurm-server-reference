// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.items;

import com.wurmonline.shared.exceptions.WurmServerException;

public final class NoSuchTemplateException extends WurmServerException
{
    private static final long serialVersionUID = 1157557174258373795L;
    
    public NoSuchTemplateException(final String message) {
        super(message);
    }
    
    public NoSuchTemplateException(final Throwable cause) {
        super(cause);
    }
    
    public NoSuchTemplateException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
