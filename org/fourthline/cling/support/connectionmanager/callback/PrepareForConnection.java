// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.connectionmanager.callback;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.support.model.ConnectionInfo;
import org.fourthline.cling.model.ServiceReference;
import org.fourthline.cling.support.model.ProtocolInfo;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.controlpoint.ActionCallback;

public abstract class PrepareForConnection extends ActionCallback
{
    public PrepareForConnection(final Service service, final ProtocolInfo remoteProtocolInfo, final ServiceReference peerConnectionManager, final int peerConnectionID, final ConnectionInfo.Direction direction) {
        this(service, null, remoteProtocolInfo, peerConnectionManager, peerConnectionID, direction);
    }
    
    public PrepareForConnection(final Service service, final ControlPoint controlPoint, final ProtocolInfo remoteProtocolInfo, final ServiceReference peerConnectionManager, final int peerConnectionID, final ConnectionInfo.Direction direction) {
        super(new ActionInvocation(service.getAction("PrepareForConnection")), controlPoint);
        this.getActionInvocation().setInput("RemoteProtocolInfo", remoteProtocolInfo.toString());
        this.getActionInvocation().setInput("PeerConnectionManager", peerConnectionManager.toString());
        this.getActionInvocation().setInput("PeerConnectionID", peerConnectionID);
        this.getActionInvocation().setInput("Direction", direction.toString());
    }
    
    @Override
    public void success(final ActionInvocation invocation) {
        this.received(invocation, (int)invocation.getOutput("ConnectionID").getValue(), (int)invocation.getOutput("RcsID").getValue(), (int)invocation.getOutput("AVTransportID").getValue());
    }
    
    public abstract void received(final ActionInvocation p0, final int p1, final int p2, final int p3);
}
