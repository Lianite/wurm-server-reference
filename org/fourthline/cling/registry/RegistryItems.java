// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.registry;

import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.resource.Resource;
import org.fourthline.cling.model.types.ServiceType;
import java.util.Arrays;
import java.util.Collection;
import org.fourthline.cling.model.types.DeviceType;
import java.util.Iterator;
import java.util.HashSet;
import org.fourthline.cling.model.types.UDN;
import java.util.Set;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.meta.Device;

abstract class RegistryItems<D extends Device, S extends GENASubscription>
{
    protected final RegistryImpl registry;
    protected final Set<RegistryItem<UDN, D>> deviceItems;
    protected final Set<RegistryItem<String, S>> subscriptionItems;
    
    RegistryItems(final RegistryImpl registry) {
        this.deviceItems = new HashSet<RegistryItem<UDN, D>>();
        this.subscriptionItems = new HashSet<RegistryItem<String, S>>();
        this.registry = registry;
    }
    
    Set<RegistryItem<UDN, D>> getDeviceItems() {
        return this.deviceItems;
    }
    
    Set<RegistryItem<String, S>> getSubscriptionItems() {
        return this.subscriptionItems;
    }
    
    abstract void add(final D p0);
    
    abstract boolean remove(final D p0);
    
    abstract void removeAll();
    
    abstract void maintain();
    
    abstract void shutdown();
    
    D get(final UDN udn, final boolean rootOnly) {
        for (final RegistryItem<UDN, D> item : this.deviceItems) {
            final D device = item.getItem();
            if (device.getIdentity().getUdn().equals(udn)) {
                return device;
            }
            if (rootOnly) {
                continue;
            }
            final D foundDevice = item.getItem().findDevice(udn);
            if (foundDevice != null) {
                return foundDevice;
            }
        }
        return null;
    }
    
    Collection<D> get(final DeviceType deviceType) {
        final Collection<D> devices = new HashSet<D>();
        for (final RegistryItem<UDN, D> item : this.deviceItems) {
            final D[] d = item.getItem().findDevices(deviceType);
            if (d != null) {
                devices.addAll((Collection<? extends D>)Arrays.asList(d));
            }
        }
        return devices;
    }
    
    Collection<D> get(final ServiceType serviceType) {
        final Collection<D> devices = new HashSet<D>();
        for (final RegistryItem<UDN, D> item : this.deviceItems) {
            final D[] d = item.getItem().findDevices(serviceType);
            if (d != null) {
                devices.addAll((Collection<? extends D>)Arrays.asList(d));
            }
        }
        return devices;
    }
    
    Collection<D> get() {
        final Collection<D> devices = new HashSet<D>();
        for (final RegistryItem<UDN, D> item : this.deviceItems) {
            devices.add(item.getItem());
        }
        return devices;
    }
    
    boolean contains(final D device) {
        return this.contains(device.getIdentity().getUdn());
    }
    
    boolean contains(final UDN udn) {
        return this.deviceItems.contains(new RegistryItem(udn));
    }
    
    void addSubscription(final S subscription) {
        final RegistryItem<String, S> subscriptionItem = new RegistryItem<String, S>(subscription.getSubscriptionId(), subscription, subscription.getActualDurationSeconds());
        this.subscriptionItems.add(subscriptionItem);
    }
    
    boolean updateSubscription(final S subscription) {
        if (this.removeSubscription(subscription)) {
            this.addSubscription(subscription);
            return true;
        }
        return false;
    }
    
    boolean removeSubscription(final S subscription) {
        return this.subscriptionItems.remove(new RegistryItem(subscription.getSubscriptionId()));
    }
    
    S getSubscription(final String subscriptionId) {
        for (final RegistryItem<String, S> registryItem : this.subscriptionItems) {
            if (registryItem.getKey().equals(subscriptionId)) {
                return registryItem.getItem();
            }
        }
        return null;
    }
    
    Resource[] getResources(final Device device) throws RegistrationException {
        try {
            return this.registry.getConfiguration().getNamespace().getResources(device);
        }
        catch (ValidationException ex) {
            throw new RegistrationException("Resource discover error: " + ex.toString(), ex);
        }
    }
}
