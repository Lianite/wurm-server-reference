// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.avtransport.callback;

import org.fourthline.cling.model.action.ActionArgumentValue;
import java.util.Map;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.meta.Service;
import java.util.logging.Logger;
import org.fourthline.cling.controlpoint.ActionCallback;

public abstract class GetMediaInfo extends ActionCallback
{
    private static Logger log;
    
    public GetMediaInfo(final Service service) {
        this(new UnsignedIntegerFourBytes(0L), service);
    }
    
    public GetMediaInfo(final UnsignedIntegerFourBytes instanceId, final Service service) {
        super(new ActionInvocation(service.getAction("GetMediaInfo")));
        this.getActionInvocation().setInput("InstanceID", instanceId);
    }
    
    @Override
    public void success(final ActionInvocation invocation) {
        final MediaInfo mediaInfo = new MediaInfo(invocation.getOutputMap());
        this.received(invocation, mediaInfo);
    }
    
    public abstract void received(final ActionInvocation p0, final MediaInfo p1);
    
    static {
        GetMediaInfo.log = Logger.getLogger(GetMediaInfo.class.getName());
    }
}
