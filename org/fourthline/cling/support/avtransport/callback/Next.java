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

public abstract class Next extends ActionCallback
{
    private static Logger log;
    
    protected Next(final ActionInvocation actionInvocation, final ControlPoint controlPoint) {
        super(actionInvocation, controlPoint);
    }
    
    protected Next(final ActionInvocation actionInvocation) {
        super(actionInvocation);
    }
    
    public Next(final Service service) {
        this(new UnsignedIntegerFourBytes(0L), service);
    }
    
    public Next(final UnsignedIntegerFourBytes instanceId, final Service service) {
        super(new ActionInvocation(service.getAction("Next")));
        this.getActionInvocation().setInput("InstanceID", instanceId);
    }
    
    @Override
    public void success(final ActionInvocation invocation) {
        Next.log.fine("Execution successful");
    }
    
    static {
        Next.log = Logger.getLogger(Next.class.getName());
    }
}
