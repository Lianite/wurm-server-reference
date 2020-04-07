// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.avtransport.callback;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.support.model.SeekMode;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.meta.Service;
import java.util.logging.Logger;
import org.fourthline.cling.controlpoint.ActionCallback;

public abstract class Seek extends ActionCallback
{
    private static Logger log;
    
    public Seek(final Service service, final String relativeTimeTarget) {
        this(new UnsignedIntegerFourBytes(0L), service, SeekMode.REL_TIME, relativeTimeTarget);
    }
    
    public Seek(final UnsignedIntegerFourBytes instanceId, final Service service, final String relativeTimeTarget) {
        this(instanceId, service, SeekMode.REL_TIME, relativeTimeTarget);
    }
    
    public Seek(final Service service, final SeekMode mode, final String target) {
        this(new UnsignedIntegerFourBytes(0L), service, mode, target);
    }
    
    public Seek(final UnsignedIntegerFourBytes instanceId, final Service service, final SeekMode mode, final String target) {
        super(new ActionInvocation(service.getAction("Seek")));
        this.getActionInvocation().setInput("InstanceID", instanceId);
        this.getActionInvocation().setInput("Unit", mode.name());
        this.getActionInvocation().setInput("Target", target);
    }
    
    @Override
    public void success(final ActionInvocation invocation) {
        Seek.log.fine("Execution successful");
    }
    
    static {
        Seek.log = Logger.getLogger(Seek.class.getName());
    }
}
