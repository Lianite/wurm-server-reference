// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.registry;

import org.fourthline.cling.model.meta.Service;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.meta.LocalDevice;
import java.util.Iterator;
import org.fourthline.cling.model.resource.Resource;
import java.util.logging.Level;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.RemoteDeviceIdentity;
import java.util.logging.Logger;
import org.fourthline.cling.model.gena.RemoteGENASubscription;
import org.fourthline.cling.model.meta.RemoteDevice;

class RemoteItems extends RegistryItems<RemoteDevice, RemoteGENASubscription>
{
    private static Logger log;
    
    RemoteItems(final RegistryImpl registry) {
        super(registry);
    }
    
    @Override
    void add(final RemoteDevice device) {
        if (this.update(((Device<RemoteDeviceIdentity, D, S>)device).getIdentity())) {
            RemoteItems.log.fine("Ignoring addition, device already registered: " + device);
            return;
        }
        final Resource[] resources2;
        final Resource[] resources = resources2 = this.getResources(device);
        for (final Resource deviceResource : resources2) {
            RemoteItems.log.fine("Validating remote device resource; " + deviceResource);
            if (this.registry.getResource(deviceResource.getPathQuery()) != null) {
                throw new RegistrationException("URI namespace conflict with already registered resource: " + deviceResource);
            }
        }
        for (final Resource validatedResource : resources) {
            this.registry.addResource(validatedResource);
            RemoteItems.log.fine("Added remote device resource: " + validatedResource);
        }
        final RegistryItem item = new RegistryItem((K)((Device<RemoteDeviceIdentity, D, S>)device).getIdentity().getUdn(), (I)device, (this.registry.getConfiguration().getRemoteDeviceMaxAgeSeconds() != null) ? this.registry.getConfiguration().getRemoteDeviceMaxAgeSeconds() : ((Device<RemoteDeviceIdentity, D, S>)device).getIdentity().getMaxAgeSeconds());
        RemoteItems.log.fine("Adding hydrated remote device to registry with " + item.getExpirationDetails().getMaxAgeSeconds() + " seconds expiration: " + device);
        ((RegistryItems<RemoteDevice, S>)this).getDeviceItems().add(item);
        if (RemoteItems.log.isLoggable(Level.FINEST)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("\n");
            sb.append("-------------------------- START Registry Namespace -----------------------------------\n");
            for (final Resource resource : this.registry.getResources()) {
                sb.append(resource).append("\n");
            }
            sb.append("-------------------------- END Registry Namespace -----------------------------------");
            RemoteItems.log.finest(sb.toString());
        }
        RemoteItems.log.fine("Completely hydrated remote device graph available, calling listeners: " + device);
        for (final RegistryListener listener : this.registry.getListeners()) {
            this.registry.getConfiguration().getRegistryListenerExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    listener.remoteDeviceAdded(RemoteItems.this.registry, device);
                }
            });
        }
    }
    
    boolean update(final RemoteDeviceIdentity rdIdentity) {
        for (final LocalDevice localDevice : this.registry.getLocalDevices()) {
            if (localDevice.findDevice(rdIdentity.getUdn()) != null) {
                RemoteItems.log.fine("Ignoring update, a local device graph contains UDN");
                return true;
            }
        }
        RemoteDevice registeredRemoteDevice = ((RegistryItems<RemoteDevice, S>)this).get(rdIdentity.getUdn(), false);
        if (registeredRemoteDevice != null) {
            if (!registeredRemoteDevice.isRoot()) {
                RemoteItems.log.fine("Updating root device of embedded: " + registeredRemoteDevice);
                registeredRemoteDevice = registeredRemoteDevice.getRoot();
            }
            final RegistryItem<UDN, RemoteDevice> item = new RegistryItem<UDN, RemoteDevice>(((Device<RemoteDeviceIdentity, D, S>)registeredRemoteDevice).getIdentity().getUdn(), registeredRemoteDevice, (this.registry.getConfiguration().getRemoteDeviceMaxAgeSeconds() != null) ? this.registry.getConfiguration().getRemoteDeviceMaxAgeSeconds() : rdIdentity.getMaxAgeSeconds());
            RemoteItems.log.fine("Updating expiration of: " + registeredRemoteDevice);
            ((RegistryItems<RemoteDevice, S>)this).getDeviceItems().remove(item);
            ((RegistryItems<RemoteDevice, S>)this).getDeviceItems().add(item);
            RemoteItems.log.fine("Remote device updated, calling listeners: " + registeredRemoteDevice);
            for (final RegistryListener listener : this.registry.getListeners()) {
                this.registry.getConfiguration().getRegistryListenerExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        listener.remoteDeviceUpdated(RemoteItems.this.registry, item.getItem());
                    }
                });
            }
            return true;
        }
        return false;
    }
    
    @Override
    boolean remove(final RemoteDevice remoteDevice) {
        return this.remove(remoteDevice, false);
    }
    
    boolean remove(final RemoteDevice remoteDevice, final boolean shuttingDown) throws RegistrationException {
        final RemoteDevice registeredDevice = ((RegistryItems<RemoteDevice, S>)this).get(((Device<RemoteDeviceIdentity, D, S>)remoteDevice).getIdentity().getUdn(), true);
        if (registeredDevice != null) {
            RemoteItems.log.fine("Removing remote device from registry: " + remoteDevice);
            for (final Resource deviceResource : this.getResources(registeredDevice)) {
                if (this.registry.removeResource(deviceResource)) {
                    RemoteItems.log.fine("Unregistered resource: " + deviceResource);
                }
            }
            final Iterator<RegistryItem<String, RemoteGENASubscription>> it = ((RegistryItems<D, RemoteGENASubscription>)this).getSubscriptionItems().iterator();
            while (it.hasNext()) {
                final RegistryItem<String, RemoteGENASubscription> outgoingSubscription = it.next();
                final UDN subscriptionForUDN = ((Device<RemoteDeviceIdentity, D, S>)((Service<RemoteDevice, S>)outgoingSubscription.getItem().getService()).getDevice()).getIdentity().getUdn();
                if (subscriptionForUDN.equals(((Device<RemoteDeviceIdentity, D, S>)registeredDevice).getIdentity().getUdn())) {
                    RemoteItems.log.fine("Removing outgoing subscription: " + outgoingSubscription.getKey());
                    it.remove();
                    if (shuttingDown) {
                        continue;
                    }
                    this.registry.getConfiguration().getRegistryListenerExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            outgoingSubscription.getItem().end(CancelReason.DEVICE_WAS_REMOVED, null);
                        }
                    });
                }
            }
            if (!shuttingDown) {
                for (final RegistryListener listener : this.registry.getListeners()) {
                    this.registry.getConfiguration().getRegistryListenerExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            listener.remoteDeviceRemoved(RemoteItems.this.registry, registeredDevice);
                        }
                    });
                }
            }
            ((RegistryItems<RemoteDevice, S>)this).getDeviceItems().remove(new RegistryItem(((Device<RemoteDeviceIdentity, D, S>)registeredDevice).getIdentity().getUdn()));
            return true;
        }
        return false;
    }
    
    @Override
    void removeAll() {
        this.removeAll(false);
    }
    
    void removeAll(final boolean shuttingDown) {
        final RemoteDevice[] array;
        final RemoteDevice[] allDevices = array = ((RegistryItems<RemoteDevice, S>)this).get().toArray(new RemoteDevice[((RegistryItems<RemoteDevice, S>)this).get().size()]);
        for (final RemoteDevice device : array) {
            this.remove(device, shuttingDown);
        }
    }
    
    void start() {
    }
    
    @Override
    void maintain() {
        if (((RegistryItems<RemoteDevice, S>)this).getDeviceItems().isEmpty()) {
            return;
        }
        final Map<UDN, RemoteDevice> expiredRemoteDevices = new HashMap<UDN, RemoteDevice>();
        for (final RegistryItem<UDN, RemoteDevice> remoteItem : ((RegistryItems<RemoteDevice, S>)this).getDeviceItems()) {
            if (RemoteItems.log.isLoggable(Level.FINEST)) {
                RemoteItems.log.finest("Device '" + remoteItem.getItem() + "' expires in seconds: " + remoteItem.getExpirationDetails().getSecondsUntilExpiration());
            }
            if (remoteItem.getExpirationDetails().hasExpired(false)) {
                expiredRemoteDevices.put(remoteItem.getKey(), remoteItem.getItem());
            }
        }
        for (final RemoteDevice remoteDevice : expiredRemoteDevices.values()) {
            if (RemoteItems.log.isLoggable(Level.FINE)) {
                RemoteItems.log.fine("Removing expired: " + remoteDevice);
            }
            this.remove(remoteDevice);
        }
        final Set<RemoteGENASubscription> expiredOutgoingSubscriptions = new HashSet<RemoteGENASubscription>();
        for (final RegistryItem<String, RemoteGENASubscription> item : ((RegistryItems<D, RemoteGENASubscription>)this).getSubscriptionItems()) {
            if (item.getExpirationDetails().hasExpired(true)) {
                expiredOutgoingSubscriptions.add(item.getItem());
            }
        }
        for (final RemoteGENASubscription subscription : expiredOutgoingSubscriptions) {
            if (RemoteItems.log.isLoggable(Level.FINEST)) {
                RemoteItems.log.fine("Renewing outgoing subscription: " + subscription);
            }
            this.renewOutgoingSubscription(subscription);
        }
    }
    
    public void resume() {
        RemoteItems.log.fine("Updating remote device expiration timestamps on resume");
        final List<RemoteDeviceIdentity> toUpdate = new ArrayList<RemoteDeviceIdentity>();
        for (final RegistryItem<UDN, RemoteDevice> remoteItem : ((RegistryItems<RemoteDevice, S>)this).getDeviceItems()) {
            toUpdate.add(((Device<RemoteDeviceIdentity, D, S>)remoteItem.getItem()).getIdentity());
        }
        for (final RemoteDeviceIdentity identity : toUpdate) {
            this.update(identity);
        }
    }
    
    @Override
    void shutdown() {
        RemoteItems.log.fine("Cancelling all outgoing subscriptions to remote devices during shutdown");
        final List<RemoteGENASubscription> remoteSubscriptions = new ArrayList<RemoteGENASubscription>();
        for (final RegistryItem<String, RemoteGENASubscription> item : ((RegistryItems<D, RemoteGENASubscription>)this).getSubscriptionItems()) {
            remoteSubscriptions.add(item.getItem());
        }
        for (final RemoteGENASubscription remoteSubscription : remoteSubscriptions) {
            this.registry.getProtocolFactory().createSendingUnsubscribe(remoteSubscription).run();
        }
        RemoteItems.log.fine("Removing all remote devices from registry during shutdown");
        this.removeAll(true);
    }
    
    protected void renewOutgoingSubscription(final RemoteGENASubscription subscription) {
        this.registry.executeAsyncProtocol(this.registry.getProtocolFactory().createSendingRenewal(subscription));
    }
    
    static {
        RemoteItems.log = Logger.getLogger(Registry.class.getName());
    }
}
