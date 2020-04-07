// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.protocol.async;

import org.fourthline.cling.transport.RouterException;
import java.util.Iterator;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.protocol.RetrieveRemoteDescriptors;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.ValidationError;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteDeviceIdentity;
import org.fourthline.cling.model.message.UpnpRequest;
import org.fourthline.cling.model.message.IncomingDatagramMessage;
import org.fourthline.cling.UpnpService;
import java.util.logging.Logger;
import org.fourthline.cling.model.message.discovery.IncomingNotificationRequest;
import org.fourthline.cling.protocol.ReceivingAsync;

public class ReceivingNotification extends ReceivingAsync<IncomingNotificationRequest>
{
    private static final Logger log;
    
    public ReceivingNotification(final UpnpService upnpService, final IncomingDatagramMessage<UpnpRequest> inputMessage) {
        super(upnpService, new IncomingNotificationRequest(inputMessage));
    }
    
    @Override
    protected void execute() throws RouterException {
        final UDN udn = this.getInputMessage().getUDN();
        if (udn == null) {
            ReceivingNotification.log.fine("Ignoring notification message without UDN: " + ((ReceivingAsync<Object>)this).getInputMessage());
            return;
        }
        final RemoteDeviceIdentity rdIdentity = new RemoteDeviceIdentity(this.getInputMessage());
        ReceivingNotification.log.fine("Received device notification: " + rdIdentity);
        RemoteDevice rd;
        try {
            rd = new RemoteDevice(rdIdentity);
        }
        catch (ValidationException ex) {
            ReceivingNotification.log.warning("Validation errors of device during discovery: " + rdIdentity);
            for (final ValidationError validationError : ex.getErrors()) {
                ReceivingNotification.log.warning(validationError.toString());
            }
            return;
        }
        if (this.getInputMessage().isAliveMessage()) {
            ReceivingNotification.log.fine("Received device ALIVE advertisement, descriptor location is: " + rdIdentity.getDescriptorURL());
            if (rdIdentity.getDescriptorURL() == null) {
                ReceivingNotification.log.finer("Ignoring message without location URL header: " + ((ReceivingAsync<Object>)this).getInputMessage());
                return;
            }
            if (rdIdentity.getMaxAgeSeconds() == null) {
                ReceivingNotification.log.finer("Ignoring message without max-age header: " + ((ReceivingAsync<Object>)this).getInputMessage());
                return;
            }
            if (this.getUpnpService().getRegistry().update(rdIdentity)) {
                ReceivingNotification.log.finer("Remote device was already known: " + udn);
                return;
            }
            this.getUpnpService().getConfiguration().getAsyncProtocolExecutor().execute(new RetrieveRemoteDescriptors(this.getUpnpService(), rd));
        }
        else if (this.getInputMessage().isByeByeMessage()) {
            ReceivingNotification.log.fine("Received device BYEBYE advertisement");
            final boolean removed = this.getUpnpService().getRegistry().removeDevice(rd);
            if (removed) {
                ReceivingNotification.log.fine("Removed remote device from registry: " + rd);
            }
        }
        else {
            ReceivingNotification.log.finer("Ignoring unknown notification message: " + ((ReceivingAsync<Object>)this).getInputMessage());
        }
    }
    
    static {
        log = Logger.getLogger(ReceivingNotification.class.getName());
    }
}
