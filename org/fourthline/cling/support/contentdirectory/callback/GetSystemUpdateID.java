// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.contentdirectory.callback;

import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.controlpoint.ActionCallback;

public abstract class GetSystemUpdateID extends ActionCallback
{
    public GetSystemUpdateID(final Service service) {
        super(new ActionInvocation(service.getAction("GetSystemUpdateID")));
    }
    
    @Override
    public void success(final ActionInvocation invocation) {
        boolean ok = true;
        long id = 0L;
        try {
            id = Long.valueOf(invocation.getOutput("Id").getValue().toString());
        }
        catch (Exception ex) {
            invocation.setFailure(new ActionException(ErrorCode.ACTION_FAILED, "Can't parse GetSystemUpdateID response: " + ex, ex));
            this.failure(invocation, null);
            ok = false;
        }
        if (ok) {
            this.received(invocation, id);
        }
    }
    
    public abstract void received(final ActionInvocation p0, final long p1);
}
