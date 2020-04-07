// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.connectionmanager;

import java.util.Iterator;
import org.fourthline.cling.model.types.csv.CSVUnsignedIntegerFourBytes;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.types.csv.CSV;
import org.fourthline.cling.binding.annotations.UpnpOutputArgument;
import org.fourthline.cling.binding.annotations.UpnpAction;
import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.binding.annotations.UpnpInputArgument;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.beans.PropertyChangeSupport;
import java.util.logging.Logger;
import org.fourthline.cling.support.model.ConnectionInfo;
import org.fourthline.cling.binding.annotations.UpnpStateVariable;
import org.fourthline.cling.binding.annotations.UpnpStateVariables;
import org.fourthline.cling.model.ServiceReference;
import org.fourthline.cling.support.model.ProtocolInfos;
import org.fourthline.cling.support.model.ProtocolInfo;
import org.fourthline.cling.binding.annotations.UpnpServiceType;
import org.fourthline.cling.binding.annotations.UpnpServiceId;
import org.fourthline.cling.binding.annotations.UpnpService;

@UpnpService(serviceId = @UpnpServiceId("ConnectionManager"), serviceType = @UpnpServiceType(value = "ConnectionManager", version = 1), stringConvertibleTypes = { ProtocolInfo.class, ProtocolInfos.class, ServiceReference.class })
@UpnpStateVariables({ @UpnpStateVariable(name = "SourceProtocolInfo", datatype = "string"), @UpnpStateVariable(name = "SinkProtocolInfo", datatype = "string"), @UpnpStateVariable(name = "CurrentConnectionIDs", datatype = "string"), @UpnpStateVariable(name = "A_ARG_TYPE_ConnectionStatus", allowedValuesEnum = ConnectionInfo.Status.class, sendEvents = false), @UpnpStateVariable(name = "A_ARG_TYPE_ConnectionManager", datatype = "string", sendEvents = false), @UpnpStateVariable(name = "A_ARG_TYPE_Direction", allowedValuesEnum = ConnectionInfo.Direction.class, sendEvents = false), @UpnpStateVariable(name = "A_ARG_TYPE_ProtocolInfo", datatype = "string", sendEvents = false), @UpnpStateVariable(name = "A_ARG_TYPE_ConnectionID", datatype = "i4", sendEvents = false), @UpnpStateVariable(name = "A_ARG_TYPE_AVTransportID", datatype = "i4", sendEvents = false), @UpnpStateVariable(name = "A_ARG_TYPE_RcsID", datatype = "i4", sendEvents = false) })
public class ConnectionManagerService
{
    private static final Logger log;
    protected final PropertyChangeSupport propertyChangeSupport;
    protected final Map<Integer, ConnectionInfo> activeConnections;
    protected final ProtocolInfos sourceProtocolInfo;
    protected final ProtocolInfos sinkProtocolInfo;
    
    public ConnectionManagerService() {
        this(new ConnectionInfo[] { new ConnectionInfo() });
    }
    
    public ConnectionManagerService(final ProtocolInfos sourceProtocolInfo, final ProtocolInfos sinkProtocolInfo) {
        this(sourceProtocolInfo, sinkProtocolInfo, new ConnectionInfo[] { new ConnectionInfo() });
    }
    
    public ConnectionManagerService(final ConnectionInfo... activeConnections) {
        this(null, new ProtocolInfos(new ProtocolInfo[0]), new ProtocolInfos(new ProtocolInfo[0]), activeConnections);
    }
    
    public ConnectionManagerService(final ProtocolInfos sourceProtocolInfo, final ProtocolInfos sinkProtocolInfo, final ConnectionInfo... activeConnections) {
        this(null, sourceProtocolInfo, sinkProtocolInfo, activeConnections);
    }
    
    public ConnectionManagerService(final PropertyChangeSupport propertyChangeSupport, final ProtocolInfos sourceProtocolInfo, final ProtocolInfos sinkProtocolInfo, final ConnectionInfo... activeConnections) {
        this.activeConnections = new ConcurrentHashMap<Integer, ConnectionInfo>();
        this.propertyChangeSupport = ((propertyChangeSupport == null) ? new PropertyChangeSupport(this) : propertyChangeSupport);
        this.sourceProtocolInfo = sourceProtocolInfo;
        this.sinkProtocolInfo = sinkProtocolInfo;
        for (final ConnectionInfo activeConnection : activeConnections) {
            this.activeConnections.put(activeConnection.getConnectionID(), activeConnection);
        }
    }
    
    public PropertyChangeSupport getPropertyChangeSupport() {
        return this.propertyChangeSupport;
    }
    
    @UpnpAction(out = { @UpnpOutputArgument(name = "RcsID", getterName = "getRcsID"), @UpnpOutputArgument(name = "AVTransportID", getterName = "getAvTransportID"), @UpnpOutputArgument(name = "ProtocolInfo", getterName = "getProtocolInfo"), @UpnpOutputArgument(name = "PeerConnectionManager", stateVariable = "A_ARG_TYPE_ConnectionManager", getterName = "getPeerConnectionManager"), @UpnpOutputArgument(name = "PeerConnectionID", stateVariable = "A_ARG_TYPE_ConnectionID", getterName = "getPeerConnectionID"), @UpnpOutputArgument(name = "Direction", getterName = "getDirection"), @UpnpOutputArgument(name = "Status", stateVariable = "A_ARG_TYPE_ConnectionStatus", getterName = "getConnectionStatus") })
    public synchronized ConnectionInfo getCurrentConnectionInfo(@UpnpInputArgument(name = "ConnectionID") final int connectionId) throws ActionException {
        ConnectionManagerService.log.fine("Getting connection information of connection ID: " + connectionId);
        final ConnectionInfo info;
        if ((info = this.activeConnections.get(connectionId)) == null) {
            throw new ConnectionManagerException(ConnectionManagerErrorCode.INVALID_CONNECTION_REFERENCE, "Non-active connection ID: " + connectionId);
        }
        return info;
    }
    
    @UpnpAction(out = { @UpnpOutputArgument(name = "ConnectionIDs") })
    public synchronized CSV<UnsignedIntegerFourBytes> getCurrentConnectionIDs() {
        final CSV<UnsignedIntegerFourBytes> csv = new CSVUnsignedIntegerFourBytes();
        for (final Integer connectionID : this.activeConnections.keySet()) {
            csv.add(new UnsignedIntegerFourBytes(connectionID));
        }
        ConnectionManagerService.log.fine("Returning current connection IDs: " + csv.size());
        return csv;
    }
    
    @UpnpAction(out = { @UpnpOutputArgument(name = "Source", stateVariable = "SourceProtocolInfo", getterName = "getSourceProtocolInfo"), @UpnpOutputArgument(name = "Sink", stateVariable = "SinkProtocolInfo", getterName = "getSinkProtocolInfo") })
    public synchronized void getProtocolInfo() throws ActionException {
    }
    
    public synchronized ProtocolInfos getSourceProtocolInfo() {
        return this.sourceProtocolInfo;
    }
    
    public synchronized ProtocolInfos getSinkProtocolInfo() {
        return this.sinkProtocolInfo;
    }
    
    static {
        log = Logger.getLogger(ConnectionManagerService.class.getName());
    }
}
