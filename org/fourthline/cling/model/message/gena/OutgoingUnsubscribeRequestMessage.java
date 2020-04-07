// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.gena;

import java.util.List;
import java.util.Map;
import org.fourthline.cling.model.message.header.SubscriptionIdHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.message.UpnpRequest;
import org.fourthline.cling.model.message.UpnpHeaders;
import org.fourthline.cling.model.gena.RemoteGENASubscription;
import org.fourthline.cling.model.message.StreamRequestMessage;

public class OutgoingUnsubscribeRequestMessage extends StreamRequestMessage
{
    public OutgoingUnsubscribeRequestMessage(final RemoteGENASubscription subscription, final UpnpHeaders extraHeaders) {
        super(UpnpRequest.Method.UNSUBSCRIBE, subscription.getEventSubscriptionURL());
        this.getHeaders().add(UpnpHeader.Type.SID, new SubscriptionIdHeader(subscription.getSubscriptionId()));
        if (extraHeaders != null) {
            this.getHeaders().putAll(extraHeaders);
        }
    }
}
