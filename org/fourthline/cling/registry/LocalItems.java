// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.registry;

import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.protocol.SendingAsync;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.meta.LocalService;
import java.util.Set;
import java.util.Collections;
import java.util.HashSet;
import java.util.Collection;
import java.util.Iterator;
import org.fourthline.cling.model.resource.Resource;
import org.fourthline.cling.model.meta.Device;
import java.util.HashMap;
import java.util.Random;
import org.fourthline.cling.model.DiscoveryOptions;
import org.fourthline.cling.model.types.UDN;
import java.util.Map;
import java.util.logging.Logger;
import org.fourthline.cling.model.gena.LocalGENASubscription;
import org.fourthline.cling.model.meta.LocalDevice;

class LocalItems extends RegistryItems<LocalDevice, LocalGENASubscription>
{
    private static Logger log;
    protected Map<UDN, DiscoveryOptions> discoveryOptions;
    protected long lastAliveIntervalTimestamp;
    protected Random randomGenerator;
    
    LocalItems(final RegistryImpl registry) {
        super(registry);
        this.discoveryOptions = new HashMap<UDN, DiscoveryOptions>();
        this.lastAliveIntervalTimestamp = 0L;
        this.randomGenerator = new Random();
    }
    
    protected void setDiscoveryOptions(final UDN udn, final DiscoveryOptions options) {
        if (options != null) {
            this.discoveryOptions.put(udn, options);
        }
        else {
            this.discoveryOptions.remove(udn);
        }
    }
    
    protected DiscoveryOptions getDiscoveryOptions(final UDN udn) {
        return this.discoveryOptions.get(udn);
    }
    
    protected boolean isAdvertised(final UDN udn) {
        return this.getDiscoveryOptions(udn) == null || this.getDiscoveryOptions(udn).isAdvertised();
    }
    
    protected boolean isByeByeBeforeFirstAlive(final UDN udn) {
        return this.getDiscoveryOptions(udn) != null && this.getDiscoveryOptions(udn).isByeByeBeforeFirstAlive();
    }
    
    @Override
    void add(final LocalDevice localDevice) throws RegistrationException {
        this.add(localDevice, null);
    }
    
    void add(final LocalDevice localDevice, final DiscoveryOptions options) throws RegistrationException {
        this.setDiscoveryOptions(((Device<DeviceIdentity, D, S>)localDevice).getIdentity().getUdn(), options);
        if (this.registry.getDevice(((Device<DeviceIdentity, D, S>)localDevice).getIdentity().getUdn(), false) != null) {
            LocalItems.log.fine("Ignoring addition, device already registered: " + localDevice);
            return;
        }
        LocalItems.log.fine("Adding local device to registry: " + localDevice);
        for (final Resource deviceResource : this.getResources(localDevice)) {
            if (this.registry.getResource(deviceResource.getPathQuery()) != null) {
                throw new RegistrationException("URI namespace conflict with already registered resource: " + deviceResource);
            }
            this.registry.addResource(deviceResource);
            LocalItems.log.fine("Registered resource: " + deviceResource);
        }
        LocalItems.log.fine("Adding item to registry with expiration in seconds: " + ((Device<DeviceIdentity, D, S>)localDevice).getIdentity().getMaxAgeSeconds());
        final RegistryItem<UDN, LocalDevice> localItem = new RegistryItem<UDN, LocalDevice>(((Device<DeviceIdentity, D, S>)localDevice).getIdentity().getUdn(), localDevice, ((Device<DeviceIdentity, D, S>)localDevice).getIdentity().getMaxAgeSeconds());
        ((RegistryItems<LocalDevice, S>)this).getDeviceItems().add(localItem);
        LocalItems.log.fine("Registered local device: " + localItem);
        if (this.isByeByeBeforeFirstAlive(localItem.getKey())) {
            this.advertiseByebye(localDevice, true);
        }
        if (this.isAdvertised(localItem.getKey())) {
            this.advertiseAlive(localDevice);
        }
        for (final RegistryListener listener : this.registry.getListeners()) {
            this.registry.getConfiguration().getRegistryListenerExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    listener.localDeviceAdded(LocalItems.this.registry, localDevice);
                }
            });
        }
    }
    
    @Override
    Collection<LocalDevice> get() {
        final Set<LocalDevice> c = new HashSet<LocalDevice>();
        for (final RegistryItem<UDN, LocalDevice> item : ((RegistryItems<LocalDevice, S>)this).getDeviceItems()) {
            c.add(item.getItem());
        }
        return Collections.unmodifiableCollection((Collection<? extends LocalDevice>)c);
    }
    
    @Override
    boolean remove(final LocalDevice localDevice) throws RegistrationException {
        return this.remove(localDevice, false);
    }
    
    boolean remove(final LocalDevice localDevice, final boolean shuttingDown) throws RegistrationException {
        final LocalDevice registeredDevice = ((RegistryItems<LocalDevice, S>)this).get(((Device<DeviceIdentity, D, S>)localDevice).getIdentity().getUdn(), true);
        if (registeredDevice != null) {
            LocalItems.log.fine("Removing local device from registry: " + localDevice);
            this.setDiscoveryOptions(((Device<DeviceIdentity, D, S>)localDevice).getIdentity().getUdn(), null);
            ((RegistryItems<LocalDevice, S>)this).getDeviceItems().remove(new RegistryItem(((Device<DeviceIdentity, D, S>)localDevice).getIdentity().getUdn()));
            for (final Resource deviceResource : this.getResources(localDevice)) {
                if (this.registry.removeResource(deviceResource)) {
                    LocalItems.log.fine("Unregistered resource: " + deviceResource);
                }
            }
            final Iterator<RegistryItem<String, LocalGENASubscription>> it = ((RegistryItems<D, LocalGENASubscription>)this).getSubscriptionItems().iterator();
            while (it.hasNext()) {
                final RegistryItem<String, LocalGENASubscription> incomingSubscription = it.next();
                final UDN subscriptionForUDN = incomingSubscription.getItem().getService().getDevice().getIdentity().getUdn();
                if (subscriptionForUDN.equals(((Device<DeviceIdentity, D, S>)registeredDevice).getIdentity().getUdn())) {
                    LocalItems.log.fine("Removing incoming subscription: " + incomingSubscription.getKey());
                    it.remove();
                    if (shuttingDown) {
                        continue;
                    }
                    this.registry.getConfiguration().getRegistryListenerExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            incomingSubscription.getItem().end(CancelReason.DEVICE_WAS_REMOVED);
                        }
                    });
                }
            }
            if (this.isAdvertised(((Device<DeviceIdentity, D, S>)localDevice).getIdentity().getUdn())) {
                this.advertiseByebye(localDevice, !shuttingDown);
            }
            if (!shuttingDown) {
                for (final RegistryListener listener : this.registry.getListeners()) {
                    this.registry.getConfiguration().getRegistryListenerExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            listener.localDeviceRemoved(LocalItems.this.registry, localDevice);
                        }
                    });
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    void removeAll() {
        this.removeAll(false);
    }
    
    void removeAll(final boolean shuttingDown) {
        final LocalDevice[] array;
        final LocalDevice[] allDevices = array = this.get().toArray(new LocalDevice[this.get().size()]);
        for (final LocalDevice device : array) {
            this.remove(device, shuttingDown);
        }
    }
    
    public void advertiseLocalDevices() {
        for (final RegistryItem<UDN, LocalDevice> localItem : this.deviceItems) {
            if (this.isAdvertised(localItem.getKey())) {
                this.advertiseAlive(localItem.getItem());
            }
        }
    }
    
    @Override
    void maintain() {
        if (((RegistryItems<LocalDevice, S>)this).getDeviceItems().isEmpty()) {
            return;
        }
        final Set<RegistryItem<UDN, LocalDevice>> expiredLocalItems = new HashSet<RegistryItem<UDN, LocalDevice>>();
        final int aliveIntervalMillis = this.registry.getConfiguration().getAliveIntervalMillis();
        if (aliveIntervalMillis > 0) {
            final long now = System.currentTimeMillis();
            if (now - this.lastAliveIntervalTimestamp > aliveIntervalMillis) {
                this.lastAliveIntervalTimestamp = now;
                for (final RegistryItem<UDN, LocalDevice> localItem : ((RegistryItems<LocalDevice, S>)this).getDeviceItems()) {
                    if (this.isAdvertised(localItem.getKey())) {
                        LocalItems.log.finer("Flooding advertisement of local item: " + localItem);
                        expiredLocalItems.add(localItem);
                    }
                }
            }
        }
        else {
            this.lastAliveIntervalTimestamp = 0L;
            for (final RegistryItem<UDN, LocalDevice> localItem2 : ((RegistryItems<LocalDevice, S>)this).getDeviceItems()) {
                if (this.isAdvertised(localItem2.getKey()) && localItem2.getExpirationDetails().hasExpired(true)) {
                    LocalItems.log.finer("Local item has expired: " + localItem2);
                    expiredLocalItems.add(localItem2);
                }
            }
        }
        for (final RegistryItem<UDN, LocalDevice> expiredLocalItem : expiredLocalItems) {
            LocalItems.log.fine("Refreshing local device advertisement: " + expiredLocalItem.getItem());
            this.advertiseAlive(expiredLocalItem.getItem());
            expiredLocalItem.getExpirationDetails().stampLastRefresh();
        }
        final Set<RegistryItem<String, LocalGENASubscription>> expiredIncomingSubscriptions = new HashSet<RegistryItem<String, LocalGENASubscription>>();
        for (final RegistryItem<String, LocalGENASubscription> item : ((RegistryItems<D, LocalGENASubscription>)this).getSubscriptionItems()) {
            if (item.getExpirationDetails().hasExpired(false)) {
                expiredIncomingSubscriptions.add(item);
            }
        }
        for (final RegistryItem<String, LocalGENASubscription> subscription : expiredIncomingSubscriptions) {
            LocalItems.log.fine("Removing expired: " + subscription);
            ((RegistryItems<D, LocalGENASubscription>)this).removeSubscription(subscription.getItem());
            subscription.getItem().end(CancelReason.EXPIRED);
        }
    }
    
    @Override
    void shutdown() {
        LocalItems.log.fine("Clearing all registered subscriptions to local devices during shutdown");
        ((RegistryItems<D, LocalGENASubscription>)this).getSubscriptionItems().clear();
        LocalItems.log.fine("Removing all local devices from registry during shutdown");
        this.removeAll(true);
    }
    
    protected void advertiseAlive(final LocalDevice localDevice) {
        this.registry.executeAsyncProtocol(new Runnable() {
            @Override
            public void run() {
                try {
                    LocalItems.log.finer("Sleeping some milliseconds to avoid flooding the network with ALIVE msgs");
                    Thread.sleep(LocalItems.this.randomGenerator.nextInt(100));
                }
                catch (InterruptedException ex) {
                    LocalItems.log.severe("Background execution interrupted: " + ex.getMessage());
                }
                LocalItems.this.registry.getProtocolFactory().createSendingNotificationAlive(localDevice).run();
            }
        });
    }
    
    protected void advertiseByebye(final LocalDevice localDevice, final boolean asynchronous) {
        final SendingAsync prot = this.registry.getProtocolFactory().createSendingNotificationByebye(localDevice);
        if (asynchronous) {
            this.registry.executeAsyncProtocol(prot);
        }
        else {
            prot.run();
        }
    }
    
    static {
        LocalItems.log = Logger.getLogger(Registry.class.getName());
    }
}
