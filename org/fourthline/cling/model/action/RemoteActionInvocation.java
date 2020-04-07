// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.action;

import org.fourthline.cling.model.profile.ClientInfo;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.profile.RemoteClientInfo;

public class RemoteActionInvocation extends ActionInvocation
{
    protected final RemoteClientInfo remoteClientInfo;
    
    public RemoteActionInvocation(final Action action, final ActionArgumentValue[] input, final ActionArgumentValue[] output, final RemoteClientInfo remoteClientInfo) {
        super(action, input, output, null);
        this.remoteClientInfo = remoteClientInfo;
    }
    
    public RemoteActionInvocation(final Action action, final RemoteClientInfo remoteClientInfo) {
        super(action);
        this.remoteClientInfo = remoteClientInfo;
    }
    
    public RemoteActionInvocation(final ActionException failure, final RemoteClientInfo remoteClientInfo) {
        super(failure);
        this.remoteClientInfo = remoteClientInfo;
    }
    
    public RemoteClientInfo getRemoteClientInfo() {
        return this.remoteClientInfo;
    }
}
