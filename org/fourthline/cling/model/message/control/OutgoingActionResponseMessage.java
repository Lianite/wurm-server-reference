// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.control;

import org.fourthline.cling.model.message.header.EXTHeader;
import org.fourthline.cling.model.message.header.ServerHeader;
import org.fourthline.cling.model.message.header.ContentTypeHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.meta.QueryStateVariableAction;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.message.StreamResponseMessage;

public class OutgoingActionResponseMessage extends StreamResponseMessage implements ActionResponseMessage
{
    private String actionNamespace;
    
    public OutgoingActionResponseMessage(final Action action) {
        this(UpnpResponse.Status.OK, action);
    }
    
    public OutgoingActionResponseMessage(final UpnpResponse.Status status) {
        this(status, null);
    }
    
    public OutgoingActionResponseMessage(final UpnpResponse.Status status, final Action action) {
        super(new UpnpResponse(status));
        if (action != null) {
            if (action instanceof QueryStateVariableAction) {
                this.actionNamespace = "urn:schemas-upnp-org:control-1-0";
            }
            else {
                this.actionNamespace = action.getService().getServiceType().toString();
            }
        }
        this.addHeaders();
    }
    
    protected void addHeaders() {
        this.getHeaders().add(UpnpHeader.Type.CONTENT_TYPE, new ContentTypeHeader(ContentTypeHeader.DEFAULT_CONTENT_TYPE_UTF8));
        this.getHeaders().add(UpnpHeader.Type.SERVER, new ServerHeader());
        this.getHeaders().add(UpnpHeader.Type.EXT, new EXTHeader());
    }
    
    @Override
    public String getActionNamespace() {
        return this.actionNamespace;
    }
}
