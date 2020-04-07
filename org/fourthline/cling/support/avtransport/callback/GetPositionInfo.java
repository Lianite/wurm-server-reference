// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.avtransport.callback;

import org.fourthline.cling.model.action.ActionArgumentValue;
import java.util.Map;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.meta.Service;
import java.util.logging.Logger;
import org.fourthline.cling.controlpoint.ActionCallback;

public abstract class GetPositionInfo extends ActionCallback
{
    private static Logger log;
    
    public GetPositionInfo(final Service service) {
        this(new UnsignedIntegerFourBytes(0L), service);
    }
    
    public GetPositionInfo(final UnsignedIntegerFourBytes instanceId, final Service service) {
        super(new ActionInvocation(service.getAction("GetPositionInfo")));
        this.getActionInvocation().setInput("InstanceID", instanceId);
    }
    
    @Override
    public void success(final ActionInvocation invocation) {
        final PositionInfo positionInfo = new PositionInfo(invocation.getOutputMap());
        this.received(invocation, positionInfo);
    }
    
    public abstract void received(final ActionInvocation p0, final PositionInfo p1);
    
    static {
        GetPositionInfo.log = Logger.getLogger(GetPositionInfo.class.getName());
    }
}
