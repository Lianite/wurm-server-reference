// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.messagebox;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.support.messagebox.model.Message;
import org.fourthline.cling.model.meta.Service;
import org.seamless.util.MimeType;
import org.fourthline.cling.controlpoint.ActionCallback;

public abstract class AddMessage extends ActionCallback
{
    protected final MimeType mimeType;
    
    public AddMessage(final Service service, final Message message) {
        super(new ActionInvocation(service.getAction("AddMessage")));
        this.mimeType = MimeType.valueOf("text/xml;charset=\"utf-8\"");
        this.getActionInvocation().setInput("MessageID", Integer.toString(message.getId()));
        this.getActionInvocation().setInput("MessageType", this.mimeType.toString());
        this.getActionInvocation().setInput("Message", message.toString());
    }
}
