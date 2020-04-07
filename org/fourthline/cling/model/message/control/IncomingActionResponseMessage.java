// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.control;

import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.StreamResponseMessage;

public class IncomingActionResponseMessage extends StreamResponseMessage implements ActionResponseMessage
{
    public IncomingActionResponseMessage(final StreamResponseMessage source) {
        super(source);
    }
    
    public IncomingActionResponseMessage(final UpnpResponse operation) {
        super(operation);
    }
    
    @Override
    public String getActionNamespace() {
        return null;
    }
    
    public boolean isFailedNonRecoverable() {
        final int statusCode = this.getOperation().getStatusCode();
        return this.getOperation().isFailed() && statusCode != UpnpResponse.Status.METHOD_NOT_SUPPORTED.getStatusCode() && (statusCode != UpnpResponse.Status.INTERNAL_SERVER_ERROR.getStatusCode() || !this.hasBody());
    }
    
    public boolean isFailedRecoverable() {
        return this.hasBody() && this.getOperation().getStatusCode() == UpnpResponse.Status.INTERNAL_SERVER_ERROR.getStatusCode();
    }
}
