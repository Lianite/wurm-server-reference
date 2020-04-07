// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.avtransport.callback;

import org.fourthline.cling.model.action.ActionArgumentValue;
import java.util.Map;
import org.fourthline.cling.support.model.TransportInfo;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.meta.Service;
import java.util.logging.Logger;
import org.fourthline.cling.controlpoint.ActionCallback;

public abstract class GetTransportInfo extends ActionCallback
{
    private static Logger log;
    
    public GetTransportInfo(final Service service) {
        this(new UnsignedIntegerFourBytes(0L), service);
    }
    
    public GetTransportInfo(final UnsignedIntegerFourBytes instanceId, final Service service) {
        super(new ActionInvocation(service.getAction("GetTransportInfo")));
        this.getActionInvocation().setInput("InstanceID", instanceId);
    }
    
    @Override
    public void success(final ActionInvocation invocation) {
        final TransportInfo transportInfo = new TransportInfo(invocation.getOutputMap());
        this.received(invocation, transportInfo);
    }
    
    public abstract void received(final ActionInvocation p0, final TransportInfo p1);
    
    static {
        GetTransportInfo.log = Logger.getLogger(GetTransportInfo.class.getName());
    }
}
