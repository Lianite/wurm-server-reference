// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.avtransport.callback;

import org.fourthline.cling.support.model.TransportAction;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.meta.Service;
import java.util.logging.Logger;
import org.fourthline.cling.controlpoint.ActionCallback;

public abstract class GetCurrentTransportActions extends ActionCallback
{
    private static Logger log;
    
    public GetCurrentTransportActions(final Service service) {
        this(new UnsignedIntegerFourBytes(0L), service);
    }
    
    public GetCurrentTransportActions(final UnsignedIntegerFourBytes instanceId, final Service service) {
        super(new ActionInvocation(service.getAction("GetCurrentTransportActions")));
        this.getActionInvocation().setInput("InstanceID", instanceId);
    }
    
    @Override
    public void success(final ActionInvocation invocation) {
        final String actionsString = (String)invocation.getOutput("Actions").getValue();
        this.received(invocation, TransportAction.valueOfCommaSeparatedList(actionsString));
    }
    
    public abstract void received(final ActionInvocation p0, final TransportAction[] p1);
    
    static {
        GetCurrentTransportActions.log = Logger.getLogger(GetCurrentTransportActions.class.getName());
    }
}
