// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.renderingcontrol.callback;

import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.support.model.Channel;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.meta.Service;
import java.util.logging.Logger;
import org.fourthline.cling.controlpoint.ActionCallback;

public abstract class GetVolume extends ActionCallback
{
    private static Logger log;
    
    public GetVolume(final Service service) {
        this(new UnsignedIntegerFourBytes(0L), service);
    }
    
    public GetVolume(final UnsignedIntegerFourBytes instanceId, final Service service) {
        super(new ActionInvocation(service.getAction("GetVolume")));
        this.getActionInvocation().setInput("InstanceID", instanceId);
        this.getActionInvocation().setInput("Channel", Channel.Master.toString());
    }
    
    @Override
    public void success(final ActionInvocation invocation) {
        boolean ok = true;
        int currentVolume = 0;
        try {
            currentVolume = Integer.valueOf(invocation.getOutput("CurrentVolume").getValue().toString());
        }
        catch (Exception ex) {
            invocation.setFailure(new ActionException(ErrorCode.ACTION_FAILED, "Can't parse ProtocolInfo response: " + ex, ex));
            this.failure(invocation, null);
            ok = false;
        }
        if (ok) {
            this.received(invocation, currentVolume);
        }
    }
    
    public abstract void received(final ActionInvocation p0, final int p1);
    
    static {
        GetVolume.log = Logger.getLogger(GetVolume.class.getName());
    }
}
