// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.discovery;

import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.message.header.InterfaceMacHeader;
import org.fourthline.cling.model.message.header.EXTHeader;
import org.fourthline.cling.model.message.header.ServerHeader;
import org.fourthline.cling.model.message.header.LocationHeader;
import org.fourthline.cling.model.message.header.MaxAgeHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.Location;
import org.fourthline.cling.model.message.IncomingDatagramMessage;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.OutgoingDatagramMessage;

public class OutgoingSearchResponse extends OutgoingDatagramMessage<UpnpResponse>
{
    public OutgoingSearchResponse(final IncomingDatagramMessage request, final Location location, final LocalDevice device) {
        super(new UpnpResponse(UpnpResponse.Status.OK), request.getSourceAddress(), request.getSourcePort());
        this.getHeaders().add(UpnpHeader.Type.MAX_AGE, new MaxAgeHeader(((Device<DeviceIdentity, D, S>)device).getIdentity().getMaxAgeSeconds()));
        this.getHeaders().add(UpnpHeader.Type.LOCATION, new LocationHeader(location.getURL()));
        this.getHeaders().add(UpnpHeader.Type.SERVER, new ServerHeader());
        this.getHeaders().add(UpnpHeader.Type.EXT, new EXTHeader());
        if ("true".equals(System.getProperty("org.fourthline.cling.network.announceMACAddress")) && location.getNetworkAddress().getHardwareAddress() != null) {
            this.getHeaders().add(UpnpHeader.Type.EXT_IFACE_MAC, new InterfaceMacHeader(location.getNetworkAddress().getHardwareAddress()));
        }
    }
}
