// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.registry.event;

import org.fourthline.cling.model.meta.RemoteDevice;

public class FailedRemoteDeviceDiscovery extends DeviceDiscovery<RemoteDevice>
{
    protected Exception exception;
    
    public FailedRemoteDeviceDiscovery(final RemoteDevice device, final Exception ex) {
        super(device);
        this.exception = ex;
    }
    
    public Exception getException() {
        return this.exception;
    }
}
