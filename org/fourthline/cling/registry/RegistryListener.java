// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.registry;

import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;

public interface RegistryListener
{
    void remoteDeviceDiscoveryStarted(final Registry p0, final RemoteDevice p1);
    
    void remoteDeviceDiscoveryFailed(final Registry p0, final RemoteDevice p1, final Exception p2);
    
    void remoteDeviceAdded(final Registry p0, final RemoteDevice p1);
    
    void remoteDeviceUpdated(final Registry p0, final RemoteDevice p1);
    
    void remoteDeviceRemoved(final Registry p0, final RemoteDevice p1);
    
    void localDeviceAdded(final Registry p0, final LocalDevice p1);
    
    void localDeviceRemoved(final Registry p0, final LocalDevice p1);
    
    void beforeShutdown(final Registry p0);
    
    void afterShutdown();
}
