// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.connectionmanager;

import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.model.action.ActionException;

public class ConnectionManagerException extends ActionException
{
    public ConnectionManagerException(final int errorCode, final String message) {
        super(errorCode, message);
    }
    
    public ConnectionManagerException(final int errorCode, final String message, final Throwable cause) {
        super(errorCode, message, cause);
    }
    
    public ConnectionManagerException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }
    
    public ConnectionManagerException(final ErrorCode errorCode) {
        super(errorCode);
    }
    
    public ConnectionManagerException(final ConnectionManagerErrorCode errorCode, final String message) {
        super(errorCode.getCode(), errorCode.getDescription() + ". " + message + ".");
    }
    
    public ConnectionManagerException(final ConnectionManagerErrorCode errorCode) {
        super(errorCode.getCode(), errorCode.getDescription());
    }
}
