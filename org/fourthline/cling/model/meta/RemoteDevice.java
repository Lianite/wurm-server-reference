// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.meta;

import java.util.Arrays;
import org.fourthline.cling.model.resource.ServiceEventCallbackResource;
import java.util.ArrayList;
import org.fourthline.cling.model.resource.Resource;
import org.fourthline.cling.model.Namespace;
import java.util.Collection;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.ServiceType;
import java.util.List;
import org.fourthline.cling.model.types.UDN;
import org.seamless.util.URIUtil;
import java.net.URL;
import java.net.URI;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.ValidationException;

public class RemoteDevice extends Device<RemoteDeviceIdentity, RemoteDevice, RemoteService>
{
    public RemoteDevice(final RemoteDeviceIdentity identity) throws ValidationException {
        super(identity);
    }
    
    public RemoteDevice(final RemoteDeviceIdentity identity, final DeviceType type, final DeviceDetails details, final RemoteService service) throws ValidationException {
        super(identity, type, details, null, new RemoteService[] { service });
    }
    
    public RemoteDevice(final RemoteDeviceIdentity identity, final DeviceType type, final DeviceDetails details, final RemoteService service, final RemoteDevice embeddedDevice) throws ValidationException {
        super(identity, type, details, null, new RemoteService[] { service }, new RemoteDevice[] { embeddedDevice });
    }
    
    public RemoteDevice(final RemoteDeviceIdentity identity, final DeviceType type, final DeviceDetails details, final RemoteService[] services) throws ValidationException {
        super(identity, type, details, null, services);
    }
    
    public RemoteDevice(final RemoteDeviceIdentity identity, final DeviceType type, final DeviceDetails details, final RemoteService[] services, final RemoteDevice[] embeddedDevices) throws ValidationException {
        super(identity, type, details, null, services, embeddedDevices);
    }
    
    public RemoteDevice(final RemoteDeviceIdentity identity, final DeviceType type, final DeviceDetails details, final Icon icon, final RemoteService service) throws ValidationException {
        super(identity, type, details, new Icon[] { icon }, new RemoteService[] { service });
    }
    
    public RemoteDevice(final RemoteDeviceIdentity identity, final DeviceType type, final DeviceDetails details, final Icon icon, final RemoteService service, final RemoteDevice embeddedDevice) throws ValidationException {
        super(identity, type, details, new Icon[] { icon }, new RemoteService[] { service }, new RemoteDevice[] { embeddedDevice });
    }
    
    public RemoteDevice(final RemoteDeviceIdentity identity, final DeviceType type, final DeviceDetails details, final Icon icon, final RemoteService[] services) throws ValidationException {
        super(identity, type, details, new Icon[] { icon }, services);
    }
    
    public RemoteDevice(final RemoteDeviceIdentity identity, final DeviceType type, final DeviceDetails details, final Icon icon, final RemoteService[] services, final RemoteDevice[] embeddedDevices) throws ValidationException {
        super(identity, type, details, new Icon[] { icon }, services, embeddedDevices);
    }
    
    public RemoteDevice(final RemoteDeviceIdentity identity, final DeviceType type, final DeviceDetails details, final Icon[] icons, final RemoteService service) throws ValidationException {
        super(identity, type, details, icons, new RemoteService[] { service });
    }
    
    public RemoteDevice(final RemoteDeviceIdentity identity, final DeviceType type, final DeviceDetails details, final Icon[] icons, final RemoteService service, final RemoteDevice embeddedDevice) throws ValidationException {
        super(identity, type, details, icons, new RemoteService[] { service }, new RemoteDevice[] { embeddedDevice });
    }
    
    public RemoteDevice(final RemoteDeviceIdentity identity, final DeviceType type, final DeviceDetails details, final Icon[] icons, final RemoteService[] services) throws ValidationException {
        super(identity, type, details, icons, services);
    }
    
    public RemoteDevice(final RemoteDeviceIdentity identity, final DeviceType type, final DeviceDetails details, final Icon[] icons, final RemoteService[] services, final RemoteDevice[] embeddedDevices) throws ValidationException {
        super(identity, type, details, icons, services, embeddedDevices);
    }
    
    public RemoteDevice(final RemoteDeviceIdentity identity, final UDAVersion version, final DeviceType type, final DeviceDetails details, final Icon[] icons, final RemoteService[] services, final RemoteDevice[] embeddedDevices) throws ValidationException {
        super(identity, version, type, details, icons, services, embeddedDevices);
    }
    
    @Override
    public RemoteService[] getServices() {
        return (RemoteService[])((this.services != null) ? this.services : new RemoteService[0]);
    }
    
    @Override
    public RemoteDevice[] getEmbeddedDevices() {
        return (RemoteDevice[])((this.embeddedDevices != null) ? this.embeddedDevices : new RemoteDevice[0]);
    }
    
    public URL normalizeURI(final URI relativeOrAbsoluteURI) {
        if (this.getDetails() != null && this.getDetails().getBaseURL() != null) {
            return URIUtil.createAbsoluteURL(this.getDetails().getBaseURL(), relativeOrAbsoluteURI);
        }
        return URIUtil.createAbsoluteURL(((Device<RemoteDeviceIdentity, D, S>)this).getIdentity().getDescriptorURL(), relativeOrAbsoluteURI);
    }
    
    @Override
    public RemoteDevice newInstance(final UDN udn, final UDAVersion version, final DeviceType type, final DeviceDetails details, final Icon[] icons, final RemoteService[] services, final List<RemoteDevice> embeddedDevices) throws ValidationException {
        return new RemoteDevice(new RemoteDeviceIdentity(udn, ((Device<RemoteDeviceIdentity, D, S>)this).getIdentity()), version, type, details, icons, services, (RemoteDevice[])((embeddedDevices.size() > 0) ? ((RemoteDevice[])embeddedDevices.toArray(new RemoteDevice[embeddedDevices.size()])) : null));
    }
    
    @Override
    public RemoteService newInstance(final ServiceType serviceType, final ServiceId serviceId, final URI descriptorURI, final URI controlURI, final URI eventSubscriptionURI, final Action<RemoteService>[] actions, final StateVariable<RemoteService>[] stateVariables) throws ValidationException {
        return new RemoteService(serviceType, serviceId, descriptorURI, controlURI, eventSubscriptionURI, actions, stateVariables);
    }
    
    @Override
    public RemoteDevice[] toDeviceArray(final Collection<RemoteDevice> col) {
        return col.toArray(new RemoteDevice[col.size()]);
    }
    
    @Override
    public RemoteService[] newServiceArray(final int size) {
        return new RemoteService[size];
    }
    
    @Override
    public RemoteService[] toServiceArray(final Collection<RemoteService> col) {
        return col.toArray(new RemoteService[col.size()]);
    }
    
    @Override
    public Resource[] discoverResources(final Namespace namespace) {
        final List<Resource> discovered = new ArrayList<Resource>();
        for (final RemoteService service : this.getServices()) {
            if (service != null) {
                discovered.add(new ServiceEventCallbackResource(namespace.getEventCallbackPath(service), service));
            }
        }
        if (this.hasEmbeddedDevices()) {
            for (final Device embeddedDevice : this.getEmbeddedDevices()) {
                if (embeddedDevice != null) {
                    discovered.addAll(Arrays.asList(embeddedDevice.discoverResources(namespace)));
                }
            }
        }
        return discovered.toArray(new Resource[discovered.size()]);
    }
    
    @Override
    public RemoteDevice getRoot() {
        if (this.isRoot()) {
            return this;
        }
        RemoteDevice current;
        for (current = this; current.getParentDevice() != null; current = ((Device<DI, RemoteDevice, S>)current).getParentDevice()) {}
        return current;
    }
    
    @Override
    public RemoteDevice findDevice(final UDN udn) {
        return ((Device<DI, RemoteDevice, S>)this).find(udn, this);
    }
}
