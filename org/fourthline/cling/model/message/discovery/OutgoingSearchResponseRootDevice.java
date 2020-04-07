// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.discovery;

import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.message.header.USNRootDeviceHeader;
import org.fourthline.cling.model.message.header.RootDeviceHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.Location;
import org.fourthline.cling.model.message.IncomingDatagramMessage;

public class OutgoingSearchResponseRootDevice extends OutgoingSearchResponse
{
    public OutgoingSearchResponseRootDevice(final IncomingDatagramMessage request, final Location location, final LocalDevice device) {
        super(request, location, device);
        this.getHeaders().add(UpnpHeader.Type.ST, new RootDeviceHeader());
        this.getHeaders().add(UpnpHeader.Type.USN, new USNRootDeviceHeader(((Device<DeviceIdentity, D, S>)device).getIdentity().getUdn()));
    }
}
