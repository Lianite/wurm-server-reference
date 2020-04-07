// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.avtransport.callback;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.meta.Service;
import java.util.logging.Logger;
import org.fourthline.cling.controlpoint.ActionCallback;

public abstract class SetAVTransportURI extends ActionCallback
{
    private static Logger log;
    
    public SetAVTransportURI(final Service service, final String uri) {
        this(new UnsignedIntegerFourBytes(0L), service, uri, null);
    }
    
    public SetAVTransportURI(final Service service, final String uri, final String metadata) {
        this(new UnsignedIntegerFourBytes(0L), service, uri, metadata);
    }
    
    public SetAVTransportURI(final UnsignedIntegerFourBytes instanceId, final Service service, final String uri) {
        this(instanceId, service, uri, null);
    }
    
    public SetAVTransportURI(final UnsignedIntegerFourBytes instanceId, final Service service, final String uri, final String metadata) {
        super(new ActionInvocation(service.getAction("SetAVTransportURI")));
        SetAVTransportURI.log.fine("Creating SetAVTransportURI action for URI: " + uri);
        this.getActionInvocation().setInput("InstanceID", instanceId);
        this.getActionInvocation().setInput("CurrentURI", uri);
        this.getActionInvocation().setInput("CurrentURIMetaData", metadata);
    }
    
    @Override
    public void success(final ActionInvocation invocation) {
        SetAVTransportURI.log.fine("Execution successful");
    }
    
    static {
        SetAVTransportURI.log = Logger.getLogger(SetAVTransportURI.class.getName());
    }
}
