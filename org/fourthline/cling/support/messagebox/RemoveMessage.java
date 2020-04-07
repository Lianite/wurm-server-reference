// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.messagebox;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.support.messagebox.model.Message;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.controlpoint.ActionCallback;

public abstract class RemoveMessage extends ActionCallback
{
    public RemoveMessage(final Service service, final Message message) {
        this(service, message.getId());
    }
    
    public RemoveMessage(final Service service, final int id) {
        super(new ActionInvocation(service.getAction("RemoveMessage")));
        this.getActionInvocation().setInput("MessageID", id);
    }
}
