// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.protocol;

import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.transport.RouterException;
import java.util.logging.Level;
import org.seamless.util.Exceptions;
import org.fourthline.cling.UpnpService;
import java.util.logging.Logger;
import org.fourthline.cling.model.message.UpnpMessage;

public abstract class ReceivingAsync<M extends UpnpMessage> implements Runnable
{
    private static final Logger log;
    private final UpnpService upnpService;
    private M inputMessage;
    
    protected ReceivingAsync(final UpnpService upnpService, final M inputMessage) {
        this.upnpService = upnpService;
        this.inputMessage = inputMessage;
    }
    
    public UpnpService getUpnpService() {
        return this.upnpService;
    }
    
    public M getInputMessage() {
        return this.inputMessage;
    }
    
    @Override
    public void run() {
        boolean proceed;
        try {
            proceed = this.waitBeforeExecution();
        }
        catch (InterruptedException ex2) {
            ReceivingAsync.log.info("Protocol wait before execution interrupted (on shutdown?): " + this.getClass().getSimpleName());
            proceed = false;
        }
        if (proceed) {
            try {
                this.execute();
            }
            catch (Exception ex) {
                final Throwable cause = Exceptions.unwrap(ex);
                if (!(cause instanceof InterruptedException)) {
                    throw new RuntimeException("Fatal error while executing protocol '" + this.getClass().getSimpleName() + "': " + ex, ex);
                }
                ReceivingAsync.log.log(Level.INFO, "Interrupted protocol '" + this.getClass().getSimpleName() + "': " + ex, cause);
            }
        }
    }
    
    protected boolean waitBeforeExecution() throws InterruptedException {
        return true;
    }
    
    protected abstract void execute() throws RouterException;
    
    protected <H extends UpnpHeader> H getFirstHeader(final UpnpHeader.Type headerType, final Class<H> subtype) {
        return this.getInputMessage().getHeaders().getFirstHeader(headerType, subtype);
    }
    
    @Override
    public String toString() {
        return "(" + this.getClass().getSimpleName() + ")";
    }
    
    static {
        log = Logger.getLogger(UpnpService.class.getName());
    }
}
