// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.connectionmanager;

import org.fourthline.cling.support.connectionmanager.callback.ConnectionComplete;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.support.connectionmanager.callback.PrepareForConnection;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.binding.annotations.UpnpOutputArgument;
import org.fourthline.cling.binding.annotations.UpnpAction;
import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.model.ServiceReference;
import org.fourthline.cling.binding.annotations.UpnpInputArgument;
import org.fourthline.cling.support.model.ProtocolInfo;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.types.csv.CSV;
import java.util.Iterator;
import java.beans.PropertyChangeSupport;
import org.fourthline.cling.support.model.ProtocolInfos;
import org.fourthline.cling.support.model.ConnectionInfo;
import java.util.logging.Logger;

public abstract class AbstractPeeringConnectionManagerService extends ConnectionManagerService
{
    private static final Logger log;
    
    protected AbstractPeeringConnectionManagerService(final ConnectionInfo... activeConnections) {
        super(activeConnections);
    }
    
    protected AbstractPeeringConnectionManagerService(final ProtocolInfos sourceProtocolInfo, final ProtocolInfos sinkProtocolInfo, final ConnectionInfo... activeConnections) {
        super(sourceProtocolInfo, sinkProtocolInfo, activeConnections);
    }
    
    protected AbstractPeeringConnectionManagerService(final PropertyChangeSupport propertyChangeSupport, final ProtocolInfos sourceProtocolInfo, final ProtocolInfos sinkProtocolInfo, final ConnectionInfo... activeConnections) {
        super(propertyChangeSupport, sourceProtocolInfo, sinkProtocolInfo, activeConnections);
    }
    
    protected synchronized int getNewConnectionId() {
        int currentHighestID = -1;
        for (final Integer key : this.activeConnections.keySet()) {
            if (key > currentHighestID) {
                currentHighestID = key;
            }
        }
        return ++currentHighestID;
    }
    
    protected synchronized void storeConnection(final ConnectionInfo info) {
        final CSV<UnsignedIntegerFourBytes> oldConnectionIDs = this.getCurrentConnectionIDs();
        this.activeConnections.put(info.getConnectionID(), info);
        AbstractPeeringConnectionManagerService.log.fine("Connection stored, firing event: " + info.getConnectionID());
        final CSV<UnsignedIntegerFourBytes> newConnectionIDs = this.getCurrentConnectionIDs();
        this.getPropertyChangeSupport().firePropertyChange("CurrentConnectionIDs", oldConnectionIDs, newConnectionIDs);
    }
    
    protected synchronized void removeConnection(final int connectionID) {
        final CSV<UnsignedIntegerFourBytes> oldConnectionIDs = this.getCurrentConnectionIDs();
        this.activeConnections.remove(connectionID);
        AbstractPeeringConnectionManagerService.log.fine("Connection removed, firing event: " + connectionID);
        final CSV<UnsignedIntegerFourBytes> newConnectionIDs = this.getCurrentConnectionIDs();
        this.getPropertyChangeSupport().firePropertyChange("CurrentConnectionIDs", oldConnectionIDs, newConnectionIDs);
    }
    
    @UpnpAction(out = { @UpnpOutputArgument(name = "ConnectionID", stateVariable = "A_ARG_TYPE_ConnectionID", getterName = "getConnectionID"), @UpnpOutputArgument(name = "AVTransportID", stateVariable = "A_ARG_TYPE_AVTransportID", getterName = "getAvTransportID"), @UpnpOutputArgument(name = "RcsID", stateVariable = "A_ARG_TYPE_RcsID", getterName = "getRcsID") })
    public synchronized ConnectionInfo prepareForConnection(@UpnpInputArgument(name = "RemoteProtocolInfo", stateVariable = "A_ARG_TYPE_ProtocolInfo") final ProtocolInfo remoteProtocolInfo, @UpnpInputArgument(name = "PeerConnectionManager", stateVariable = "A_ARG_TYPE_ConnectionManager") final ServiceReference peerConnectionManager, @UpnpInputArgument(name = "PeerConnectionID", stateVariable = "A_ARG_TYPE_ConnectionID") final int peerConnectionId, @UpnpInputArgument(name = "Direction", stateVariable = "A_ARG_TYPE_Direction") final String direction) throws ActionException {
        final int connectionId = this.getNewConnectionId();
        ConnectionInfo.Direction dir;
        try {
            dir = ConnectionInfo.Direction.valueOf(direction);
        }
        catch (Exception ex) {
            throw new ConnectionManagerException(ErrorCode.ARGUMENT_VALUE_INVALID, "Unsupported direction: " + direction);
        }
        AbstractPeeringConnectionManagerService.log.fine("Preparing for connection with local new ID " + connectionId + " and peer connection ID: " + peerConnectionId);
        final ConnectionInfo newConnectionInfo = this.createConnection(connectionId, peerConnectionId, peerConnectionManager, dir, remoteProtocolInfo);
        this.storeConnection(newConnectionInfo);
        return newConnectionInfo;
    }
    
    @UpnpAction
    public synchronized void connectionComplete(@UpnpInputArgument(name = "ConnectionID", stateVariable = "A_ARG_TYPE_ConnectionID") final int connectionID) throws ActionException {
        final ConnectionInfo info = this.getCurrentConnectionInfo(connectionID);
        AbstractPeeringConnectionManagerService.log.fine("Closing connection ID " + connectionID);
        this.closeConnection(info);
        this.removeConnection(connectionID);
    }
    
    public synchronized int createConnectionWithPeer(final ServiceReference localServiceReference, final ControlPoint controlPoint, final Service peerService, final ProtocolInfo protInfo, final ConnectionInfo.Direction direction) {
        final int localConnectionID = this.getNewConnectionId();
        AbstractPeeringConnectionManagerService.log.fine("Creating new connection ID " + localConnectionID + " with peer: " + peerService);
        final boolean[] failed = { false };
        new PrepareForConnection(peerService, controlPoint, protInfo, localServiceReference, localConnectionID, direction) {
            @Override
            public void received(final ActionInvocation invocation, final int peerConnectionID, final int rcsID, final int avTransportID) {
                final ConnectionInfo info = new ConnectionInfo(localConnectionID, rcsID, avTransportID, protInfo, peerService.getReference(), peerConnectionID, direction.getOpposite(), ConnectionInfo.Status.OK);
                AbstractPeeringConnectionManagerService.this.storeConnection(info);
            }
            
            @Override
            public void failure(final ActionInvocation invocation, final UpnpResponse operation, final String defaultMsg) {
                AbstractPeeringConnectionManagerService.this.peerFailure(invocation, operation, defaultMsg);
                failed[0] = true;
            }
        }.run();
        return failed[0] ? -1 : localConnectionID;
    }
    
    public synchronized void closeConnectionWithPeer(final ControlPoint controlPoint, final Service peerService, final int connectionID) throws ActionException {
        this.closeConnectionWithPeer(controlPoint, peerService, this.getCurrentConnectionInfo(connectionID));
    }
    
    public synchronized void closeConnectionWithPeer(final ControlPoint controlPoint, final Service peerService, final ConnectionInfo connectionInfo) throws ActionException {
        AbstractPeeringConnectionManagerService.log.fine("Closing connection ID " + connectionInfo.getConnectionID() + " with peer: " + peerService);
        new ConnectionComplete(peerService, controlPoint, connectionInfo.getPeerConnectionID()) {
            @Override
            public void success(final ActionInvocation invocation) {
                AbstractPeeringConnectionManagerService.this.removeConnection(connectionInfo.getConnectionID());
            }
            
            @Override
            public void failure(final ActionInvocation invocation, final UpnpResponse operation, final String defaultMsg) {
                AbstractPeeringConnectionManagerService.this.peerFailure(invocation, operation, defaultMsg);
            }
        }.run();
    }
    
    protected abstract ConnectionInfo createConnection(final int p0, final int p1, final ServiceReference p2, final ConnectionInfo.Direction p3, final ProtocolInfo p4) throws ActionException;
    
    protected abstract void closeConnection(final ConnectionInfo p0);
    
    protected abstract void peerFailure(final ActionInvocation p0, final UpnpResponse p1, final String p2);
    
    static {
        log = Logger.getLogger(AbstractPeeringConnectionManagerService.class.getName());
    }
}
