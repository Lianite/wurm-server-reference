// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.contentdirectory;

import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.model.action.ActionException;

public class ContentDirectoryException extends ActionException
{
    public ContentDirectoryException(final int errorCode, final String message) {
        super(errorCode, message);
    }
    
    public ContentDirectoryException(final int errorCode, final String message, final Throwable cause) {
        super(errorCode, message, cause);
    }
    
    public ContentDirectoryException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }
    
    public ContentDirectoryException(final ErrorCode errorCode) {
        super(errorCode);
    }
    
    public ContentDirectoryException(final ContentDirectoryErrorCode errorCode, final String message) {
        super(errorCode.getCode(), errorCode.getDescription() + ". " + message + ".");
    }
    
    public ContentDirectoryException(final ContentDirectoryErrorCode errorCode) {
        super(errorCode.getCode(), errorCode.getDescription());
    }
}
