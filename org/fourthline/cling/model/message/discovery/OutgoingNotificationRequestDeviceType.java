// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.discovery;

import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.message.header.DeviceUSNHeader;
import org.fourthline.cling.model.message.header.DeviceTypeHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.types.NotificationSubtype;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.Location;

public class OutgoingNotificationRequestDeviceType extends OutgoingNotificationRequest
{
    public OutgoingNotificationRequestDeviceType(final Location location, final LocalDevice device, final NotificationSubtype type) {
        super(location, device, type);
        this.getHeaders().add(UpnpHeader.Type.NT, new DeviceTypeHeader(device.getType()));
        this.getHeaders().add(UpnpHeader.Type.USN, new DeviceUSNHeader(((Device<DeviceIdentity, D, S>)device).getIdentity().getUdn(), device.getType()));
    }
}
