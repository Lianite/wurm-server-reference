// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model;

import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;

public class AVTransport
{
    protected final UnsignedIntegerFourBytes instanceID;
    protected final LastChange lastChange;
    protected MediaInfo mediaInfo;
    protected TransportInfo transportInfo;
    protected PositionInfo positionInfo;
    protected DeviceCapabilities deviceCapabilities;
    protected TransportSettings transportSettings;
    
    public AVTransport(final UnsignedIntegerFourBytes instanceID, final LastChange lastChange, final StorageMedium possiblePlayMedium) {
        this(instanceID, lastChange, new StorageMedium[] { possiblePlayMedium });
    }
    
    public AVTransport(final UnsignedIntegerFourBytes instanceID, final LastChange lastChange, final StorageMedium[] possiblePlayMedia) {
        this.instanceID = instanceID;
        this.lastChange = lastChange;
        this.setDeviceCapabilities(new DeviceCapabilities(possiblePlayMedia));
        this.setMediaInfo(new MediaInfo());
        this.setTransportInfo(new TransportInfo());
        this.setPositionInfo(new PositionInfo());
        this.setTransportSettings(new TransportSettings());
    }
    
    public UnsignedIntegerFourBytes getInstanceId() {
        return this.instanceID;
    }
    
    public LastChange getLastChange() {
        return this.lastChange;
    }
    
    public MediaInfo getMediaInfo() {
        return this.mediaInfo;
    }
    
    public void setMediaInfo(final MediaInfo mediaInfo) {
        this.mediaInfo = mediaInfo;
    }
    
    public TransportInfo getTransportInfo() {
        return this.transportInfo;
    }
    
    public void setTransportInfo(final TransportInfo transportInfo) {
        this.transportInfo = transportInfo;
    }
    
    public PositionInfo getPositionInfo() {
        return this.positionInfo;
    }
    
    public void setPositionInfo(final PositionInfo positionInfo) {
        this.positionInfo = positionInfo;
    }
    
    public DeviceCapabilities getDeviceCapabilities() {
        return this.deviceCapabilities;
    }
    
    public void setDeviceCapabilities(final DeviceCapabilities deviceCapabilities) {
        this.deviceCapabilities = deviceCapabilities;
    }
    
    public TransportSettings getTransportSettings() {
        return this.transportSettings;
    }
    
    public void setTransportSettings(final TransportSettings transportSettings) {
        this.transportSettings = transportSettings;
    }
}
