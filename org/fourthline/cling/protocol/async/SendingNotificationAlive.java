// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.protocol.async;

import org.fourthline.cling.model.types.NotificationSubtype;
import org.fourthline.cling.transport.RouterException;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.UpnpService;
import java.util.logging.Logger;

public class SendingNotificationAlive extends SendingNotification
{
    private static final Logger log;
    
    public SendingNotificationAlive(final UpnpService upnpService, final LocalDevice device) {
        super(upnpService, device);
    }
    
    @Override
    protected void execute() throws RouterException {
        SendingNotificationAlive.log.fine("Sending alive messages (" + this.getBulkRepeat() + " times) for: " + this.getDevice());
        super.execute();
    }
    
    @Override
    protected NotificationSubtype getNotificationSubtype() {
        return NotificationSubtype.ALIVE;
    }
    
    static {
        log = Logger.getLogger(SendingNotification.class.getName());
    }
}
