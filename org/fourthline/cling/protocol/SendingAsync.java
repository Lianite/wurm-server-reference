// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.protocol;

import org.fourthline.cling.transport.RouterException;
import java.util.logging.Level;
import org.seamless.util.Exceptions;
import org.fourthline.cling.UpnpService;
import java.util.logging.Logger;

public abstract class SendingAsync implements Runnable
{
    private static final Logger log;
    private final UpnpService upnpService;
    
    protected SendingAsync(final UpnpService upnpService) {
        this.upnpService = upnpService;
    }
    
    public UpnpService getUpnpService() {
        return this.upnpService;
    }
    
    @Override
    public void run() {
        try {
            this.execute();
        }
        catch (Exception ex) {
            final Throwable cause = Exceptions.unwrap(ex);
            if (!(cause instanceof InterruptedException)) {
                throw new RuntimeException("Fatal error while executing protocol '" + this.getClass().getSimpleName() + "': " + ex, ex);
            }
            SendingAsync.log.log(Level.INFO, "Interrupted protocol '" + this.getClass().getSimpleName() + "': " + ex, cause);
        }
    }
    
    protected abstract void execute() throws RouterException;
    
    @Override
    public String toString() {
        return "(" + this.getClass().getSimpleName() + ")";
    }
    
    static {
        log = Logger.getLogger(UpnpService.class.getName());
    }
}
