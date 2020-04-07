// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.avtransport.callback;

import org.fourthline.cling.model.action.ActionArgumentValue;
import java.util.Map;
import org.fourthline.cling.support.model.DeviceCapabilities;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.meta.Service;
import java.util.logging.Logger;
import org.fourthline.cling.controlpoint.ActionCallback;

public abstract class GetDeviceCapabilities extends ActionCallback
{
    private static Logger log;
    
    public GetDeviceCapabilities(final Service service) {
        this(new UnsignedIntegerFourBytes(0L), service);
    }
    
    public GetDeviceCapabilities(final UnsignedIntegerFourBytes instanceId, final Service service) {
        super(new ActionInvocation(service.getAction("GetDeviceCapabilities")));
        this.getActionInvocation().setInput("InstanceID", instanceId);
    }
    
    @Override
    public void success(final ActionInvocation invocation) {
        final DeviceCapabilities caps = new DeviceCapabilities(invocation.getOutputMap());
        this.received(invocation, caps);
    }
    
    public abstract void received(final ActionInvocation p0, final DeviceCapabilities p1);
    
    static {
        GetDeviceCapabilities.log = Logger.getLogger(GetDeviceCapabilities.class.getName());
    }
}
