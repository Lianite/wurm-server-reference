// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.gena;

import java.util.Map;
import org.fourthline.cling.model.message.header.TimeoutHeader;
import org.fourthline.cling.model.message.header.NTEventHeader;
import org.fourthline.cling.model.message.header.CallbackHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.message.UpnpRequest;
import org.fourthline.cling.model.message.UpnpHeaders;
import java.net.URL;
import java.util.List;
import org.fourthline.cling.model.gena.RemoteGENASubscription;
import org.fourthline.cling.model.message.StreamRequestMessage;

public class OutgoingSubscribeRequestMessage extends StreamRequestMessage
{
    public OutgoingSubscribeRequestMessage(final RemoteGENASubscription subscription, final List<URL> callbackURLs, final UpnpHeaders extraHeaders) {
        super(UpnpRequest.Method.SUBSCRIBE, subscription.getEventSubscriptionURL());
        this.getHeaders().add(UpnpHeader.Type.CALLBACK, new CallbackHeader(callbackURLs));
        this.getHeaders().add(UpnpHeader.Type.NT, new NTEventHeader());
        this.getHeaders().add(UpnpHeader.Type.TIMEOUT, new TimeoutHeader(subscription.getRequestedDurationSeconds()));
        if (extraHeaders != null) {
            this.getHeaders().putAll(extraHeaders);
        }
    }
    
    public boolean hasCallbackURLs() {
        final CallbackHeader callbackHeader = this.getHeaders().getFirstHeader(UpnpHeader.Type.CALLBACK, CallbackHeader.class);
        return ((UpnpHeader<List>)callbackHeader).getValue().size() > 0;
    }
}
