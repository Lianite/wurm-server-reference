// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.igd.callback;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.model.PortMapping;
import org.fourthline.cling.controlpoint.ActionCallback;

public abstract class PortMappingDelete extends ActionCallback
{
    protected final PortMapping portMapping;
    
    public PortMappingDelete(final Service service, final PortMapping portMapping) {
        this(service, null, portMapping);
    }
    
    protected PortMappingDelete(final Service service, final ControlPoint controlPoint, final PortMapping portMapping) {
        super(new ActionInvocation(service.getAction("DeletePortMapping")), controlPoint);
        this.portMapping = portMapping;
        this.getActionInvocation().setInput("NewExternalPort", portMapping.getExternalPort());
        this.getActionInvocation().setInput("NewProtocol", portMapping.getProtocol());
        if (portMapping.hasRemoteHost()) {
            this.getActionInvocation().setInput("NewRemoteHost", portMapping.getRemoteHost());
        }
    }
}
