// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.igd.callback;

import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.model.Connection;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.controlpoint.ActionCallback;

public abstract class GetStatusInfo extends ActionCallback
{
    public GetStatusInfo(final Service service) {
        super(new ActionInvocation(service.getAction("GetStatusInfo")));
    }
    
    @Override
    public void success(final ActionInvocation invocation) {
        try {
            final Connection.Status status = Connection.Status.valueOf(invocation.getOutput("NewConnectionStatus").getValue().toString());
            final Connection.Error lastError = Connection.Error.valueOf(invocation.getOutput("NewLastConnectionError").getValue().toString());
            this.success(new Connection.StatusInfo(status, (UnsignedIntegerFourBytes)invocation.getOutput("NewUptime").getValue(), lastError));
        }
        catch (Exception ex) {
            invocation.setFailure(new ActionException(ErrorCode.ARGUMENT_VALUE_INVALID, "Invalid status or last error string: " + ex, ex));
            this.failure(invocation, null);
        }
    }
    
    protected abstract void success(final Connection.StatusInfo p0);
}
