// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.discovery;

import org.fourthline.cling.model.types.NotificationSubtype;
import org.fourthline.cling.model.message.header.MANHeader;
import org.fourthline.cling.model.message.header.MXHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.message.UpnpRequest;
import org.fourthline.cling.model.message.IncomingDatagramMessage;

public class IncomingSearchRequest extends IncomingDatagramMessage<UpnpRequest>
{
    public IncomingSearchRequest(final IncomingDatagramMessage<UpnpRequest> source) {
        super(source);
    }
    
    public UpnpHeader getSearchTarget() {
        return this.getHeaders().getFirstHeader(UpnpHeader.Type.ST);
    }
    
    public Integer getMX() {
        final MXHeader header = this.getHeaders().getFirstHeader(UpnpHeader.Type.MX, MXHeader.class);
        if (header != null) {
            return header.getValue();
        }
        return null;
    }
    
    public boolean isMANSSDPDiscover() {
        final MANHeader header = this.getHeaders().getFirstHeader(UpnpHeader.Type.MAN, MANHeader.class);
        return header != null && header.getValue().equals(NotificationSubtype.DISCOVER.getHeaderString());
    }
}
