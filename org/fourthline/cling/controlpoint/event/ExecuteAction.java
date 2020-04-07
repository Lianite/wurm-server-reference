// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.controlpoint.event;

import org.fourthline.cling.controlpoint.ActionCallback;

public class ExecuteAction
{
    protected ActionCallback callback;
    
    public ExecuteAction(final ActionCallback callback) {
        this.callback = callback;
    }
    
    public ActionCallback getCallback() {
        return this.callback;
    }
}
