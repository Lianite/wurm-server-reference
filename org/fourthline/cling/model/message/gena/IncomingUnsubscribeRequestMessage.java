// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.gena;

import org.fourthline.cling.model.message.header.SubscriptionIdHeader;
import org.fourthline.cling.model.message.header.NTEventHeader;
import org.fourthline.cling.model.message.header.CallbackHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.message.StreamRequestMessage;

public class IncomingUnsubscribeRequestMessage extends StreamRequestMessage
{
    private final LocalService service;
    
    public IncomingUnsubscribeRequestMessage(final StreamRequestMessage source, final LocalService service) {
        super(source);
        this.service = service;
    }
    
    public LocalService getService() {
        return this.service;
    }
    
    public boolean hasCallbackHeader() {
        return this.getHeaders().getFirstHeader(UpnpHeader.Type.CALLBACK, CallbackHeader.class) != null;
    }
    
    public boolean hasNotificationHeader() {
        return this.getHeaders().getFirstHeader(UpnpHeader.Type.NT, NTEventHeader.class) != null;
    }
    
    public String getSubscriptionId() {
        final SubscriptionIdHeader header = this.getHeaders().getFirstHeader(UpnpHeader.Type.SID, SubscriptionIdHeader.class);
        return (header != null) ? header.getValue() : null;
    }
}
