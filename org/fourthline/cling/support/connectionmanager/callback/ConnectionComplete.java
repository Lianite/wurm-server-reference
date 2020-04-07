// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.connectionmanager.callback;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.controlpoint.ActionCallback;

public abstract class ConnectionComplete extends ActionCallback
{
    public ConnectionComplete(final Service service, final int connectionID) {
        this(service, null, connectionID);
    }
    
    protected ConnectionComplete(final Service service, final ControlPoint controlPoint, final int connectionID) {
        super(new ActionInvocation(service.getAction("ConnectionComplete")), controlPoint);
        this.getActionInvocation().setInput("ConnectionID", connectionID);
    }
}
