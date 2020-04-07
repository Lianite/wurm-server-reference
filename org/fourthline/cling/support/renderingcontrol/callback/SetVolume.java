// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.renderingcontrol.callback;

import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes;
import org.fourthline.cling.support.model.Channel;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.meta.Service;
import java.util.logging.Logger;
import org.fourthline.cling.controlpoint.ActionCallback;

public abstract class SetVolume extends ActionCallback
{
    private static Logger log;
    
    public SetVolume(final Service service, final long newVolume) {
        this(new UnsignedIntegerFourBytes(0L), service, newVolume);
    }
    
    public SetVolume(final UnsignedIntegerFourBytes instanceId, final Service service, final long newVolume) {
        super(new ActionInvocation(service.getAction("SetVolume")));
        this.getActionInvocation().setInput("InstanceID", instanceId);
        this.getActionInvocation().setInput("Channel", Channel.Master.toString());
        this.getActionInvocation().setInput("DesiredVolume", new UnsignedIntegerTwoBytes(newVolume));
    }
    
    @Override
    public void success(final ActionInvocation invocation) {
        SetVolume.log.fine("Executed successfully");
    }
    
    static {
        SetVolume.log = Logger.getLogger(SetVolume.class.getName());
    }
}
