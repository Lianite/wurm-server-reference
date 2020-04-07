// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures;

import com.wurmonline.shared.exceptions.WurmServerException;

public final class NoSuchCreatureTemplateException extends WurmServerException
{
    private static final long serialVersionUID = 9155452762336621872L;
    
    NoSuchCreatureTemplateException(final String message) {
        super(message);
    }
    
    NoSuchCreatureTemplateException(final Throwable cause) {
        super(cause);
    }
    
    NoSuchCreatureTemplateException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
