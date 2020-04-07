// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.protocol.async;

import org.fourthline.cling.model.meta.DeviceIdentity;
import java.util.logging.Level;
import org.fourthline.cling.model.DiscoveryOptions;
import org.fourthline.cling.model.Location;
import java.util.Collection;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.message.discovery.OutgoingSearchResponseServiceType;
import org.fourthline.cling.model.message.discovery.OutgoingSearchResponseDeviceType;
import org.fourthline.cling.model.message.discovery.OutgoingSearchResponseUDN;
import org.fourthline.cling.model.message.discovery.OutgoingSearchResponseRootDevice;
import java.util.ArrayList;
import org.fourthline.cling.model.message.OutgoingDatagramMessage;
import org.fourthline.cling.model.message.discovery.OutgoingSearchResponse;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.message.header.ServiceTypeHeader;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.message.header.DeviceTypeHeader;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.model.message.header.UDNHeader;
import org.fourthline.cling.model.message.header.RootDeviceHeader;
import org.fourthline.cling.model.message.header.STAllHeader;
import org.fourthline.cling.model.message.header.MXHeader;
import org.fourthline.cling.transport.RouterException;
import java.util.Iterator;
import java.util.List;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.NetworkAddress;
import org.fourthline.cling.model.message.UpnpRequest;
import org.fourthline.cling.model.message.IncomingDatagramMessage;
import org.fourthline.cling.UpnpService;
import java.util.Random;
import java.util.logging.Logger;
import org.fourthline.cling.model.message.discovery.IncomingSearchRequest;
import org.fourthline.cling.protocol.ReceivingAsync;

public class ReceivingSearch extends ReceivingAsync<IncomingSearchRequest>
{
    private static final Logger log;
    private static final boolean LOG_ENABLED;
    protected final Random randomGenerator;
    
    public ReceivingSearch(final UpnpService upnpService, final IncomingDatagramMessage<UpnpRequest> inputMessage) {
        super(upnpService, new IncomingSearchRequest(inputMessage));
        this.randomGenerator = new Random();
    }
    
    @Override
    protected void execute() throws RouterException {
        if (this.getUpnpService().getRouter() == null) {
            ReceivingSearch.log.fine("Router hasn't completed initialization, ignoring received search message");
            return;
        }
        if (!this.getInputMessage().isMANSSDPDiscover()) {
            ReceivingSearch.log.fine("Invalid search request, no or invalid MAN ssdp:discover header: " + ((ReceivingAsync<Object>)this).getInputMessage());
            return;
        }
        final UpnpHeader searchTarget = this.getInputMessage().getSearchTarget();
        if (searchTarget == null) {
            ReceivingSearch.log.fine("Invalid search request, did not contain ST header: " + ((ReceivingAsync<Object>)this).getInputMessage());
            return;
        }
        final List<NetworkAddress> activeStreamServers = this.getUpnpService().getRouter().getActiveStreamServers(this.getInputMessage().getLocalAddress());
        if (activeStreamServers.size() == 0) {
            ReceivingSearch.log.fine("Aborting search response, no active stream servers found (network disabled?)");
            return;
        }
        for (final NetworkAddress activeStreamServer : activeStreamServers) {
            this.sendResponses(searchTarget, activeStreamServer);
        }
    }
    
    @Override
    protected boolean waitBeforeExecution() throws InterruptedException {
        Integer mx = this.getInputMessage().getMX();
        if (mx == null) {
            ReceivingSearch.log.fine("Invalid search request, did not contain MX header: " + ((ReceivingAsync<Object>)this).getInputMessage());
            return false;
        }
        if (mx > 120 || mx <= 0) {
            mx = MXHeader.DEFAULT_VALUE;
        }
        if (this.getUpnpService().getRegistry().getLocalDevices().size() > 0) {
            final int sleepTime = this.randomGenerator.nextInt(mx * 1000);
            ReceivingSearch.log.fine("Sleeping " + sleepTime + " milliseconds to avoid flooding with search responses");
            Thread.sleep(sleepTime);
        }
        return true;
    }
    
    protected void sendResponses(final UpnpHeader searchTarget, final NetworkAddress activeStreamServer) throws RouterException {
        if (searchTarget instanceof STAllHeader) {
            this.sendSearchResponseAll(activeStreamServer);
        }
        else if (searchTarget instanceof RootDeviceHeader) {
            this.sendSearchResponseRootDevices(activeStreamServer);
        }
        else if (searchTarget instanceof UDNHeader) {
            this.sendSearchResponseUDN(searchTarget.getValue(), activeStreamServer);
        }
        else if (searchTarget instanceof DeviceTypeHeader) {
            this.sendSearchResponseDeviceType((DeviceType)searchTarget.getValue(), activeStreamServer);
        }
        else if (searchTarget instanceof ServiceTypeHeader) {
            this.sendSearchResponseServiceType((ServiceType)searchTarget.getValue(), activeStreamServer);
        }
        else {
            ReceivingSearch.log.warning("Non-implemented search request target: " + searchTarget.getClass());
        }
    }
    
    protected void sendSearchResponseAll(final NetworkAddress activeStreamServer) throws RouterException {
        if (ReceivingSearch.LOG_ENABLED) {
            ReceivingSearch.log.fine("Responding to 'all' search with advertisement messages for all local devices");
        }
        for (final LocalDevice localDevice : this.getUpnpService().getRegistry().getLocalDevices()) {
            if (this.isAdvertisementDisabled(localDevice)) {
                continue;
            }
            if (ReceivingSearch.LOG_ENABLED) {
                ReceivingSearch.log.finer("Sending root device messages: " + localDevice);
            }
            final List<OutgoingSearchResponse> rootDeviceMsgs = this.createDeviceMessages(localDevice, activeStreamServer);
            for (final OutgoingSearchResponse upnpMessage : rootDeviceMsgs) {
                this.getUpnpService().getRouter().send(upnpMessage);
            }
            if (localDevice.hasEmbeddedDevices()) {
                for (final LocalDevice embeddedDevice : ((Device<DI, LocalDevice, S>)localDevice).findEmbeddedDevices()) {
                    if (ReceivingSearch.LOG_ENABLED) {
                        ReceivingSearch.log.finer("Sending embedded device messages: " + embeddedDevice);
                    }
                    final List<OutgoingSearchResponse> embeddedDeviceMsgs = this.createDeviceMessages(embeddedDevice, activeStreamServer);
                    for (final OutgoingSearchResponse upnpMessage2 : embeddedDeviceMsgs) {
                        this.getUpnpService().getRouter().send(upnpMessage2);
                    }
                }
            }
            final List<OutgoingSearchResponse> serviceTypeMsgs = this.createServiceTypeMessages(localDevice, activeStreamServer);
            if (serviceTypeMsgs.size() <= 0) {
                continue;
            }
            if (ReceivingSearch.LOG_ENABLED) {
                ReceivingSearch.log.finer("Sending service type messages");
            }
            for (final OutgoingSearchResponse upnpMessage3 : serviceTypeMsgs) {
                this.getUpnpService().getRouter().send(upnpMessage3);
            }
        }
    }
    
    protected List<OutgoingSearchResponse> createDeviceMessages(final LocalDevice device, final NetworkAddress activeStreamServer) {
        final List<OutgoingSearchResponse> msgs = new ArrayList<OutgoingSearchResponse>();
        if (device.isRoot()) {
            msgs.add(new OutgoingSearchResponseRootDevice(((ReceivingAsync<IncomingDatagramMessage>)this).getInputMessage(), this.getDescriptorLocation(activeStreamServer, device), device));
        }
        msgs.add(new OutgoingSearchResponseUDN(((ReceivingAsync<IncomingDatagramMessage>)this).getInputMessage(), this.getDescriptorLocation(activeStreamServer, device), device));
        msgs.add(new OutgoingSearchResponseDeviceType(((ReceivingAsync<IncomingDatagramMessage>)this).getInputMessage(), this.getDescriptorLocation(activeStreamServer, device), device));
        for (final OutgoingSearchResponse msg : msgs) {
            this.prepareOutgoingSearchResponse(msg);
        }
        return msgs;
    }
    
    protected List<OutgoingSearchResponse> createServiceTypeMessages(final LocalDevice device, final NetworkAddress activeStreamServer) {
        final List<OutgoingSearchResponse> msgs = new ArrayList<OutgoingSearchResponse>();
        for (final ServiceType serviceType : device.findServiceTypes()) {
            final OutgoingSearchResponse message = new OutgoingSearchResponseServiceType(((ReceivingAsync<IncomingDatagramMessage>)this).getInputMessage(), this.getDescriptorLocation(activeStreamServer, device), device, serviceType);
            this.prepareOutgoingSearchResponse(message);
            msgs.add(message);
        }
        return msgs;
    }
    
    protected void sendSearchResponseRootDevices(final NetworkAddress activeStreamServer) throws RouterException {
        ReceivingSearch.log.fine("Responding to root device search with advertisement messages for all local root devices");
        for (final LocalDevice device : this.getUpnpService().getRegistry().getLocalDevices()) {
            if (this.isAdvertisementDisabled(device)) {
                continue;
            }
            final OutgoingSearchResponse message = new OutgoingSearchResponseRootDevice(((ReceivingAsync<IncomingDatagramMessage>)this).getInputMessage(), this.getDescriptorLocation(activeStreamServer, device), device);
            this.prepareOutgoingSearchResponse(message);
            this.getUpnpService().getRouter().send(message);
        }
    }
    
    protected void sendSearchResponseUDN(final UDN udn, final NetworkAddress activeStreamServer) throws RouterException {
        final Device device = this.getUpnpService().getRegistry().getDevice(udn, false);
        if (device != null && device instanceof LocalDevice) {
            if (this.isAdvertisementDisabled((LocalDevice)device)) {
                return;
            }
            ReceivingSearch.log.fine("Responding to UDN device search: " + udn);
            final OutgoingSearchResponse message = new OutgoingSearchResponseUDN(((ReceivingAsync<IncomingDatagramMessage>)this).getInputMessage(), this.getDescriptorLocation(activeStreamServer, (LocalDevice)device), (LocalDevice)device);
            this.prepareOutgoingSearchResponse(message);
            this.getUpnpService().getRouter().send(message);
        }
    }
    
    protected void sendSearchResponseDeviceType(final DeviceType deviceType, final NetworkAddress activeStreamServer) throws RouterException {
        ReceivingSearch.log.fine("Responding to device type search: " + deviceType);
        final Collection<Device> devices = this.getUpnpService().getRegistry().getDevices(deviceType);
        for (final Device device : devices) {
            if (device instanceof LocalDevice) {
                if (this.isAdvertisementDisabled((LocalDevice)device)) {
                    continue;
                }
                ReceivingSearch.log.finer("Sending matching device type search result for: " + device);
                final OutgoingSearchResponse message = new OutgoingSearchResponseDeviceType(((ReceivingAsync<IncomingDatagramMessage>)this).getInputMessage(), this.getDescriptorLocation(activeStreamServer, (LocalDevice)device), (LocalDevice)device);
                this.prepareOutgoingSearchResponse(message);
                this.getUpnpService().getRouter().send(message);
            }
        }
    }
    
    protected void sendSearchResponseServiceType(final ServiceType serviceType, final NetworkAddress activeStreamServer) throws RouterException {
        ReceivingSearch.log.fine("Responding to service type search: " + serviceType);
        final Collection<Device> devices = this.getUpnpService().getRegistry().getDevices(serviceType);
        for (final Device device : devices) {
            if (device instanceof LocalDevice) {
                if (this.isAdvertisementDisabled((LocalDevice)device)) {
                    continue;
                }
                ReceivingSearch.log.finer("Sending matching service type search result: " + device);
                final OutgoingSearchResponse message = new OutgoingSearchResponseServiceType(((ReceivingAsync<IncomingDatagramMessage>)this).getInputMessage(), this.getDescriptorLocation(activeStreamServer, (LocalDevice)device), (LocalDevice)device, serviceType);
                this.prepareOutgoingSearchResponse(message);
                this.getUpnpService().getRouter().send(message);
            }
        }
    }
    
    protected Location getDescriptorLocation(final NetworkAddress activeStreamServer, final LocalDevice device) {
        return new Location(activeStreamServer, this.getUpnpService().getConfiguration().getNamespace().getDescriptorPathString(device));
    }
    
    protected boolean isAdvertisementDisabled(final LocalDevice device) {
        final DiscoveryOptions options = this.getUpnpService().getRegistry().getDiscoveryOptions(((Device<DeviceIdentity, D, S>)device).getIdentity().getUdn());
        return options != null && !options.isAdvertised();
    }
    
    protected void prepareOutgoingSearchResponse(final OutgoingSearchResponse message) {
    }
    
    static {
        log = Logger.getLogger(ReceivingSearch.class.getName());
        LOG_ENABLED = ReceivingSearch.log.isLoggable(Level.FINE);
    }
}
