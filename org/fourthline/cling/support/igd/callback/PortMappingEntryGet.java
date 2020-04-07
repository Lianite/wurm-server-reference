// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.igd.callback;

import org.fourthline.cling.model.action.ActionArgumentValue;
import java.util.Map;
import org.fourthline.cling.support.model.PortMapping;
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.controlpoint.ActionCallback;

public abstract class PortMappingEntryGet extends ActionCallback
{
    public PortMappingEntryGet(final Service service, final long index) {
        this(service, null, index);
    }
    
    protected PortMappingEntryGet(final Service service, final ControlPoint controlPoint, final long index) {
        super(new ActionInvocation(service.getAction("GetGenericPortMappingEntry")), controlPoint);
        this.getActionInvocation().setInput("NewPortMappingIndex", new UnsignedIntegerTwoBytes(index));
    }
    
    @Override
    public void success(final ActionInvocation invocation) {
        final Map<String, ActionArgumentValue<Service>> outputMap = (Map<String, ActionArgumentValue<Service>>)invocation.getOutputMap();
        this.success(new PortMapping(outputMap));
    }
    
    protected abstract void success(final PortMapping p0);
}
