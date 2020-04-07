// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.gena;

import org.fourthline.cling.model.message.header.SubscriptionIdHeader;
import org.fourthline.cling.model.message.header.TimeoutHeader;
import org.fourthline.cling.model.message.header.NTEventHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.message.header.CallbackHeader;
import java.net.URL;
import java.util.List;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.message.StreamRequestMessage;

public class IncomingSubscribeRequestMessage extends StreamRequestMessage
{
    private final LocalService service;
    
    public IncomingSubscribeRequestMessage(final StreamRequestMessage source, final LocalService service) {
        super(source);
        this.service = service;
    }
    
    public LocalService getService() {
        return this.service;
    }
    
    public List<URL> getCallbackURLs() {
        final CallbackHeader header = this.getHeaders().getFirstHeader(UpnpHeader.Type.CALLBACK, CallbackHeader.class);
        return (header != null) ? header.getValue() : null;
    }
    
    public boolean hasNotificationHeader() {
        return this.getHeaders().getFirstHeader(UpnpHeader.Type.NT, NTEventHeader.class) != null;
    }
    
    public Integer getRequestedTimeoutSeconds() {
        final TimeoutHeader timeoutHeader = this.getHeaders().getFirstHeader(UpnpHeader.Type.TIMEOUT, TimeoutHeader.class);
        return (timeoutHeader != null) ? timeoutHeader.getValue() : null;
    }
    
    public String getSubscriptionId() {
        final SubscriptionIdHeader header = this.getHeaders().getFirstHeader(UpnpHeader.Type.SID, SubscriptionIdHeader.class);
        return (header != null) ? header.getValue() : null;
    }
}
