// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.protocol;

import org.fourthline.cling.transport.RouterException;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.StreamRequestMessage;

public abstract class SendingSync<IN extends StreamRequestMessage, OUT extends StreamResponseMessage> extends SendingAsync
{
    private final IN inputMessage;
    protected OUT outputMessage;
    
    protected SendingSync(final UpnpService upnpService, final IN inputMessage) {
        super(upnpService);
        this.inputMessage = inputMessage;
    }
    
    public IN getInputMessage() {
        return this.inputMessage;
    }
    
    public OUT getOutputMessage() {
        return this.outputMessage;
    }
    
    @Override
    protected final void execute() throws RouterException {
        this.outputMessage = this.executeSync();
    }
    
    protected abstract OUT executeSync() throws RouterException;
    
    @Override
    public String toString() {
        return "(" + this.getClass().getSimpleName() + ")";
    }
}
