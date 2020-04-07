// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.discovery;

import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.message.header.UDNHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.Location;
import org.fourthline.cling.model.message.IncomingDatagramMessage;

public class OutgoingSearchResponseUDN extends OutgoingSearchResponse
{
    public OutgoingSearchResponseUDN(final IncomingDatagramMessage request, final Location location, final LocalDevice device) {
        super(request, location, device);
        this.getHeaders().add(UpnpHeader.Type.ST, new UDNHeader(((Device<DeviceIdentity, D, S>)device).getIdentity().getUdn()));
        this.getHeaders().add(UpnpHeader.Type.USN, new UDNHeader(((Device<DeviceIdentity, D, S>)device).getIdentity().getUdn()));
    }
}
