// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.connectionmanager.callback;

import org.fourthline.cling.model.action.ActionArgumentValue;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.support.model.ProtocolInfos;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.controlpoint.ActionCallback;

public abstract class GetProtocolInfo extends ActionCallback
{
    public GetProtocolInfo(final Service service) {
        this(service, null);
    }
    
    protected GetProtocolInfo(final Service service, final ControlPoint controlPoint) {
        super(new ActionInvocation(service.getAction("GetProtocolInfo")), controlPoint);
    }
    
    @Override
    public void success(final ActionInvocation invocation) {
        try {
            final ActionArgumentValue sink = invocation.getOutput("Sink");
            final ActionArgumentValue source = invocation.getOutput("Source");
            this.received(invocation, (sink != null) ? new ProtocolInfos(sink.toString()) : null, (source != null) ? new ProtocolInfos(source.toString()) : null);
        }
        catch (Exception ex) {
            invocation.setFailure(new ActionException(ErrorCode.ACTION_FAILED, "Can't parse ProtocolInfo response: " + ex, ex));
            this.failure(invocation, null);
        }
    }
    
    public abstract void received(final ActionInvocation p0, final ProtocolInfos p1, final ProtocolInfos p2);
}
