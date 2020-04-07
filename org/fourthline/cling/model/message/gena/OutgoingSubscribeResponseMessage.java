// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.gena;

import org.fourthline.cling.model.message.header.TimeoutHeader;
import org.fourthline.cling.model.message.header.SubscriptionIdHeader;
import org.fourthline.cling.model.message.header.ServerHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.gena.LocalGENASubscription;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.StreamResponseMessage;

public class OutgoingSubscribeResponseMessage extends StreamResponseMessage
{
    public OutgoingSubscribeResponseMessage(final UpnpResponse.Status status) {
        super(status);
    }
    
    public OutgoingSubscribeResponseMessage(final LocalGENASubscription subscription) {
        super(new UpnpResponse(UpnpResponse.Status.OK));
        this.getHeaders().add(UpnpHeader.Type.SERVER, new ServerHeader());
        this.getHeaders().add(UpnpHeader.Type.SID, new SubscriptionIdHeader(subscription.getSubscriptionId()));
        this.getHeaders().add(UpnpHeader.Type.TIMEOUT, new TimeoutHeader(subscription.getActualDurationSeconds()));
    }
}
