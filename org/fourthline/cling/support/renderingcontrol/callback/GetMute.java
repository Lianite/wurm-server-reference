// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.renderingcontrol.callback;

import org.fourthline.cling.support.model.Channel;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.meta.Service;
import java.util.logging.Logger;
import org.fourthline.cling.controlpoint.ActionCallback;

public abstract class GetMute extends ActionCallback
{
    private static Logger log;
    
    public GetMute(final Service service) {
        this(new UnsignedIntegerFourBytes(0L), service);
    }
    
    public GetMute(final UnsignedIntegerFourBytes instanceId, final Service service) {
        super(new ActionInvocation(service.getAction("GetMute")));
        this.getActionInvocation().setInput("InstanceID", instanceId);
        this.getActionInvocation().setInput("Channel", Channel.Master.toString());
    }
    
    @Override
    public void success(final ActionInvocation invocation) {
        final boolean currentMute = (boolean)invocation.getOutput("CurrentMute").getValue();
        this.received(invocation, currentMute);
    }
    
    public abstract void received(final ActionInvocation p0, final boolean p1);
    
    static {
        GetMute.log = Logger.getLogger(GetMute.class.getName());
    }
}
