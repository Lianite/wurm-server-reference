// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.protocol.async;

import org.fourthline.cling.model.types.NotificationSubtype;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.message.discovery.OutgoingNotificationRequestServiceType;
import org.fourthline.cling.model.message.discovery.OutgoingNotificationRequestDeviceType;
import org.fourthline.cling.model.message.discovery.OutgoingNotificationRequestUDN;
import org.fourthline.cling.model.message.discovery.OutgoingNotificationRequestRootDevice;
import org.fourthline.cling.model.message.OutgoingDatagramMessage;
import org.fourthline.cling.model.message.discovery.OutgoingNotificationRequest;
import org.fourthline.cling.transport.RouterException;
import java.util.Iterator;
import java.util.List;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.NetworkAddress;
import org.fourthline.cling.model.Location;
import java.util.ArrayList;
import java.net.InetAddress;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.meta.LocalDevice;
import java.util.logging.Logger;
import org.fourthline.cling.protocol.SendingAsync;

public abstract class SendingNotification extends SendingAsync
{
    private static final Logger log;
    private LocalDevice device;
    
    public SendingNotification(final UpnpService upnpService, final LocalDevice device) {
        super(upnpService);
        this.device = device;
    }
    
    public LocalDevice getDevice() {
        return this.device;
    }
    
    @Override
    protected void execute() throws RouterException {
        final List<NetworkAddress> activeStreamServers = this.getUpnpService().getRouter().getActiveStreamServers(null);
        if (activeStreamServers.size() == 0) {
            SendingNotification.log.fine("Aborting notifications, no active stream servers found (network disabled?)");
            return;
        }
        final List<Location> descriptorLocations = new ArrayList<Location>();
        for (final NetworkAddress activeStreamServer : activeStreamServers) {
            descriptorLocations.add(new Location(activeStreamServer, this.getUpnpService().getConfiguration().getNamespace().getDescriptorPathString(this.getDevice())));
        }
        for (int i = 0; i < this.getBulkRepeat(); ++i) {
            try {
                for (final Location descriptorLocation : descriptorLocations) {
                    this.sendMessages(descriptorLocation);
                }
                SendingNotification.log.finer("Sleeping " + this.getBulkIntervalMilliseconds() + " milliseconds");
                Thread.sleep(this.getBulkIntervalMilliseconds());
            }
            catch (InterruptedException ex) {
                SendingNotification.log.warning("Advertisement thread was interrupted: " + ex);
            }
        }
    }
    
    protected int getBulkRepeat() {
        return 3;
    }
    
    protected int getBulkIntervalMilliseconds() {
        return 150;
    }
    
    public void sendMessages(final Location descriptorLocation) throws RouterException {
        SendingNotification.log.finer("Sending root device messages: " + this.getDevice());
        final List<OutgoingNotificationRequest> rootDeviceMsgs = this.createDeviceMessages(this.getDevice(), descriptorLocation);
        for (final OutgoingNotificationRequest upnpMessage : rootDeviceMsgs) {
            this.getUpnpService().getRouter().send(upnpMessage);
        }
        if (this.getDevice().hasEmbeddedDevices()) {
            for (final LocalDevice embeddedDevice : ((Device<DI, LocalDevice, S>)this.getDevice()).findEmbeddedDevices()) {
                SendingNotification.log.finer("Sending embedded device messages: " + embeddedDevice);
                final List<OutgoingNotificationRequest> embeddedDeviceMsgs = this.createDeviceMessages(embeddedDevice, descriptorLocation);
                for (final OutgoingNotificationRequest upnpMessage2 : embeddedDeviceMsgs) {
                    this.getUpnpService().getRouter().send(upnpMessage2);
                }
            }
        }
        final List<OutgoingNotificationRequest> serviceTypeMsgs = this.createServiceTypeMessages(this.getDevice(), descriptorLocation);
        if (serviceTypeMsgs.size() > 0) {
            SendingNotification.log.finer("Sending service type messages");
            for (final OutgoingNotificationRequest upnpMessage3 : serviceTypeMsgs) {
                this.getUpnpService().getRouter().send(upnpMessage3);
            }
        }
    }
    
    protected List<OutgoingNotificationRequest> createDeviceMessages(final LocalDevice device, final Location descriptorLocation) {
        final List<OutgoingNotificationRequest> msgs = new ArrayList<OutgoingNotificationRequest>();
        if (device.isRoot()) {
            msgs.add(new OutgoingNotificationRequestRootDevice(descriptorLocation, device, this.getNotificationSubtype()));
        }
        msgs.add(new OutgoingNotificationRequestUDN(descriptorLocation, device, this.getNotificationSubtype()));
        msgs.add(new OutgoingNotificationRequestDeviceType(descriptorLocation, device, this.getNotificationSubtype()));
        return msgs;
    }
    
    protected List<OutgoingNotificationRequest> createServiceTypeMessages(final LocalDevice device, final Location descriptorLocation) {
        final List<OutgoingNotificationRequest> msgs = new ArrayList<OutgoingNotificationRequest>();
        for (final ServiceType serviceType : device.findServiceTypes()) {
            msgs.add(new OutgoingNotificationRequestServiceType(descriptorLocation, device, this.getNotificationSubtype(), serviceType));
        }
        return msgs;
    }
    
    protected abstract NotificationSubtype getNotificationSubtype();
    
    static {
        log = Logger.getLogger(SendingNotification.class.getName());
    }
}
