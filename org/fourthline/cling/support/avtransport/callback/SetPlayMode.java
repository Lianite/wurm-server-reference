// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.avtransport.callback;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.model.PlayMode;
import org.fourthline.cling.model.meta.Service;
import java.util.logging.Logger;
import org.fourthline.cling.controlpoint.ActionCallback;

public abstract class SetPlayMode extends ActionCallback
{
    private static Logger log;
    
    public SetPlayMode(final Service service, final PlayMode playMode) {
        this(new UnsignedIntegerFourBytes(0L), service, playMode);
    }
    
    public SetPlayMode(final UnsignedIntegerFourBytes instanceId, final Service service, final PlayMode playMode) {
        super(new ActionInvocation(service.getAction("SetPlayMode")));
        this.getActionInvocation().setInput("InstanceID", instanceId);
        this.getActionInvocation().setInput("NewPlayMode", playMode.toString());
    }
    
    @Override
    public void success(final ActionInvocation invocation) {
        SetPlayMode.log.fine("Execution successful");
    }
    
    static {
        SetPlayMode.log = Logger.getLogger(SetPlayMode.class.getName());
    }
}
