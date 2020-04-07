// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.avtransport.callback;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.meta.Service;
import java.util.logging.Logger;
import org.fourthline.cling.controlpoint.ActionCallback;

public abstract class Stop extends ActionCallback
{
    private static Logger log;
    
    public Stop(final Service service) {
        this(new UnsignedIntegerFourBytes(0L), service);
    }
    
    public Stop(final UnsignedIntegerFourBytes instanceId, final Service service) {
        super(new ActionInvocation(service.getAction("Stop")));
        this.getActionInvocation().setInput("InstanceID", instanceId);
    }
    
    @Override
    public void success(final ActionInvocation invocation) {
        Stop.log.fine("Execution successful");
    }
    
    static {
        Stop.log = Logger.getLogger(Stop.class.getName());
    }
}
