// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.igd.callback;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.model.PortMapping;
import org.fourthline.cling.controlpoint.ActionCallback;

public abstract class PortMappingAdd extends ActionCallback
{
    protected final PortMapping portMapping;
    
    public PortMappingAdd(final Service service, final PortMapping portMapping) {
        this(service, null, portMapping);
    }
    
    protected PortMappingAdd(final Service service, final ControlPoint controlPoint, final PortMapping portMapping) {
        super(new ActionInvocation(service.getAction("AddPortMapping")), controlPoint);
        this.portMapping = portMapping;
        this.getActionInvocation().setInput("NewExternalPort", portMapping.getExternalPort());
        this.getActionInvocation().setInput("NewProtocol", portMapping.getProtocol());
        this.getActionInvocation().setInput("NewInternalClient", portMapping.getInternalClient());
        this.getActionInvocation().setInput("NewInternalPort", portMapping.getInternalPort());
        this.getActionInvocation().setInput("NewLeaseDuration", portMapping.getLeaseDurationSeconds());
        this.getActionInvocation().setInput("NewEnabled", portMapping.isEnabled());
        if (portMapping.hasRemoteHost()) {
            this.getActionInvocation().setInput("NewRemoteHost", portMapping.getRemoteHost());
        }
        if (portMapping.hasDescription()) {
            this.getActionInvocation().setInput("NewPortMappingDescription", portMapping.getDescription());
        }
    }
}
