// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.avtransport.callback;

import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionInvocation;
import java.util.logging.Logger;
import org.fourthline.cling.controlpoint.ActionCallback;

public abstract class Pause extends ActionCallback
{
    private static Logger log;
    
    protected Pause(final ActionInvocation actionInvocation, final ControlPoint controlPoint) {
        super(actionInvocation, controlPoint);
    }
    
    protected Pause(final ActionInvocation actionInvocation) {
        super(actionInvocation);
    }
    
    public Pause(final Service service) {
        this(new UnsignedIntegerFourBytes(0L), service);
    }
    
    public Pause(final UnsignedIntegerFourBytes instanceId, final Service service) {
        super(new ActionInvocation(service.getAction("Pause")));
        this.getActionInvocation().setInput("InstanceID", instanceId);
    }
    
    @Override
    public void success(final ActionInvocation invocation) {
        Pause.log.fine("Execution successful");
    }
    
    static {
        Pause.log = Logger.getLogger(Pause.class.getName());
    }
}
