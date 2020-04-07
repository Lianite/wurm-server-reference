// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.gena;

import org.fourthline.cling.model.message.header.TimeoutHeader;
import org.fourthline.cling.model.message.header.SubscriptionIdHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.message.StreamResponseMessage;

public class IncomingSubscribeResponseMessage extends StreamResponseMessage
{
    public IncomingSubscribeResponseMessage(final StreamResponseMessage source) {
        super(source);
    }
    
    public boolean isValidHeaders() {
        return this.getHeaders().getFirstHeader(UpnpHeader.Type.SID, SubscriptionIdHeader.class) != null && this.getHeaders().getFirstHeader(UpnpHeader.Type.TIMEOUT, TimeoutHeader.class) != null;
    }
    
    public String getSubscriptionId() {
        return this.getHeaders().getFirstHeader(UpnpHeader.Type.SID, SubscriptionIdHeader.class).getValue();
    }
    
    public int getSubscriptionDurationSeconds() {
        return this.getHeaders().getFirstHeader(UpnpHeader.Type.TIMEOUT, TimeoutHeader.class).getValue();
    }
}
