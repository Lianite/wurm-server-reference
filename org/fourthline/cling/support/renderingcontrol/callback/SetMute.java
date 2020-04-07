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

public abstract class SetMute extends ActionCallback
{
    private static Logger log;
    
    public SetMute(final Service service, final boolean desiredMute) {
        this(new UnsignedIntegerFourBytes(0L), service, desiredMute);
    }
    
    public SetMute(final UnsignedIntegerFourBytes instanceId, final Service service, final boolean desiredMute) {
        super(new ActionInvocation(service.getAction("SetMute")));
        this.getActionInvocation().setInput("InstanceID", instanceId);
        this.getActionInvocation().setInput("Channel", Channel.Master.toString());
        this.getActionInvocation().setInput("DesiredMute", desiredMute);
    }
    
    @Override
    public void success(final ActionInvocation invocation) {
        SetMute.log.fine("Executed successfully");
    }
    
    static {
        SetMute.log = Logger.getLogger(SetMute.class.getName());
    }
}
