// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.protocol;

import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.meta.Device;
import java.util.concurrent.CopyOnWriteArraySet;
import org.fourthline.cling.model.types.ServiceType;
import java.util.Arrays;
import org.fourthline.cling.binding.xml.ServiceDescriptorBinder;
import java.util.Collection;
import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.RemoteService;
import java.util.Iterator;
import org.fourthline.cling.binding.xml.DeviceDescriptorBinder;
import org.fourthline.cling.registry.RegistrationException;
import org.seamless.util.Exceptions;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.ValidationError;
import org.fourthline.cling.binding.xml.DescriptorBindingException;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.UpnpHeaders;
import org.fourthline.cling.model.message.UpnpResponse;
import java.util.Map;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.UpnpRequest;
import org.fourthline.cling.transport.RouterException;
import java.util.logging.Level;
import org.fourthline.cling.model.meta.RemoteDeviceIdentity;
import java.util.ArrayList;
import org.fourthline.cling.model.types.UDN;
import java.util.List;
import java.net.URL;
import java.util.Set;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.UpnpService;
import java.util.logging.Logger;

public class RetrieveRemoteDescriptors implements Runnable
{
    private static final Logger log;
    private final UpnpService upnpService;
    private RemoteDevice rd;
    private static final Set<URL> activeRetrievals;
    protected List<UDN> errorsAlreadyLogged;
    
    public RetrieveRemoteDescriptors(final UpnpService upnpService, final RemoteDevice rd) {
        this.errorsAlreadyLogged = new ArrayList<UDN>();
        this.upnpService = upnpService;
        this.rd = rd;
    }
    
    public UpnpService getUpnpService() {
        return this.upnpService;
    }
    
    @Override
    public void run() {
        final URL deviceURL = ((Device<RemoteDeviceIdentity, D, S>)this.rd).getIdentity().getDescriptorURL();
        if (RetrieveRemoteDescriptors.activeRetrievals.contains(deviceURL)) {
            RetrieveRemoteDescriptors.log.finer("Exiting early, active retrieval for URL already in progress: " + deviceURL);
            return;
        }
        if (this.getUpnpService().getRegistry().getRemoteDevice(((Device<RemoteDeviceIdentity, D, S>)this.rd).getIdentity().getUdn(), true) != null) {
            RetrieveRemoteDescriptors.log.finer("Exiting early, already discovered: " + deviceURL);
            return;
        }
        try {
            RetrieveRemoteDescriptors.activeRetrievals.add(deviceURL);
            this.describe();
        }
        catch (RouterException ex) {
            RetrieveRemoteDescriptors.log.log(Level.WARNING, "Descriptor retrieval failed: " + deviceURL, ex);
        }
        finally {
            RetrieveRemoteDescriptors.activeRetrievals.remove(deviceURL);
        }
    }
    
    protected void describe() throws RouterException {
        if (this.getUpnpService().getRouter() == null) {
            RetrieveRemoteDescriptors.log.warning("Router not yet initialized");
            return;
        }
        StreamResponseMessage deviceDescMsg;
        try {
            final StreamRequestMessage deviceDescRetrievalMsg = new StreamRequestMessage(UpnpRequest.Method.GET, ((Device<RemoteDeviceIdentity, D, S>)this.rd).getIdentity().getDescriptorURL());
            final UpnpHeaders headers = this.getUpnpService().getConfiguration().getDescriptorRetrievalHeaders(((Device<RemoteDeviceIdentity, D, S>)this.rd).getIdentity());
            if (headers != null) {
                deviceDescRetrievalMsg.getHeaders().putAll(headers);
            }
            RetrieveRemoteDescriptors.log.fine("Sending device descriptor retrieval message: " + deviceDescRetrievalMsg);
            deviceDescMsg = this.getUpnpService().getRouter().send(deviceDescRetrievalMsg);
        }
        catch (IllegalArgumentException ex) {
            RetrieveRemoteDescriptors.log.warning("Device descriptor retrieval failed: " + ((Device<RemoteDeviceIdentity, D, S>)this.rd).getIdentity().getDescriptorURL() + ", possibly invalid URL: " + ex);
            return;
        }
        if (deviceDescMsg == null) {
            RetrieveRemoteDescriptors.log.warning("Device descriptor retrieval failed, no response: " + ((Device<RemoteDeviceIdentity, D, S>)this.rd).getIdentity().getDescriptorURL());
            return;
        }
        if (deviceDescMsg.getOperation().isFailed()) {
            RetrieveRemoteDescriptors.log.warning("Device descriptor retrieval failed: " + ((Device<RemoteDeviceIdentity, D, S>)this.rd).getIdentity().getDescriptorURL() + ", " + deviceDescMsg.getOperation().getResponseDetails());
            return;
        }
        if (!deviceDescMsg.isContentTypeTextUDA()) {
            RetrieveRemoteDescriptors.log.fine("Received device descriptor without or with invalid Content-Type: " + ((Device<RemoteDeviceIdentity, D, S>)this.rd).getIdentity().getDescriptorURL());
        }
        final String descriptorContent = deviceDescMsg.getBodyString();
        if (descriptorContent == null || descriptorContent.length() == 0) {
            RetrieveRemoteDescriptors.log.warning("Received empty device descriptor:" + ((Device<RemoteDeviceIdentity, D, S>)this.rd).getIdentity().getDescriptorURL());
            return;
        }
        RetrieveRemoteDescriptors.log.fine("Received root device descriptor: " + deviceDescMsg);
        this.describe(descriptorContent);
    }
    
    protected void describe(final String descriptorXML) throws RouterException {
        boolean notifiedStart = false;
        RemoteDevice describedDevice = null;
        try {
            final DeviceDescriptorBinder deviceDescriptorBinder = this.getUpnpService().getConfiguration().getDeviceDescriptorBinderUDA10();
            describedDevice = deviceDescriptorBinder.describe(this.rd, descriptorXML);
            RetrieveRemoteDescriptors.log.fine("Remote device described (without services) notifying listeners: " + describedDevice);
            notifiedStart = this.getUpnpService().getRegistry().notifyDiscoveryStart(describedDevice);
            RetrieveRemoteDescriptors.log.fine("Hydrating described device's services: " + describedDevice);
            final RemoteDevice hydratedDevice = this.describeServices(describedDevice);
            if (hydratedDevice == null) {
                if (!this.errorsAlreadyLogged.contains(((Device<RemoteDeviceIdentity, D, S>)this.rd).getIdentity().getUdn())) {
                    this.errorsAlreadyLogged.add(((Device<RemoteDeviceIdentity, D, S>)this.rd).getIdentity().getUdn());
                    RetrieveRemoteDescriptors.log.warning("Device service description failed: " + this.rd);
                }
                if (notifiedStart) {
                    this.getUpnpService().getRegistry().notifyDiscoveryFailure(describedDevice, new DescriptorBindingException("Device service description failed: " + this.rd));
                }
                return;
            }
            RetrieveRemoteDescriptors.log.fine("Adding fully hydrated remote device to registry: " + hydratedDevice);
            this.getUpnpService().getRegistry().addDevice(hydratedDevice);
        }
        catch (ValidationException ex) {
            if (!this.errorsAlreadyLogged.contains(((Device<RemoteDeviceIdentity, D, S>)this.rd).getIdentity().getUdn())) {
                this.errorsAlreadyLogged.add(((Device<RemoteDeviceIdentity, D, S>)this.rd).getIdentity().getUdn());
                RetrieveRemoteDescriptors.log.warning("Could not validate device model: " + this.rd);
                for (final ValidationError validationError : ex.getErrors()) {
                    RetrieveRemoteDescriptors.log.warning(validationError.toString());
                }
                if (describedDevice != null && notifiedStart) {
                    this.getUpnpService().getRegistry().notifyDiscoveryFailure(describedDevice, ex);
                }
            }
        }
        catch (DescriptorBindingException ex2) {
            RetrieveRemoteDescriptors.log.warning("Could not hydrate device or its services from descriptor: " + this.rd);
            RetrieveRemoteDescriptors.log.warning("Cause was: " + Exceptions.unwrap(ex2));
            if (describedDevice != null && notifiedStart) {
                this.getUpnpService().getRegistry().notifyDiscoveryFailure(describedDevice, ex2);
            }
        }
        catch (RegistrationException ex3) {
            RetrieveRemoteDescriptors.log.warning("Adding hydrated device to registry failed: " + this.rd);
            RetrieveRemoteDescriptors.log.warning("Cause was: " + ex3.toString());
            if (describedDevice != null && notifiedStart) {
                this.getUpnpService().getRegistry().notifyDiscoveryFailure(describedDevice, ex3);
            }
        }
    }
    
    protected RemoteDevice describeServices(final RemoteDevice currentDevice) throws RouterException, DescriptorBindingException, ValidationException {
        final List<RemoteService> describedServices = new ArrayList<RemoteService>();
        if (currentDevice.hasServices()) {
            final List<RemoteService> filteredServices = this.filterExclusiveServices(currentDevice.getServices());
            for (final RemoteService service : filteredServices) {
                final RemoteService svc = this.describeService(service);
                if (svc != null) {
                    describedServices.add(svc);
                }
                else {
                    RetrieveRemoteDescriptors.log.warning("Skipping invalid service '" + service + "' of: " + currentDevice);
                }
            }
        }
        final List<RemoteDevice> describedEmbeddedDevices = new ArrayList<RemoteDevice>();
        if (currentDevice.hasEmbeddedDevices()) {
            for (final RemoteDevice embeddedDevice : currentDevice.getEmbeddedDevices()) {
                if (embeddedDevice != null) {
                    final RemoteDevice describedEmbeddedDevice = this.describeServices(embeddedDevice);
                    if (describedEmbeddedDevice != null) {
                        describedEmbeddedDevices.add(describedEmbeddedDevice);
                    }
                }
            }
        }
        final Icon[] iconDupes = new Icon[currentDevice.getIcons().length];
        for (int i = 0; i < currentDevice.getIcons().length; ++i) {
            final Icon icon = currentDevice.getIcons()[i];
            iconDupes[i] = icon.deepCopy();
        }
        return currentDevice.newInstance(((Device<RemoteDeviceIdentity, D, S>)currentDevice).getIdentity().getUdn(), currentDevice.getVersion(), currentDevice.getType(), currentDevice.getDetails(), iconDupes, currentDevice.toServiceArray(describedServices), describedEmbeddedDevices);
    }
    
    protected RemoteService describeService(final RemoteService service) throws RouterException, DescriptorBindingException, ValidationException {
        URL descriptorURL;
        try {
            descriptorURL = ((Service<RemoteDevice, S>)service).getDevice().normalizeURI(service.getDescriptorURI());
        }
        catch (IllegalArgumentException e) {
            RetrieveRemoteDescriptors.log.warning("Could not normalize service descriptor URL: " + service.getDescriptorURI());
            return null;
        }
        final StreamRequestMessage serviceDescRetrievalMsg = new StreamRequestMessage(UpnpRequest.Method.GET, descriptorURL);
        final UpnpHeaders headers = this.getUpnpService().getConfiguration().getDescriptorRetrievalHeaders(((Device<RemoteDeviceIdentity, D, S>)((Service<RemoteDevice, S>)service).getDevice()).getIdentity());
        if (headers != null) {
            serviceDescRetrievalMsg.getHeaders().putAll(headers);
        }
        RetrieveRemoteDescriptors.log.fine("Sending service descriptor retrieval message: " + serviceDescRetrievalMsg);
        final StreamResponseMessage serviceDescMsg = this.getUpnpService().getRouter().send(serviceDescRetrievalMsg);
        if (serviceDescMsg == null) {
            RetrieveRemoteDescriptors.log.warning("Could not retrieve service descriptor, no response: " + service);
            return null;
        }
        if (serviceDescMsg.getOperation().isFailed()) {
            RetrieveRemoteDescriptors.log.warning("Service descriptor retrieval failed: " + descriptorURL + ", " + serviceDescMsg.getOperation().getResponseDetails());
            return null;
        }
        if (!serviceDescMsg.isContentTypeTextUDA()) {
            RetrieveRemoteDescriptors.log.fine("Received service descriptor without or with invalid Content-Type: " + descriptorURL);
        }
        final String descriptorContent = serviceDescMsg.getBodyString();
        if (descriptorContent == null || descriptorContent.length() == 0) {
            RetrieveRemoteDescriptors.log.warning("Received empty service descriptor:" + descriptorURL);
            return null;
        }
        RetrieveRemoteDescriptors.log.fine("Received service descriptor, hydrating service model: " + serviceDescMsg);
        final ServiceDescriptorBinder serviceDescriptorBinder = this.getUpnpService().getConfiguration().getServiceDescriptorBinderUDA10();
        return serviceDescriptorBinder.describe(service, descriptorContent);
    }
    
    protected List<RemoteService> filterExclusiveServices(final RemoteService[] services) {
        final ServiceType[] exclusiveTypes = this.getUpnpService().getConfiguration().getExclusiveServiceTypes();
        if (exclusiveTypes == null || exclusiveTypes.length == 0) {
            return Arrays.asList(services);
        }
        final List<RemoteService> exclusiveServices = new ArrayList<RemoteService>();
        for (final RemoteService discoveredService : services) {
            for (final ServiceType exclusiveType : exclusiveTypes) {
                if (discoveredService.getServiceType().implementsVersion(exclusiveType)) {
                    RetrieveRemoteDescriptors.log.fine("Including exclusive service: " + discoveredService);
                    exclusiveServices.add(discoveredService);
                }
                else {
                    RetrieveRemoteDescriptors.log.fine("Excluding unwanted service: " + exclusiveType);
                }
            }
        }
        return exclusiveServices;
    }
    
    static {
        log = Logger.getLogger(RetrieveRemoteDescriptors.class.getName());
        activeRetrievals = new CopyOnWriteArraySet<URL>();
    }
}
