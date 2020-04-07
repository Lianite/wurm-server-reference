// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.shared.exceptions.WurmServerException;

public final class NoSuchQuestionException extends WurmServerException
{
    private static final long serialVersionUID = -3831089100054421576L;
    
    public NoSuchQuestionException(final String message) {
        super(message);
    }
    
    public NoSuchQuestionException(final Throwable cause) {
        super(cause);
    }
    
    public NoSuchQuestionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
