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
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.IncomingDatagramMessage;
import org.fourthline.cling.UpnpService;
import java.util.logging.Logger;
import org.fourthline.cling.model.message.discovery.IncomingSearchResponse;
import org.fourthline.cling.protocol.ReceivingAsync;

public class ReceivingSearchResponse extends ReceivingAsync<IncomingSearchResponse>
{
    private static final Logger log;
    
    public ReceivingSearchResponse(final UpnpService upnpService, final IncomingDatagramMessage<UpnpResponse> inputMessage) {
        super(upnpService, new IncomingSearchResponse(inputMessage));
    }
    
    @Override
    protected void execute() throws RouterException {
        if (!this.getInputMessage().isSearchResponseMessage()) {
            ReceivingSearchResponse.log.fine("Ignoring invalid search response message: " + ((ReceivingAsync<Object>)this).getInputMessage());
            return;
        }
        final UDN udn = this.getInputMessage().getRootDeviceUDN();
        if (udn == null) {
            ReceivingSearchResponse.log.fine("Ignoring search response message without UDN: " + ((ReceivingAsync<Object>)this).getInputMessage());
            return;
        }
        final RemoteDeviceIdentity rdIdentity = new RemoteDeviceIdentity(this.getInputMessage());
        ReceivingSearchResponse.log.fine("Received device search response: " + rdIdentity);
        if (this.getUpnpService().getRegistry().update(rdIdentity)) {
            ReceivingSearchResponse.log.fine("Remote device was already known: " + udn);
            return;
        }
        RemoteDevice rd;
        try {
            rd = new RemoteDevice(rdIdentity);
        }
        catch (ValidationException ex) {
            ReceivingSearchResponse.log.warning("Validation errors of device during discovery: " + rdIdentity);
            for (final ValidationError validationError : ex.getErrors()) {
                ReceivingSearchResponse.log.warning(validationError.toString());
            }
            return;
        }
        if (rdIdentity.getDescriptorURL() == null) {
            ReceivingSearchResponse.log.finer("Ignoring message without location URL header: " + ((ReceivingAsync<Object>)this).getInputMessage());
            return;
        }
        if (rdIdentity.getMaxAgeSeconds() == null) {
            ReceivingSearchResponse.log.finer("Ignoring message without max-age header: " + ((ReceivingAsync<Object>)this).getInputMessage());
            return;
        }
        this.getUpnpService().getConfiguration().getAsyncProtocolExecutor().execute(new RetrieveRemoteDescriptors(this.getUpnpService(), rd));
    }
    
    static {
        log = Logger.getLogger(ReceivingSearchResponse.class.getName());
    }
}
