// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.connectionmanager.callback;

import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.support.model.ConnectionInfo;
import org.fourthline.cling.model.ServiceReference;
import org.fourthline.cling.support.model.ProtocolInfo;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.controlpoint.ActionCallback;

public abstract class GetCurrentConnectionInfo extends ActionCallback
{
    public GetCurrentConnectionInfo(final Service service, final int connectionID) {
        this(service, null, connectionID);
    }
    
    protected GetCurrentConnectionInfo(final Service service, final ControlPoint controlPoint, final int connectionID) {
        super(new ActionInvocation(service.getAction("GetCurrentConnectionInfo")), controlPoint);
        this.getActionInvocation().setInput("ConnectionID", connectionID);
    }
    
    @Override
    public void success(final ActionInvocation invocation) {
        try {
            final ConnectionInfo info = new ConnectionInfo((int)invocation.getInput("ConnectionID").getValue(), (int)invocation.getOutput("RcsID").getValue(), (int)invocation.getOutput("AVTransportID").getValue(), new ProtocolInfo(invocation.getOutput("ProtocolInfo").toString()), new ServiceReference(invocation.getOutput("PeerConnectionManager").toString()), (int)invocation.getOutput("PeerConnectionID").getValue(), ConnectionInfo.Direction.valueOf(invocation.getOutput("Direction").toString()), ConnectionInfo.Status.valueOf(invocation.getOutput("Status").toString()));
            this.received(invocation, info);
        }
        catch (Exception ex) {
            invocation.setFailure(new ActionException(ErrorCode.ACTION_FAILED, "Can't parse ConnectionInfo response: " + ex, ex));
            this.failure(invocation, null);
        }
    }
    
    public abstract void received(final ActionInvocation p0, final ConnectionInfo p1);
}
