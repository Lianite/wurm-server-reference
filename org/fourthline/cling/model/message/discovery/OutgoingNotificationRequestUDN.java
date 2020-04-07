// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.discovery;

import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.message.header.UDNHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.types.NotificationSubtype;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.Location;

public class OutgoingNotificationRequestUDN extends OutgoingNotificationRequest
{
    public OutgoingNotificationRequestUDN(final Location location, final LocalDevice device, final NotificationSubtype type) {
        super(location, device, type);
        this.getHeaders().add(UpnpHeader.Type.NT, new UDNHeader(((Device<DeviceIdentity, D, S>)device).getIdentity().getUdn()));
        this.getHeaders().add(UpnpHeader.Type.USN, new UDNHeader(((Device<DeviceIdentity, D, S>)device).getIdentity().getUdn()));
    }
}
