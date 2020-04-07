// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.registry.event;

import org.fourthline.cling.model.meta.Device;

public class DeviceDiscovery<D extends Device>
{
    protected D device;
    
    public DeviceDiscovery(final D device) {
        this.device = device;
    }
    
    public D getDevice() {
        return this.device;
    }
}
