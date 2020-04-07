// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.meta;

import java.util.Arrays;
import org.fourthline.cling.model.resource.IconResource;
import org.fourthline.cling.model.resource.ServiceEventSubscriptionResource;
import org.fourthline.cling.model.resource.ServiceControlResource;
import org.fourthline.cling.model.resource.ServiceDescriptorResource;
import org.fourthline.cling.model.resource.DeviceDescriptorResource;
import org.fourthline.cling.model.resource.Resource;
import org.fourthline.cling.model.Namespace;
import java.util.ArrayList;
import org.fourthline.cling.model.ValidationError;
import java.util.Collection;
import java.net.URI;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.ServiceType;
import java.util.List;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.model.profile.RemoteClientInfo;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.profile.DeviceDetailsProvider;

public class LocalDevice extends Device<DeviceIdentity, LocalDevice, LocalService>
{
    private final DeviceDetailsProvider deviceDetailsProvider;
    
    public LocalDevice(final DeviceIdentity identity) throws ValidationException {
        super(identity);
        this.deviceDetailsProvider = null;
    }
    
    public LocalDevice(final DeviceIdentity identity, final DeviceType type, final DeviceDetails details, final LocalService service) throws ValidationException {
        super(identity, type, details, null, new LocalService[] { service });
        this.deviceDetailsProvider = null;
    }
    
    public LocalDevice(final DeviceIdentity identity, final DeviceType type, final DeviceDetailsProvider deviceDetailsProvider, final LocalService service) throws ValidationException {
        super(identity, type, null, null, new LocalService[] { service });
        this.deviceDetailsProvider = deviceDetailsProvider;
    }
    
    public LocalDevice(final DeviceIdentity identity, final DeviceType type, final DeviceDetailsProvider deviceDetailsProvider, final LocalService service, final LocalDevice embeddedDevice) throws ValidationException {
        super(identity, type, null, null, new LocalService[] { service }, new LocalDevice[] { embeddedDevice });
        this.deviceDetailsProvider = deviceDetailsProvider;
    }
    
    public LocalDevice(final DeviceIdentity identity, final DeviceType type, final DeviceDetails details, final LocalService service, final LocalDevice embeddedDevice) throws ValidationException {
        super(identity, type, details, null, new LocalService[] { service }, new LocalDevice[] { embeddedDevice });
        this.deviceDetailsProvider = null;
    }
    
    public LocalDevice(final DeviceIdentity identity, final DeviceType type, final DeviceDetails details, final LocalService[] services) throws ValidationException {
        super(identity, type, details, null, services);
        this.deviceDetailsProvider = null;
    }
    
    public LocalDevice(final DeviceIdentity identity, final DeviceType type, final DeviceDetails details, final LocalService[] services, final LocalDevice[] embeddedDevices) throws ValidationException {
        super(identity, type, details, null, services, embeddedDevices);
        this.deviceDetailsProvider = null;
    }
    
    public LocalDevice(final DeviceIdentity identity, final DeviceType type, final DeviceDetails details, final Icon icon, final LocalService service) throws ValidationException {
        super(identity, type, details, new Icon[] { icon }, new LocalService[] { service });
        this.deviceDetailsProvider = null;
    }
    
    public LocalDevice(final DeviceIdentity identity, final DeviceType type, final DeviceDetails details, final Icon icon, final LocalService service, final LocalDevice embeddedDevice) throws ValidationException {
        super(identity, type, details, new Icon[] { icon }, new LocalService[] { service }, new LocalDevice[] { embeddedDevice });
        this.deviceDetailsProvider = null;
    }
    
    public LocalDevice(final DeviceIdentity identity, final DeviceType type, final DeviceDetails details, final Icon icon, final LocalService[] services) throws ValidationException {
        super(identity, type, details, new Icon[] { icon }, services);
        this.deviceDetailsProvider = null;
    }
    
    public LocalDevice(final DeviceIdentity identity, final DeviceType type, final DeviceDetailsProvider deviceDetailsProvider, final Icon icon, final LocalService[] services) throws ValidationException {
        super(identity, type, null, new Icon[] { icon }, services);
        this.deviceDetailsProvider = deviceDetailsProvider;
    }
    
    public LocalDevice(final DeviceIdentity identity, final DeviceType type, final DeviceDetails details, final Icon icon, final LocalService[] services, final LocalDevice[] embeddedDevices) throws ValidationException {
        super(identity, type, details, new Icon[] { icon }, services, embeddedDevices);
        this.deviceDetailsProvider = null;
    }
    
    public LocalDevice(final DeviceIdentity identity, final DeviceType type, final DeviceDetails details, final Icon[] icons, final LocalService service) throws ValidationException {
        super(identity, type, details, icons, new LocalService[] { service });
        this.deviceDetailsProvider = null;
    }
    
    public LocalDevice(final DeviceIdentity identity, final DeviceType type, final DeviceDetails details, final Icon[] icons, final LocalService service, final LocalDevice embeddedDevice) throws ValidationException {
        super(identity, type, details, icons, new LocalService[] { service }, new LocalDevice[] { embeddedDevice });
        this.deviceDetailsProvider = null;
    }
    
    public LocalDevice(final DeviceIdentity identity, final DeviceType type, final DeviceDetailsProvider deviceDetailsProvider, final Icon[] icons, final LocalService service, final LocalDevice embeddedDevice) throws ValidationException {
        super(identity, type, null, icons, new LocalService[] { service }, new LocalDevice[] { embeddedDevice });
        this.deviceDetailsProvider = deviceDetailsProvider;
    }
    
    public LocalDevice(final DeviceIdentity identity, final DeviceType type, final DeviceDetails details, final Icon[] icons, final LocalService[] services) throws ValidationException {
        super(identity, type, details, icons, services);
        this.deviceDetailsProvider = null;
    }
    
    public LocalDevice(final DeviceIdentity identity, final DeviceType type, final DeviceDetails details, final Icon[] icons, final LocalService[] services, final LocalDevice[] embeddedDevices) throws ValidationException {
        super(identity, type, details, icons, services, embeddedDevices);
        this.deviceDetailsProvider = null;
    }
    
    public LocalDevice(final DeviceIdentity identity, final UDAVersion version, final DeviceType type, final DeviceDetails details, final Icon[] icons, final LocalService[] services, final LocalDevice[] embeddedDevices) throws ValidationException {
        super(identity, version, type, details, icons, services, embeddedDevices);
        this.deviceDetailsProvider = null;
    }
    
    public LocalDevice(final DeviceIdentity identity, final UDAVersion version, final DeviceType type, final DeviceDetailsProvider deviceDetailsProvider, final Icon[] icons, final LocalService[] services, final LocalDevice[] embeddedDevices) throws ValidationException {
        super(identity, version, type, null, icons, services, embeddedDevices);
        this.deviceDetailsProvider = deviceDetailsProvider;
    }
    
    public DeviceDetailsProvider getDeviceDetailsProvider() {
        return this.deviceDetailsProvider;
    }
    
    @Override
    public DeviceDetails getDetails(final RemoteClientInfo info) {
        if (this.getDeviceDetailsProvider() != null) {
            return this.getDeviceDetailsProvider().provide(info);
        }
        return this.getDetails();
    }
    
    @Override
    public LocalService[] getServices() {
        return (LocalService[])((this.services != null) ? this.services : new LocalService[0]);
    }
    
    @Override
    public LocalDevice[] getEmbeddedDevices() {
        return (LocalDevice[])((this.embeddedDevices != null) ? this.embeddedDevices : new LocalDevice[0]);
    }
    
    @Override
    public LocalDevice newInstance(final UDN udn, final UDAVersion version, final DeviceType type, final DeviceDetails details, final Icon[] icons, final LocalService[] services, final List<LocalDevice> embeddedDevices) throws ValidationException {
        return new LocalDevice(new DeviceIdentity(udn, ((Device<DeviceIdentity, D, S>)this).getIdentity().getMaxAgeSeconds()), version, type, details, icons, services, (LocalDevice[])((embeddedDevices.size() > 0) ? ((LocalDevice[])embeddedDevices.toArray(new LocalDevice[embeddedDevices.size()])) : null));
    }
    
    @Override
    public LocalService newInstance(final ServiceType serviceType, final ServiceId serviceId, final URI descriptorURI, final URI controlURI, final URI eventSubscriptionURI, final Action<LocalService>[] actions, final StateVariable<LocalService>[] stateVariables) throws ValidationException {
        return new LocalService(serviceType, serviceId, actions, stateVariables);
    }
    
    @Override
    public LocalDevice[] toDeviceArray(final Collection<LocalDevice> col) {
        return col.toArray(new LocalDevice[col.size()]);
    }
    
    @Override
    public LocalService[] newServiceArray(final int size) {
        return new LocalService[size];
    }
    
    @Override
    public LocalService[] toServiceArray(final Collection<LocalService> col) {
        return col.toArray(new LocalService[col.size()]);
    }
    
    @Override
    public List<ValidationError> validate() {
        final List<ValidationError> errors = new ArrayList<ValidationError>();
        errors.addAll(super.validate());
        if (this.hasIcons()) {
            for (final Icon icon : this.getIcons()) {
                if (icon.getUri().isAbsolute()) {
                    errors.add(new ValidationError(this.getClass(), "icons", "Local icon URI can not be absolute: " + icon.getUri()));
                }
                if (icon.getUri().toString().contains("../")) {
                    errors.add(new ValidationError(this.getClass(), "icons", "Local icon URI must not contain '../': " + icon.getUri()));
                }
                if (icon.getUri().toString().startsWith("/")) {
                    errors.add(new ValidationError(this.getClass(), "icons", "Local icon URI must not start with '/': " + icon.getUri()));
                }
            }
        }
        return errors;
    }
    
    @Override
    public Resource[] discoverResources(final Namespace namespace) {
        final List<Resource> discovered = new ArrayList<Resource>();
        if (this.isRoot()) {
            discovered.add(new DeviceDescriptorResource(namespace.getDescriptorPath(this), this));
        }
        for (final LocalService service : this.getServices()) {
            discovered.add(new ServiceDescriptorResource(namespace.getDescriptorPath(service), service));
            discovered.add(new ServiceControlResource(namespace.getControlPath(service), service));
            discovered.add(new ServiceEventSubscriptionResource(namespace.getEventSubscriptionPath(service), service));
        }
        for (final Icon icon : this.getIcons()) {
            discovered.add(new IconResource(namespace.prefixIfRelative(this, icon.getUri()), icon));
        }
        if (this.hasEmbeddedDevices()) {
            for (final Device embeddedDevice : this.getEmbeddedDevices()) {
                discovered.addAll(Arrays.asList(embeddedDevice.discoverResources(namespace)));
            }
        }
        return discovered.toArray(new Resource[discovered.size()]);
    }
    
    @Override
    public LocalDevice getRoot() {
        if (this.isRoot()) {
            return this;
        }
        LocalDevice current;
        for (current = this; current.getParentDevice() != null; current = ((Device<DI, LocalDevice, S>)current).getParentDevice()) {}
        return current;
    }
    
    @Override
    public LocalDevice findDevice(final UDN udn) {
        return ((Device<DI, LocalDevice, S>)this).find(udn, this);
    }
}
