// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.renderingcontrol;

import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.model.action.ActionException;

public class RenderingControlException extends ActionException
{
    public RenderingControlException(final int errorCode, final String message) {
        super(errorCode, message);
    }
    
    public RenderingControlException(final int errorCode, final String message, final Throwable cause) {
        super(errorCode, message, cause);
    }
    
    public RenderingControlException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }
    
    public RenderingControlException(final ErrorCode errorCode) {
        super(errorCode);
    }
    
    public RenderingControlException(final RenderingControlErrorCode errorCode, final String message) {
        super(errorCode.getCode(), errorCode.getDescription() + ". " + message + ".");
    }
    
    public RenderingControlException(final RenderingControlErrorCode errorCode) {
        super(errorCode.getCode(), errorCode.getDescription());
    }
}
