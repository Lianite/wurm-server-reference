// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.avtransport;

import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.model.action.ActionException;

public class AVTransportException extends ActionException
{
    public AVTransportException(final int errorCode, final String message) {
        super(errorCode, message);
    }
    
    public AVTransportException(final int errorCode, final String message, final Throwable cause) {
        super(errorCode, message, cause);
    }
    
    public AVTransportException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }
    
    public AVTransportException(final ErrorCode errorCode) {
        super(errorCode);
    }
    
    public AVTransportException(final AVTransportErrorCode errorCode, final String message) {
        super(errorCode.getCode(), errorCode.getDescription() + ". " + message + ".");
    }
    
    public AVTransportException(final AVTransportErrorCode errorCode) {
        super(errorCode.getCode(), errorCode.getDescription());
    }
}
