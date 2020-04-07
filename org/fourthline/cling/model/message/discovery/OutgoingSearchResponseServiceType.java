// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.discovery;

import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.message.header.ServiceUSNHeader;
import org.fourthline.cling.model.message.header.ServiceTypeHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.Location;
import org.fourthline.cling.model.message.IncomingDatagramMessage;

public class OutgoingSearchResponseServiceType extends OutgoingSearchResponse
{
    public OutgoingSearchResponseServiceType(final IncomingDatagramMessage request, final Location location, final LocalDevice device, final ServiceType serviceType) {
        super(request, location, device);
        this.getHeaders().add(UpnpHeader.Type.ST, new ServiceTypeHeader(serviceType));
        this.getHeaders().add(UpnpHeader.Type.USN, new ServiceUSNHeader(((Device<DeviceIdentity, D, S>)device).getIdentity().getUdn(), serviceType));
    }
}
