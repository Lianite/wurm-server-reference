// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.registry;

import org.fourthline.cling.model.gena.RemoteGENASubscription;
import org.fourthline.cling.model.gena.LocalGENASubscription;
import java.net.URI;
import org.fourthline.cling.model.resource.Resource;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.ServiceReference;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.RemoteDeviceIdentity;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.model.DiscoveryOptions;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import java.util.Collection;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.UpnpService;

public interface Registry
{
    UpnpService getUpnpService();
    
    UpnpServiceConfiguration getConfiguration();
    
    ProtocolFactory getProtocolFactory();
    
    void shutdown();
    
    void pause();
    
    void resume();
    
    boolean isPaused();
    
    void addListener(final RegistryListener p0);
    
    void removeListener(final RegistryListener p0);
    
    Collection<RegistryListener> getListeners();
    
    boolean notifyDiscoveryStart(final RemoteDevice p0);
    
    void notifyDiscoveryFailure(final RemoteDevice p0, final Exception p1);
    
    void addDevice(final LocalDevice p0) throws RegistrationException;
    
    void addDevice(final LocalDevice p0, final DiscoveryOptions p1) throws RegistrationException;
    
    void setDiscoveryOptions(final UDN p0, final DiscoveryOptions p1);
    
    DiscoveryOptions getDiscoveryOptions(final UDN p0);
    
    void addDevice(final RemoteDevice p0) throws RegistrationException;
    
    boolean update(final RemoteDeviceIdentity p0);
    
    boolean removeDevice(final LocalDevice p0);
    
    boolean removeDevice(final RemoteDevice p0);
    
    boolean removeDevice(final UDN p0);
    
    void removeAllLocalDevices();
    
    void removeAllRemoteDevices();
    
    Device getDevice(final UDN p0, final boolean p1);
    
    LocalDevice getLocalDevice(final UDN p0, final boolean p1);
    
    RemoteDevice getRemoteDevice(final UDN p0, final boolean p1);
    
    Collection<LocalDevice> getLocalDevices();
    
    Collection<RemoteDevice> getRemoteDevices();
    
    Collection<Device> getDevices();
    
    Collection<Device> getDevices(final DeviceType p0);
    
    Collection<Device> getDevices(final ServiceType p0);
    
    Service getService(final ServiceReference p0);
    
    void addResource(final Resource p0);
    
    void addResource(final Resource p0, final int p1);
    
    boolean removeResource(final Resource p0);
    
    Resource getResource(final URI p0) throws IllegalArgumentException;
    
     <T extends Resource> T getResource(final Class<T> p0, final URI p1) throws IllegalArgumentException;
    
    Collection<Resource> getResources();
    
     <T extends Resource> Collection<T> getResources(final Class<T> p0);
    
    void addLocalSubscription(final LocalGENASubscription p0);
    
    LocalGENASubscription getLocalSubscription(final String p0);
    
    boolean updateLocalSubscription(final LocalGENASubscription p0);
    
    boolean removeLocalSubscription(final LocalGENASubscription p0);
    
    void addRemoteSubscription(final RemoteGENASubscription p0);
    
    RemoteGENASubscription getRemoteSubscription(final String p0);
    
    void updateRemoteSubscription(final RemoteGENASubscription p0);
    
    void removeRemoteSubscription(final RemoteGENASubscription p0);
    
    void registerPendingRemoteSubscription(final RemoteGENASubscription p0);
    
    void unregisterPendingRemoteSubscription(final RemoteGENASubscription p0);
    
    RemoteGENASubscription getWaitRemoteSubscription(final String p0);
    
    void advertiseLocalDevices();
}
