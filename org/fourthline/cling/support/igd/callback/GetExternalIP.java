// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.igd.callback;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.controlpoint.ActionCallback;

public abstract class GetExternalIP extends ActionCallback
{
    public GetExternalIP(final Service service) {
        super(new ActionInvocation(service.getAction("GetExternalIPAddress")));
    }
    
    @Override
    public void success(final ActionInvocation invocation) {
        this.success((String)invocation.getOutput("NewExternalIPAddress").getValue());
    }
    
    protected abstract void success(final String p0);
}
