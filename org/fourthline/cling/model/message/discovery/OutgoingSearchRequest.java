// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.discovery;

import org.fourthline.cling.model.message.header.HostHeader;
import org.fourthline.cling.model.message.header.MXHeader;
import org.fourthline.cling.model.message.header.MANHeader;
import org.fourthline.cling.model.types.NotificationSubtype;
import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.message.UpnpRequest;
import org.fourthline.cling.model.message.OutgoingDatagramMessage;

public class OutgoingSearchRequest extends OutgoingDatagramMessage<UpnpRequest>
{
    private UpnpHeader searchTarget;
    
    public OutgoingSearchRequest(final UpnpHeader searchTarget, final int mxSeconds) {
        super(new UpnpRequest(UpnpRequest.Method.MSEARCH), ModelUtil.getInetAddressByName("239.255.255.250"), 1900);
        this.searchTarget = searchTarget;
        this.getHeaders().add(UpnpHeader.Type.MAN, new MANHeader(NotificationSubtype.DISCOVER.getHeaderString()));
        this.getHeaders().add(UpnpHeader.Type.MX, new MXHeader(mxSeconds));
        this.getHeaders().add(UpnpHeader.Type.ST, searchTarget);
        this.getHeaders().add(UpnpHeader.Type.HOST, new HostHeader());
    }
    
    public UpnpHeader getSearchTarget() {
        return this.searchTarget;
    }
}
