// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.action;

import org.fourthline.cling.model.types.ErrorCode;

public class ActionCancelledException extends ActionException
{
    public ActionCancelledException(final InterruptedException cause) {
        super(ErrorCode.ACTION_FAILED, "Action execution interrupted", cause);
    }
}
