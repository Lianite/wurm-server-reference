// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.avtransport.callback;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.meta.Service;
import java.util.logging.Logger;
import org.fourthline.cling.controlpoint.ActionCallback;

public abstract class Play extends ActionCallback
{
    private static Logger log;
    
    public Play(final Service service) {
        this(new UnsignedIntegerFourBytes(0L), service, "1");
    }
    
    public Play(final Service service, final String speed) {
        this(new UnsignedIntegerFourBytes(0L), service, speed);
    }
    
    public Play(final UnsignedIntegerFourBytes instanceId, final Service service) {
        this(instanceId, service, "1");
    }
    
    public Play(final UnsignedIntegerFourBytes instanceId, final Service service, final String speed) {
        super(new ActionInvocation(service.getAction("Play")));
        this.getActionInvocation().setInput("InstanceID", instanceId);
        this.getActionInvocation().setInput("Speed", speed);
    }
    
    @Override
    public void success(final ActionInvocation invocation) {
        Play.log.fine("Execution successful");
    }
    
    static {
        Play.log = Logger.getLogger(Play.class.getName());
    }
}
