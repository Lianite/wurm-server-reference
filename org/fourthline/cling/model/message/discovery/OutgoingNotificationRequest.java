// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.discovery;

import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.message.header.NTSHeader;
import org.fourthline.cling.model.message.header.HostHeader;
import org.fourthline.cling.model.message.header.ServerHeader;
import org.fourthline.cling.model.message.header.LocationHeader;
import org.fourthline.cling.model.message.header.MaxAgeHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.Location;
import org.fourthline.cling.model.types.NotificationSubtype;
import org.fourthline.cling.model.message.UpnpRequest;
import org.fourthline.cling.model.message.OutgoingDatagramMessage;

public abstract class OutgoingNotificationRequest extends OutgoingDatagramMessage<UpnpRequest>
{
    private NotificationSubtype type;
    
    protected OutgoingNotificationRequest(final Location location, final LocalDevice device, final NotificationSubtype type) {
        super(new UpnpRequest(UpnpRequest.Method.NOTIFY), ModelUtil.getInetAddressByName("239.255.255.250"), 1900);
        this.type = type;
        this.getHeaders().add(UpnpHeader.Type.MAX_AGE, new MaxAgeHeader(((Device<DeviceIdentity, D, S>)device).getIdentity().getMaxAgeSeconds()));
        this.getHeaders().add(UpnpHeader.Type.LOCATION, new LocationHeader(location.getURL()));
        this.getHeaders().add(UpnpHeader.Type.SERVER, new ServerHeader());
        this.getHeaders().add(UpnpHeader.Type.HOST, new HostHeader());
        this.getHeaders().add(UpnpHeader.Type.NTS, new NTSHeader(type));
    }
    
    public NotificationSubtype getType() {
        return this.type;
    }
}
