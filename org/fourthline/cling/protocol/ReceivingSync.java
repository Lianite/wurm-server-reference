// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.protocol;

import org.fourthline.cling.transport.RouterException;
import java.util.List;
import java.util.Map;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.profile.RemoteClientInfo;
import java.util.logging.Logger;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.StreamRequestMessage;

public abstract class ReceivingSync<IN extends StreamRequestMessage, OUT extends StreamResponseMessage> extends ReceivingAsync<IN>
{
    private static final Logger log;
    protected final RemoteClientInfo remoteClientInfo;
    protected OUT outputMessage;
    
    protected ReceivingSync(final UpnpService upnpService, final IN inputMessage) {
        super(upnpService, inputMessage);
        this.remoteClientInfo = new RemoteClientInfo(inputMessage);
    }
    
    public OUT getOutputMessage() {
        return this.outputMessage;
    }
    
    @Override
    protected final void execute() throws RouterException {
        this.outputMessage = this.executeSync();
        if (this.outputMessage != null && this.getRemoteClientInfo().getExtraResponseHeaders().size() > 0) {
            ReceivingSync.log.fine("Setting extra headers on response message: " + this.getRemoteClientInfo().getExtraResponseHeaders().size());
            this.outputMessage.getHeaders().putAll(this.getRemoteClientInfo().getExtraResponseHeaders());
        }
    }
    
    protected abstract OUT executeSync() throws RouterException;
    
    public void responseSent(final StreamResponseMessage responseMessage) {
    }
    
    public void responseException(final Throwable t) {
    }
    
    public RemoteClientInfo getRemoteClientInfo() {
        return this.remoteClientInfo;
    }
    
    @Override
    public String toString() {
        return "(" + this.getClass().getSimpleName() + ")";
    }
    
    static {
        log = Logger.getLogger(UpnpService.class.getName());
    }
}
