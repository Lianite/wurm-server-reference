// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.registry;

import java.util.logging.Level;
import org.fourthline.cling.model.gena.LocalGENASubscription;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.ServiceReference;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.model.DiscoveryOptions;
import org.fourthline.cling.model.meta.LocalDevice;
import java.util.Iterator;
import org.fourthline.cling.model.meta.RemoteDeviceIdentity;
import org.fourthline.cling.model.meta.RemoteDevice;
import java.util.Collections;
import java.util.Collection;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.UpnpServiceConfiguration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.fourthline.cling.model.resource.Resource;
import java.net.URI;
import org.fourthline.cling.model.gena.RemoteGENASubscription;
import java.util.Set;
import org.fourthline.cling.UpnpService;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RegistryImpl implements Registry
{
    private static Logger log;
    protected UpnpService upnpService;
    protected RegistryMaintainer registryMaintainer;
    protected final Set<RemoteGENASubscription> pendingSubscriptionsLock;
    protected final Set<RegistryListener> registryListeners;
    protected final Set<RegistryItem<URI, Resource>> resourceItems;
    protected final List<Runnable> pendingExecutions;
    protected final RemoteItems remoteItems;
    protected final LocalItems localItems;
    
    public RegistryImpl() {
        this.pendingSubscriptionsLock = new HashSet<RemoteGENASubscription>();
        this.registryListeners = new HashSet<RegistryListener>();
        this.resourceItems = new HashSet<RegistryItem<URI, Resource>>();
        this.pendingExecutions = new ArrayList<Runnable>();
        this.remoteItems = new RemoteItems(this);
        this.localItems = new LocalItems(this);
    }
    
    public RegistryImpl(final UpnpService upnpService) {
        this.pendingSubscriptionsLock = new HashSet<RemoteGENASubscription>();
        this.registryListeners = new HashSet<RegistryListener>();
        this.resourceItems = new HashSet<RegistryItem<URI, Resource>>();
        this.pendingExecutions = new ArrayList<Runnable>();
        this.remoteItems = new RemoteItems(this);
        this.localItems = new LocalItems(this);
        RegistryImpl.log.fine("Creating Registry: " + this.getClass().getName());
        this.upnpService = upnpService;
        RegistryImpl.log.fine("Starting registry background maintenance...");
        this.registryMaintainer = this.createRegistryMaintainer();
        if (this.registryMaintainer != null) {
            this.getConfiguration().getRegistryMaintainerExecutor().execute(this.registryMaintainer);
        }
    }
    
    @Override
    public UpnpService getUpnpService() {
        return this.upnpService;
    }
    
    @Override
    public UpnpServiceConfiguration getConfiguration() {
        return this.getUpnpService().getConfiguration();
    }
    
    @Override
    public ProtocolFactory getProtocolFactory() {
        return this.getUpnpService().getProtocolFactory();
    }
    
    protected RegistryMaintainer createRegistryMaintainer() {
        return new RegistryMaintainer(this, this.getConfiguration().getRegistryMaintenanceIntervalMillis());
    }
    
    @Override
    public synchronized void addListener(final RegistryListener listener) {
        this.registryListeners.add(listener);
    }
    
    @Override
    public synchronized void removeListener(final RegistryListener listener) {
        this.registryListeners.remove(listener);
    }
    
    @Override
    public synchronized Collection<RegistryListener> getListeners() {
        return Collections.unmodifiableCollection((Collection<? extends RegistryListener>)this.registryListeners);
    }
    
    @Override
    public synchronized boolean notifyDiscoveryStart(final RemoteDevice device) {
        if (this.getUpnpService().getRegistry().getRemoteDevice(((Device<RemoteDeviceIdentity, D, S>)device).getIdentity().getUdn(), true) != null) {
            RegistryImpl.log.finer("Not notifying listeners, already registered: " + device);
            return false;
        }
        for (final RegistryListener listener : this.getListeners()) {
            this.getConfiguration().getRegistryListenerExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    listener.remoteDeviceDiscoveryStarted(RegistryImpl.this, device);
                }
            });
        }
        return true;
    }
    
    @Override
    public synchronized void notifyDiscoveryFailure(final RemoteDevice device, final Exception ex) {
        for (final RegistryListener listener : this.getListeners()) {
            this.getConfiguration().getRegistryListenerExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    listener.remoteDeviceDiscoveryFailed(RegistryImpl.this, device, ex);
                }
            });
        }
    }
    
    @Override
    public synchronized void addDevice(final LocalDevice localDevice) {
        this.localItems.add(localDevice);
    }
    
    @Override
    public synchronized void addDevice(final LocalDevice localDevice, final DiscoveryOptions options) {
        this.localItems.add(localDevice, options);
    }
    
    @Override
    public synchronized void setDiscoveryOptions(final UDN udn, final DiscoveryOptions options) {
        this.localItems.setDiscoveryOptions(udn, options);
    }
    
    @Override
    public synchronized DiscoveryOptions getDiscoveryOptions(final UDN udn) {
        return this.localItems.getDiscoveryOptions(udn);
    }
    
    @Override
    public synchronized void addDevice(final RemoteDevice remoteDevice) {
        this.remoteItems.add(remoteDevice);
    }
    
    @Override
    public synchronized boolean update(final RemoteDeviceIdentity rdIdentity) {
        return this.remoteItems.update(rdIdentity);
    }
    
    @Override
    public synchronized boolean removeDevice(final LocalDevice localDevice) {
        return this.localItems.remove(localDevice);
    }
    
    @Override
    public synchronized boolean removeDevice(final RemoteDevice remoteDevice) {
        return this.remoteItems.remove(remoteDevice);
    }
    
    @Override
    public synchronized void removeAllLocalDevices() {
        this.localItems.removeAll();
    }
    
    @Override
    public synchronized void removeAllRemoteDevices() {
        this.remoteItems.removeAll();
    }
    
    @Override
    public synchronized boolean removeDevice(final UDN udn) {
        final Device device = this.getDevice(udn, true);
        if (device != null && device instanceof LocalDevice) {
            return this.removeDevice((LocalDevice)device);
        }
        return device != null && device instanceof RemoteDevice && this.removeDevice((RemoteDevice)device);
    }
    
    @Override
    public synchronized Device getDevice(final UDN udn, final boolean rootOnly) {
        Device device;
        if ((device = this.localItems.get(udn, rootOnly)) != null) {
            return device;
        }
        if ((device = this.remoteItems.get(udn, rootOnly)) != null) {
            return device;
        }
        return null;
    }
    
    @Override
    public synchronized LocalDevice getLocalDevice(final UDN udn, final boolean rootOnly) {
        return ((RegistryItems<LocalDevice, S>)this.localItems).get(udn, rootOnly);
    }
    
    @Override
    public synchronized RemoteDevice getRemoteDevice(final UDN udn, final boolean rootOnly) {
        return ((RegistryItems<RemoteDevice, S>)this.remoteItems).get(udn, rootOnly);
    }
    
    @Override
    public synchronized Collection<LocalDevice> getLocalDevices() {
        return Collections.unmodifiableCollection((Collection<? extends LocalDevice>)this.localItems.get());
    }
    
    @Override
    public synchronized Collection<RemoteDevice> getRemoteDevices() {
        return Collections.unmodifiableCollection(((RegistryItems<? extends RemoteDevice, S>)this.remoteItems).get());
    }
    
    @Override
    public synchronized Collection<Device> getDevices() {
        final Set all = new HashSet();
        all.addAll(this.localItems.get());
        all.addAll(((RegistryItems<? extends E, S>)this.remoteItems).get());
        return (Collection<Device>)Collections.unmodifiableCollection((Collection<? extends Device>)all);
    }
    
    @Override
    public synchronized Collection<Device> getDevices(final DeviceType deviceType) {
        final Collection<Device> devices = new HashSet<Device>();
        devices.addAll(((RegistryItems<? extends Device, S>)this.localItems).get(deviceType));
        devices.addAll(((RegistryItems<? extends Device, S>)this.remoteItems).get(deviceType));
        return (Collection<Device>)Collections.unmodifiableCollection((Collection<? extends Device>)devices);
    }
    
    @Override
    public synchronized Collection<Device> getDevices(final ServiceType serviceType) {
        final Collection<Device> devices = new HashSet<Device>();
        devices.addAll(((RegistryItems<? extends Device, S>)this.localItems).get(serviceType));
        devices.addAll(((RegistryItems<? extends Device, S>)this.remoteItems).get(serviceType));
        return (Collection<Device>)Collections.unmodifiableCollection((Collection<? extends Device>)devices);
    }
    
    @Override
    public synchronized Service getService(final ServiceReference serviceReference) {
        final Device device;
        if ((device = this.getDevice(serviceReference.getUdn(), false)) != null) {
            return device.findService(serviceReference.getServiceId());
        }
        return null;
    }
    
    @Override
    public synchronized Resource getResource(final URI pathQuery) throws IllegalArgumentException {
        if (pathQuery.isAbsolute()) {
            throw new IllegalArgumentException("Resource URI can not be absolute, only path and query:" + pathQuery);
        }
        for (final RegistryItem<URI, Resource> resourceItem : this.resourceItems) {
            final Resource resource = resourceItem.getItem();
            if (resource.matches(pathQuery)) {
                return resource;
            }
        }
        if (pathQuery.getPath().endsWith("/")) {
            final URI pathQueryWithoutSlash = URI.create(pathQuery.toString().substring(0, pathQuery.toString().length() - 1));
            for (final RegistryItem<URI, Resource> resourceItem2 : this.resourceItems) {
                final Resource resource2 = resourceItem2.getItem();
                if (resource2.matches(pathQueryWithoutSlash)) {
                    return resource2;
                }
            }
        }
        return null;
    }
    
    @Override
    public synchronized <T extends Resource> T getResource(final Class<T> resourceType, final URI pathQuery) throws IllegalArgumentException {
        final Resource resource = this.getResource(pathQuery);
        if (resource != null && resourceType.isAssignableFrom(resource.getClass())) {
            return (T)resource;
        }
        return null;
    }
    
    @Override
    public synchronized Collection<Resource> getResources() {
        final Collection<Resource> s = new HashSet<Resource>();
        for (final RegistryItem<URI, Resource> resourceItem : this.resourceItems) {
            s.add(resourceItem.getItem());
        }
        return s;
    }
    
    @Override
    public synchronized <T extends Resource> Collection<T> getResources(final Class<T> resourceType) {
        final Collection<T> s = new HashSet<T>();
        for (final RegistryItem<URI, Resource> resourceItem : this.resourceItems) {
            if (resourceType.isAssignableFrom(resourceItem.getItem().getClass())) {
                s.add((T)resourceItem.getItem());
            }
        }
        return s;
    }
    
    @Override
    public synchronized void addResource(final Resource resource) {
        this.addResource(resource, 0);
    }
    
    @Override
    public synchronized void addResource(final Resource resource, final int maxAgeSeconds) {
        final RegistryItem resourceItem = new RegistryItem((K)resource.getPathQuery(), (I)resource, maxAgeSeconds);
        this.resourceItems.remove(resourceItem);
        this.resourceItems.add(resourceItem);
    }
    
    @Override
    public synchronized boolean removeResource(final Resource resource) {
        return this.resourceItems.remove(new RegistryItem(resource.getPathQuery()));
    }
    
    @Override
    public synchronized void addLocalSubscription(final LocalGENASubscription subscription) {
        ((RegistryItems<D, LocalGENASubscription>)this.localItems).addSubscription(subscription);
    }
    
    @Override
    public synchronized LocalGENASubscription getLocalSubscription(final String subscriptionId) {
        return ((RegistryItems<D, LocalGENASubscription>)this.localItems).getSubscription(subscriptionId);
    }
    
    @Override
    public synchronized boolean updateLocalSubscription(final LocalGENASubscription subscription) {
        return ((RegistryItems<D, LocalGENASubscription>)this.localItems).updateSubscription(subscription);
    }
    
    @Override
    public synchronized boolean removeLocalSubscription(final LocalGENASubscription subscription) {
        return ((RegistryItems<D, LocalGENASubscription>)this.localItems).removeSubscription(subscription);
    }
    
    @Override
    public synchronized void addRemoteSubscription(final RemoteGENASubscription subscription) {
        ((RegistryItems<D, RemoteGENASubscription>)this.remoteItems).addSubscription(subscription);
    }
    
    @Override
    public synchronized RemoteGENASubscription getRemoteSubscription(final String subscriptionId) {
        return ((RegistryItems<D, RemoteGENASubscription>)this.remoteItems).getSubscription(subscriptionId);
    }
    
    @Override
    public synchronized void updateRemoteSubscription(final RemoteGENASubscription subscription) {
        ((RegistryItems<D, RemoteGENASubscription>)this.remoteItems).updateSubscription(subscription);
    }
    
    @Override
    public synchronized void removeRemoteSubscription(final RemoteGENASubscription subscription) {
        ((RegistryItems<D, RemoteGENASubscription>)this.remoteItems).removeSubscription(subscription);
    }
    
    @Override
    public synchronized void advertiseLocalDevices() {
        this.localItems.advertiseLocalDevices();
    }
    
    @Override
    public synchronized void shutdown() {
        RegistryImpl.log.fine("Shutting down registry...");
        if (this.registryMaintainer != null) {
            this.registryMaintainer.stop();
        }
        RegistryImpl.log.finest("Executing final pending operations on shutdown: " + this.pendingExecutions.size());
        this.runPendingExecutions(false);
        for (final RegistryListener listener : this.registryListeners) {
            listener.beforeShutdown(this);
        }
        final RegistryItem<URI, Resource>[] array;
        final RegistryItem<URI, Resource>[] resources = array = this.resourceItems.toArray(new RegistryItem[this.resourceItems.size()]);
        for (final RegistryItem<URI, Resource> resourceItem : array) {
            resourceItem.getItem().shutdown();
        }
        this.remoteItems.shutdown();
        this.localItems.shutdown();
        for (final RegistryListener listener2 : this.registryListeners) {
            listener2.afterShutdown();
        }
    }
    
    @Override
    public synchronized void pause() {
        if (this.registryMaintainer != null) {
            RegistryImpl.log.fine("Pausing registry maintenance");
            this.runPendingExecutions(true);
            this.registryMaintainer.stop();
            this.registryMaintainer = null;
        }
    }
    
    @Override
    public synchronized void resume() {
        if (this.registryMaintainer == null) {
            RegistryImpl.log.fine("Resuming registry maintenance");
            this.remoteItems.resume();
            this.registryMaintainer = this.createRegistryMaintainer();
            if (this.registryMaintainer != null) {
                this.getConfiguration().getRegistryMaintainerExecutor().execute(this.registryMaintainer);
            }
        }
    }
    
    @Override
    public synchronized boolean isPaused() {
        return this.registryMaintainer == null;
    }
    
    synchronized void maintain() {
        if (RegistryImpl.log.isLoggable(Level.FINEST)) {
            RegistryImpl.log.finest("Maintaining registry...");
        }
        final Iterator<RegistryItem<URI, Resource>> it = this.resourceItems.iterator();
        while (it.hasNext()) {
            final RegistryItem<URI, Resource> item = it.next();
            if (item.getExpirationDetails().hasExpired()) {
                if (RegistryImpl.log.isLoggable(Level.FINER)) {
                    RegistryImpl.log.finer("Removing expired resource: " + item);
                }
                it.remove();
            }
        }
        for (final RegistryItem<URI, Resource> resourceItem : this.resourceItems) {
            resourceItem.getItem().maintain(this.pendingExecutions, resourceItem.getExpirationDetails());
        }
        this.remoteItems.maintain();
        this.localItems.maintain();
        this.runPendingExecutions(true);
    }
    
    synchronized void executeAsyncProtocol(final Runnable runnable) {
        this.pendingExecutions.add(runnable);
    }
    
    synchronized void runPendingExecutions(final boolean async) {
        if (RegistryImpl.log.isLoggable(Level.FINEST)) {
            RegistryImpl.log.finest("Executing pending operations: " + this.pendingExecutions.size());
        }
        for (final Runnable pendingExecution : this.pendingExecutions) {
            if (async) {
                this.getConfiguration().getAsyncProtocolExecutor().execute(pendingExecution);
            }
            else {
                pendingExecution.run();
            }
        }
        if (this.pendingExecutions.size() > 0) {
            this.pendingExecutions.clear();
        }
    }
    
    public void printDebugLog() {
        if (RegistryImpl.log.isLoggable(Level.FINE)) {
            RegistryImpl.log.fine("====================================    REMOTE   ================================================");
            for (final RemoteDevice remoteDevice : ((RegistryItems<RemoteDevice, S>)this.remoteItems).get()) {
                RegistryImpl.log.fine(remoteDevice.toString());
            }
            RegistryImpl.log.fine("====================================    LOCAL    ================================================");
            for (final LocalDevice localDevice : this.localItems.get()) {
                RegistryImpl.log.fine(localDevice.toString());
            }
            RegistryImpl.log.fine("====================================  RESOURCES  ================================================");
            for (final RegistryItem<URI, Resource> resourceItem : this.resourceItems) {
                RegistryImpl.log.fine(resourceItem.toString());
            }
            RegistryImpl.log.fine("=================================================================================================");
        }
    }
    
    @Override
    public void registerPendingRemoteSubscription(final RemoteGENASubscription subscription) {
        synchronized (this.pendingSubscriptionsLock) {
            this.pendingSubscriptionsLock.add(subscription);
        }
    }
    
    @Override
    public void unregisterPendingRemoteSubscription(final RemoteGENASubscription subscription) {
        synchronized (this.pendingSubscriptionsLock) {
            if (this.pendingSubscriptionsLock.remove(subscription)) {
                this.pendingSubscriptionsLock.notifyAll();
            }
        }
    }
    
    @Override
    public RemoteGENASubscription getWaitRemoteSubscription(final String subscriptionId) {
        synchronized (this.pendingSubscriptionsLock) {
            RemoteGENASubscription subscription;
            for (subscription = this.getRemoteSubscription(subscriptionId); subscription == null && !this.pendingSubscriptionsLock.isEmpty(); subscription = this.getRemoteSubscription(subscriptionId)) {
                try {
                    RegistryImpl.log.finest("Subscription not found, waiting for pending subscription procedure to terminate.");
                    this.pendingSubscriptionsLock.wait();
                }
                catch (InterruptedException ex) {}
            }
            return subscription;
        }
    }
    
    static {
        RegistryImpl.log = Logger.getLogger(Registry.class.getName());
    }
}
