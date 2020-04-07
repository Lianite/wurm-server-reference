// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.discovery;

import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.message.header.InterfaceMacHeader;
import org.fourthline.cling.model.message.header.USNRootDeviceHeader;
import org.fourthline.cling.model.message.header.RootDeviceHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.types.NotificationSubtype;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.Location;

public class OutgoingNotificationRequestRootDevice extends OutgoingNotificationRequest
{
    public OutgoingNotificationRequestRootDevice(final Location location, final LocalDevice device, final NotificationSubtype type) {
        super(location, device, type);
        this.getHeaders().add(UpnpHeader.Type.NT, new RootDeviceHeader());
        this.getHeaders().add(UpnpHeader.Type.USN, new USNRootDeviceHeader(((Device<DeviceIdentity, D, S>)device).getIdentity().getUdn()));
        if ("true".equals(System.getProperty("org.fourthline.cling.network.announceMACAddress")) && location.getNetworkAddress().getHardwareAddress() != null) {
            this.getHeaders().add(UpnpHeader.Type.EXT_IFACE_MAC, new InterfaceMacHeader(location.getNetworkAddress().getHardwareAddress()));
        }
    }
}
