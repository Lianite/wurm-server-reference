// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.gena;

import org.fourthline.cling.model.message.header.ContentTypeHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.StreamResponseMessage;

public class OutgoingEventResponseMessage extends StreamResponseMessage
{
    public OutgoingEventResponseMessage() {
        super(new UpnpResponse(UpnpResponse.Status.OK));
        this.getHeaders().add(UpnpHeader.Type.CONTENT_TYPE, new ContentTypeHeader());
    }
    
    public OutgoingEventResponseMessage(final UpnpResponse operation) {
        super(operation);
        this.getHeaders().add(UpnpHeader.Type.CONTENT_TYPE, new ContentTypeHeader());
    }
}
