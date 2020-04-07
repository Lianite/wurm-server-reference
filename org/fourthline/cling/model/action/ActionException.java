// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.action;

import org.fourthline.cling.model.types.ErrorCode;

public class ActionException extends Exception
{
    private int errorCode;
    
    public ActionException(final int errorCode, final String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public ActionException(final int errorCode, final String message, final Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public ActionException(final ErrorCode errorCode) {
        this(errorCode.getCode(), errorCode.getDescription());
    }
    
    public ActionException(final ErrorCode errorCode, final String message) {
        this(errorCode, message, true);
    }
    
    public ActionException(final ErrorCode errorCode, final String message, final boolean concatMessages) {
        this(errorCode.getCode(), concatMessages ? (errorCode.getDescription() + ". " + message + ".") : message);
    }
    
    public ActionException(final ErrorCode errorCode, final String message, final Throwable cause) {
        this(errorCode.getCode(), errorCode.getDescription() + ". " + message + ".", cause);
    }
    
    public int getErrorCode() {
        return this.errorCode;
    }
}
