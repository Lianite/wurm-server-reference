// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model;

import org.fourthline.cling.model.ServiceReference;

public class ConnectionInfo
{
    protected final int connectionID;
    protected final int rcsID;
    protected final int avTransportID;
    protected final ProtocolInfo protocolInfo;
    protected final ServiceReference peerConnectionManager;
    protected final int peerConnectionID;
    protected final Direction direction;
    protected Status connectionStatus;
    
    public ConnectionInfo() {
        this(0, 0, 0, null, null, -1, Direction.Input, Status.Unknown);
    }
    
    public ConnectionInfo(final int connectionID, final int rcsID, final int avTransportID, final ProtocolInfo protocolInfo, final ServiceReference peerConnectionManager, final int peerConnectionID, final Direction direction, final Status connectionStatus) {
        this.connectionStatus = Status.Unknown;
        this.connectionID = connectionID;
        this.rcsID = rcsID;
        this.avTransportID = avTransportID;
        this.protocolInfo = protocolInfo;
        this.peerConnectionManager = peerConnectionManager;
        this.peerConnectionID = peerConnectionID;
        this.direction = direction;
        this.connectionStatus = connectionStatus;
    }
    
    public int getConnectionID() {
        return this.connectionID;
    }
    
    public int getRcsID() {
        return this.rcsID;
    }
    
    public int getAvTransportID() {
        return this.avTransportID;
    }
    
    public ProtocolInfo getProtocolInfo() {
        return this.protocolInfo;
    }
    
    public ServiceReference getPeerConnectionManager() {
        return this.peerConnectionManager;
    }
    
    public int getPeerConnectionID() {
        return this.peerConnectionID;
    }
    
    public Direction getDirection() {
        return this.direction;
    }
    
    public synchronized Status getConnectionStatus() {
        return this.connectionStatus;
    }
    
    public synchronized void setConnectionStatus(final Status connectionStatus) {
        this.connectionStatus = connectionStatus;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final ConnectionInfo that = (ConnectionInfo)o;
        if (this.avTransportID != that.avTransportID) {
            return false;
        }
        if (this.connectionID != that.connectionID) {
            return false;
        }
        if (this.peerConnectionID != that.peerConnectionID) {
            return false;
        }
        if (this.rcsID != that.rcsID) {
            return false;
        }
        if (this.connectionStatus != that.connectionStatus) {
            return false;
        }
        if (this.direction != that.direction) {
            return false;
        }
        Label_0140: {
            if (this.peerConnectionManager != null) {
                if (this.peerConnectionManager.equals(that.peerConnectionManager)) {
                    break Label_0140;
                }
            }
            else if (that.peerConnectionManager == null) {
                break Label_0140;
            }
            return false;
        }
        if (this.protocolInfo != null) {
            if (this.protocolInfo.equals(that.protocolInfo)) {
                return true;
            }
        }
        else if (that.protocolInfo == null) {
            return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int result = this.connectionID;
        result = 31 * result + this.rcsID;
        result = 31 * result + this.avTransportID;
        result = 31 * result + ((this.protocolInfo != null) ? this.protocolInfo.hashCode() : 0);
        result = 31 * result + ((this.peerConnectionManager != null) ? this.peerConnectionManager.hashCode() : 0);
        result = 31 * result + this.peerConnectionID;
        result = 31 * result + this.direction.hashCode();
        result = 31 * result + this.connectionStatus.hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        return "(" + this.getClass().getSimpleName() + ") ID: " + this.getConnectionID() + ", Status: " + this.getConnectionStatus();
    }
    
    public enum Status
    {
        OK, 
        ContentFormatMismatch, 
        InsufficientBandwidth, 
        UnreliableChannel, 
        Unknown;
    }
    
    public enum Direction
    {
        Output, 
        Input;
        
        public Direction getOpposite() {
            return this.equals(Direction.Output) ? Direction.Input : Direction.Output;
        }
    }
}
