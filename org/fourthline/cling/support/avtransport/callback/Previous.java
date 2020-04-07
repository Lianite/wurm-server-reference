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

public abstract class Previous extends ActionCallback
{
    private static Logger log;
    
    protected Previous(final ActionInvocation actionInvocation, final ControlPoint controlPoint) {
        super(actionInvocation, controlPoint);
    }
    
    protected Previous(final ActionInvocation actionInvocation) {
        super(actionInvocation);
    }
    
    public Previous(final Service service) {
        this(new UnsignedIntegerFourBytes(0L), service);
    }
    
    public Previous(final UnsignedIntegerFourBytes instanceId, final Service service) {
        super(new ActionInvocation(service.getAction("Previous")));
        this.getActionInvocation().setInput("InstanceID", instanceId);
    }
    
    @Override
    public void success(final ActionInvocation invocation) {
        Previous.log.fine("Execution successful");
    }
    
    static {
        Previous.log = Logger.getLogger(Previous.class.getName());
    }
}
