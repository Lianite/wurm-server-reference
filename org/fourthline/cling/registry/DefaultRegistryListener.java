// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.registry;

import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.RemoteDevice;

public class DefaultRegistryListener implements RegistryListener
{
    @Override
    public void remoteDeviceDiscoveryStarted(final Registry registry, final RemoteDevice device) {
    }
    
    @Override
    public void remoteDeviceDiscoveryFailed(final Registry registry, final RemoteDevice device, final Exception ex) {
    }
    
    @Override
    public void remoteDeviceAdded(final Registry registry, final RemoteDevice device) {
        this.deviceAdded(registry, device);
    }
    
    @Override
    public void remoteDeviceUpdated(final Registry registry, final RemoteDevice device) {
    }
    
    @Override
    public void remoteDeviceRemoved(final Registry registry, final RemoteDevice device) {
        this.deviceRemoved(registry, device);
    }
    
    @Override
    public void localDeviceAdded(final Registry registry, final LocalDevice device) {
        this.deviceAdded(registry, device);
    }
    
    @Override
    public void localDeviceRemoved(final Registry registry, final LocalDevice device) {
        this.deviceRemoved(registry, device);
    }
    
    public void deviceAdded(final Registry registry, final Device device) {
    }
    
    public void deviceRemoved(final Registry registry, final Device device) {
    }
    
    @Override
    public void beforeShutdown(final Registry registry) {
    }
    
    @Override
    public void afterShutdown() {
    }
}
