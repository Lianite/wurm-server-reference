// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.support;

import com.wurmonline.shared.exceptions.WurmServerException;

public class TrelloCardNotFoundException extends WurmServerException
{
    private static final long serialVersionUID = 7427993543996731841L;
    
    public TrelloCardNotFoundException(final String message) {
        super(message);
    }
    
    public TrelloCardNotFoundException(final Throwable cause) {
        super(cause);
    }
    
    public TrelloCardNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
