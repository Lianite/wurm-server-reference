// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.spi;

import org.fourthline.cling.protocol.ProtocolCreationException;
import org.fourthline.cling.model.message.UpnpResponse;
import org.seamless.util.Exceptions;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.protocol.ReceivingSync;
import org.fourthline.cling.protocol.ProtocolFactory;
import java.util.logging.Logger;

public abstract class UpnpStream implements Runnable
{
    private static Logger log;
    protected final ProtocolFactory protocolFactory;
    protected ReceivingSync syncProtocol;
    
    protected UpnpStream(final ProtocolFactory protocolFactory) {
        this.protocolFactory = protocolFactory;
    }
    
    public ProtocolFactory getProtocolFactory() {
        return this.protocolFactory;
    }
    
    public StreamResponseMessage process(final StreamRequestMessage requestMsg) {
        UpnpStream.log.fine("Processing stream request message: " + requestMsg);
        try {
            this.syncProtocol = this.getProtocolFactory().createReceivingSync(requestMsg);
        }
        catch (ProtocolCreationException ex) {
            UpnpStream.log.warning("Processing stream request failed - " + Exceptions.unwrap(ex).toString());
            return new StreamResponseMessage(UpnpResponse.Status.NOT_IMPLEMENTED);
        }
        UpnpStream.log.fine("Running protocol for synchronous message processing: " + this.syncProtocol);
        this.syncProtocol.run();
        final StreamResponseMessage responseMsg = this.syncProtocol.getOutputMessage();
        if (responseMsg == null) {
            UpnpStream.log.finer("Protocol did not return any response message");
            return null;
        }
        UpnpStream.log.finer("Protocol returned response: " + responseMsg);
        return responseMsg;
    }
    
    protected void responseSent(final StreamResponseMessage responseMessage) {
        if (this.syncProtocol != null) {
            this.syncProtocol.responseSent(responseMessage);
        }
    }
    
    protected void responseException(final Throwable t) {
        if (this.syncProtocol != null) {
            this.syncProtocol.responseException(t);
        }
    }
    
    @Override
    public String toString() {
        return "(" + this.getClass().getSimpleName() + ")";
    }
    
    static {
        UpnpStream.log = Logger.getLogger(UpnpStream.class.getName());
    }
}
