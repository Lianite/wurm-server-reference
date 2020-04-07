// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.meta;

import org.fourthline.cling.model.resource.Resource;
import org.fourthline.cling.model.Namespace;
import java.net.URI;
import java.util.HashSet;
import org.fourthline.cling.model.types.ServiceId;
import java.util.Collection;
import java.util.Arrays;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.model.profile.RemoteClientInfo;
import java.util.Iterator;
import java.util.List;
import org.fourthline.cling.model.ValidationError;
import java.util.logging.Level;
import java.util.ArrayList;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.types.DeviceType;
import java.util.logging.Logger;
import org.fourthline.cling.model.Validatable;

public abstract class Device<DI extends DeviceIdentity, D extends Device, S extends Service> implements Validatable
{
    private static final Logger log;
    private final DI identity;
    private final UDAVersion version;
    private final DeviceType type;
    private final DeviceDetails details;
    private final Icon[] icons;
    protected final S[] services;
    protected final D[] embeddedDevices;
    private D parentDevice;
    
    public Device(final DI identity) throws ValidationException {
        this(identity, null, null, null, null, null);
    }
    
    public Device(final DI identity, final DeviceType type, final DeviceDetails details, final Icon[] icons, final S[] services) throws ValidationException {
        this(identity, null, type, details, icons, services, null);
    }
    
    public Device(final DI identity, final DeviceType type, final DeviceDetails details, final Icon[] icons, final S[] services, final D[] embeddedDevices) throws ValidationException {
        this(identity, null, type, details, icons, services, embeddedDevices);
    }
    
    public Device(final DI identity, final UDAVersion version, final DeviceType type, final DeviceDetails details, final Icon[] icons, final S[] services, final D[] embeddedDevices) throws ValidationException {
        this.identity = identity;
        this.version = ((version == null) ? new UDAVersion() : version);
        this.type = type;
        this.details = details;
        final List<Icon> validIcons = new ArrayList<Icon>();
        if (icons != null) {
            for (final Icon icon : icons) {
                if (icon != null) {
                    icon.setDevice(this);
                    final List<ValidationError> iconErrors = icon.validate();
                    if (iconErrors.isEmpty()) {
                        validIcons.add(icon);
                    }
                    else {
                        Device.log.warning("Discarding invalid '" + icon + "': " + iconErrors);
                    }
                }
            }
        }
        this.icons = validIcons.toArray(new Icon[validIcons.size()]);
        boolean allNullServices = true;
        if (services != null) {
            for (final S service : services) {
                if (service != null) {
                    allNullServices = false;
                    service.setDevice(this);
                }
            }
        }
        this.services = (Service[])((services == null || allNullServices) ? null : services);
        boolean allNullEmbedded = true;
        if (embeddedDevices != null) {
            for (final D embeddedDevice : embeddedDevices) {
                if (embeddedDevice != null) {
                    allNullEmbedded = false;
                    embeddedDevice.setParentDevice(this);
                }
            }
        }
        this.embeddedDevices = (Device[])((embeddedDevices == null || allNullEmbedded) ? null : embeddedDevices);
        final List<ValidationError> errors = this.validate();
        if (errors.size() > 0) {
            if (Device.log.isLoggable(Level.FINEST)) {
                for (final ValidationError error : errors) {
                    Device.log.finest(error.toString());
                }
            }
            throw new ValidationException("Validation of device graph failed, call getErrors() on exception", errors);
        }
    }
    
    public DI getIdentity() {
        return this.identity;
    }
    
    public UDAVersion getVersion() {
        return this.version;
    }
    
    public DeviceType getType() {
        return this.type;
    }
    
    public DeviceDetails getDetails() {
        return this.details;
    }
    
    public DeviceDetails getDetails(final RemoteClientInfo info) {
        return this.getDetails();
    }
    
    public Icon[] getIcons() {
        return this.icons;
    }
    
    public boolean hasIcons() {
        return this.getIcons() != null && this.getIcons().length > 0;
    }
    
    public boolean hasServices() {
        return this.getServices() != null && this.getServices().length > 0;
    }
    
    public boolean hasEmbeddedDevices() {
        return this.getEmbeddedDevices() != null && this.getEmbeddedDevices().length > 0;
    }
    
    public D getParentDevice() {
        return this.parentDevice;
    }
    
    void setParentDevice(final D parentDevice) {
        if (this.parentDevice != null) {
            throw new IllegalStateException("Final value has been set already, model is immutable");
        }
        this.parentDevice = parentDevice;
    }
    
    public boolean isRoot() {
        return this.getParentDevice() == null;
    }
    
    public abstract S[] getServices();
    
    public abstract D[] getEmbeddedDevices();
    
    public abstract D getRoot();
    
    public abstract D findDevice(final UDN p0);
    
    public D[] findEmbeddedDevices() {
        return (D[])this.toDeviceArray(this.findEmbeddedDevices((Device<DI, Device<DI, Device, S>, S>)this));
    }
    
    public D[] findDevices(final DeviceType deviceType) {
        return (D[])this.toDeviceArray(this.find(deviceType, (Device<DI, Device<DI, Device, S>, S>)this));
    }
    
    public D[] findDevices(final ServiceType serviceType) {
        return (D[])this.toDeviceArray(this.find(serviceType, (Device<DI, Device<DI, Device, S>, S>)this));
    }
    
    public Icon[] findIcons() {
        final List<Icon> icons = new ArrayList<Icon>();
        if (this.hasIcons()) {
            icons.addAll(Arrays.asList(this.getIcons()));
        }
        final D[] embeddedDevices2;
        final D[] embeddedDevices = embeddedDevices2 = this.findEmbeddedDevices();
        for (final D embeddedDevice : embeddedDevices2) {
            if (embeddedDevice.hasIcons()) {
                icons.addAll(Arrays.asList(embeddedDevice.getIcons()));
            }
        }
        return icons.toArray(new Icon[icons.size()]);
    }
    
    public S[] findServices() {
        return this.toServiceArray(this.findServices(null, null, (Device<DI, Device<DI, Device, S>, S>)this));
    }
    
    public S[] findServices(final ServiceType serviceType) {
        return this.toServiceArray(this.findServices(serviceType, null, (Device<DI, Device<DI, Device, S>, S>)this));
    }
    
    protected D find(final UDN udn, final D current) {
        if (current.getIdentity() != null && current.getIdentity().getUdn() != null && current.getIdentity().getUdn().equals(udn)) {
            return current;
        }
        if (current.hasEmbeddedDevices()) {
            for (final D embeddedDevice : current.getEmbeddedDevices()) {
                final D match;
                if ((match = this.find(udn, embeddedDevice)) != null) {
                    return match;
                }
            }
        }
        return null;
    }
    
    protected Collection<D> findEmbeddedDevices(final D current) {
        final Collection<D> devices = new HashSet<D>();
        if (!current.isRoot() && current.getIdentity().getUdn() != null) {
            devices.add(current);
        }
        if (current.hasEmbeddedDevices()) {
            for (final D embeddedDevice : current.getEmbeddedDevices()) {
                devices.addAll(this.findEmbeddedDevices((D)embeddedDevice));
            }
        }
        return devices;
    }
    
    protected Collection<D> find(final DeviceType deviceType, final D current) {
        final Collection<D> devices = new HashSet<D>();
        if (current.getType() != null && current.getType().implementsVersion(deviceType)) {
            devices.add(current);
        }
        if (current.hasEmbeddedDevices()) {
            for (final D embeddedDevice : current.getEmbeddedDevices()) {
                devices.addAll(this.find(deviceType, (D)embeddedDevice));
            }
        }
        return devices;
    }
    
    protected Collection<D> find(final ServiceType serviceType, final D current) {
        final Collection<S> services = this.findServices(serviceType, null, current);
        final Collection<D> devices = new HashSet<D>();
        for (final Service service : services) {
            devices.add(service.getDevice());
        }
        return devices;
    }
    
    protected Collection<S> findServices(final ServiceType serviceType, final ServiceId serviceId, final D current) {
        final Collection services = new HashSet();
        if (current.hasServices()) {
            for (final Service service : current.getServices()) {
                if (this.isMatch(service, serviceType, serviceId)) {
                    services.add(service);
                }
            }
        }
        final Collection<D> embeddedDevices = this.findEmbeddedDevices(current);
        if (embeddedDevices != null) {
            for (final D embeddedDevice : embeddedDevices) {
                if (embeddedDevice.hasServices()) {
                    for (final Service service2 : embeddedDevice.getServices()) {
                        if (this.isMatch(service2, serviceType, serviceId)) {
                            services.add(service2);
                        }
                    }
                }
            }
        }
        return (Collection<S>)services;
    }
    
    public S findService(final ServiceId serviceId) {
        final Collection<S> services = this.findServices(null, serviceId, (Device<DI, Device<DI, Device<DI, Device, S>, S>, S>)this);
        return (S)((services.size() == 1) ? ((S)services.iterator().next()) : null);
    }
    
    public S findService(final ServiceType serviceType) {
        final Collection<S> services = this.findServices(serviceType, null, (Device<DI, Device<DI, Device<DI, Device, S>, S>, S>)this);
        return (S)((services.size() > 0) ? ((S)services.iterator().next()) : null);
    }
    
    public ServiceType[] findServiceTypes() {
        final Collection<S> services = this.findServices(null, null, (Device<DI, Device<DI, Device<DI, Device, S>, S>, S>)this);
        final Collection<ServiceType> col = new HashSet<ServiceType>();
        for (final S service : services) {
            col.add(service.getServiceType());
        }
        return col.toArray(new ServiceType[col.size()]);
    }
    
    private boolean isMatch(final Service s, final ServiceType serviceType, final ServiceId serviceId) {
        final boolean matchesType = serviceType == null || s.getServiceType().implementsVersion(serviceType);
        final boolean matchesId = serviceId == null || s.getServiceId().equals(serviceId);
        return matchesType && matchesId;
    }
    
    public boolean isFullyHydrated() {
        final S[] services2;
        final S[] services = services2 = this.findServices();
        for (final S service : services2) {
            if (service.hasStateVariables()) {
                return true;
            }
        }
        return false;
    }
    
    public String getDisplayString() {
        String cleanModelName = null;
        String cleanModelNumber = null;
        if (this.getDetails() != null && this.getDetails().getModelDetails() != null) {
            final ModelDetails modelDetails = this.getDetails().getModelDetails();
            if (modelDetails.getModelName() != null) {
                cleanModelName = ((modelDetails.getModelNumber() != null && modelDetails.getModelName().endsWith(modelDetails.getModelNumber())) ? modelDetails.getModelName().substring(0, modelDetails.getModelName().length() - modelDetails.getModelNumber().length()) : modelDetails.getModelName());
            }
            if (cleanModelName != null) {
                cleanModelNumber = ((modelDetails.getModelNumber() != null && !cleanModelName.startsWith(modelDetails.getModelNumber())) ? modelDetails.getModelNumber() : "");
            }
            else {
                cleanModelNumber = modelDetails.getModelNumber();
            }
        }
        final StringBuilder sb = new StringBuilder();
        if (this.getDetails() != null && this.getDetails().getManufacturerDetails() != null) {
            if (cleanModelName != null && this.getDetails().getManufacturerDetails().getManufacturer() != null) {
                cleanModelName = (cleanModelName.startsWith(this.getDetails().getManufacturerDetails().getManufacturer()) ? cleanModelName.substring(this.getDetails().getManufacturerDetails().getManufacturer().length()).trim() : cleanModelName.trim());
            }
            if (this.getDetails().getManufacturerDetails().getManufacturer() != null) {
                sb.append(this.getDetails().getManufacturerDetails().getManufacturer());
            }
        }
        sb.append((cleanModelName != null && cleanModelName.length() > 0) ? (" " + cleanModelName) : "");
        sb.append((cleanModelNumber != null && cleanModelNumber.length() > 0) ? (" " + cleanModelNumber.trim()) : "");
        return sb.toString();
    }
    
    @Override
    public List<ValidationError> validate() {
        final List<ValidationError> errors = new ArrayList<ValidationError>();
        if (this.getType() != null) {
            errors.addAll(this.getVersion().validate());
            if (this.getIdentity() != null) {
                errors.addAll(this.getIdentity().validate());
            }
            if (this.getDetails() != null) {
                errors.addAll(this.getDetails().validate());
            }
            if (this.hasServices()) {
                for (final Service service : this.getServices()) {
                    if (service != null) {
                        errors.addAll(service.validate());
                    }
                }
            }
            if (this.hasEmbeddedDevices()) {
                for (final Device embeddedDevice : this.getEmbeddedDevices()) {
                    if (embeddedDevice != null) {
                        errors.addAll(embeddedDevice.validate());
                    }
                }
            }
        }
        return errors;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Device device = (Device)o;
        return this.identity.equals(device.identity);
    }
    
    @Override
    public int hashCode() {
        return this.identity.hashCode();
    }
    
    public abstract D newInstance(final UDN p0, final UDAVersion p1, final DeviceType p2, final DeviceDetails p3, final Icon[] p4, final S[] p5, final List<D> p6) throws ValidationException;
    
    public abstract S newInstance(final ServiceType p0, final ServiceId p1, final URI p2, final URI p3, final URI p4, final Action<S>[] p5, final StateVariable<S>[] p6) throws ValidationException;
    
    public abstract D[] toDeviceArray(final Collection<D> p0);
    
    public abstract S[] newServiceArray(final int p0);
    
    public abstract S[] toServiceArray(final Collection<S> p0);
    
    public abstract Resource[] discoverResources(final Namespace p0);
    
    @Override
    public String toString() {
        return "(" + this.getClass().getSimpleName() + ") Identity: " + this.getIdentity().toString() + ", Root: " + this.isRoot();
    }
    
    static {
        log = Logger.getLogger(Device.class.getName());
    }
}
