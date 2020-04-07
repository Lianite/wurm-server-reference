// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.protocol.sync;

import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.transport.RouterException;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.gena.RemoteGENASubscription;
import java.util.logging.Logger;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.gena.OutgoingUnsubscribeRequestMessage;
import org.fourthline.cling.protocol.SendingSync;

public class SendingUnsubscribe extends SendingSync<OutgoingUnsubscribeRequestMessage, StreamResponseMessage>
{
    private static final Logger log;
    protected final RemoteGENASubscription subscription;
    
    public SendingUnsubscribe(final UpnpService upnpService, final RemoteGENASubscription subscription) {
        super(upnpService, new OutgoingUnsubscribeRequestMessage(subscription, upnpService.getConfiguration().getEventSubscriptionHeaders(subscription.getService())));
        this.subscription = subscription;
    }
    
    @Override
    protected StreamResponseMessage executeSync() throws RouterException {
        SendingUnsubscribe.log.fine("Sending unsubscribe request: " + ((SendingSync<Object, OUT>)this).getInputMessage());
        StreamResponseMessage response = null;
        try {
            response = this.getUpnpService().getRouter().send(((SendingSync<StreamRequestMessage, OUT>)this).getInputMessage());
            return response;
        }
        finally {
            this.onUnsubscribe(response);
        }
    }
    
    protected void onUnsubscribe(final StreamResponseMessage response) {
        this.getUpnpService().getRegistry().removeRemoteSubscription(this.subscription);
        this.getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(new Runnable() {
            @Override
            public void run() {
                if (response == null) {
                    SendingUnsubscribe.log.fine("Unsubscribe failed, no response received");
                    SendingUnsubscribe.this.subscription.end(CancelReason.UNSUBSCRIBE_FAILED, null);
                }
                else if (response.getOperation().isFailed()) {
                    SendingUnsubscribe.log.fine("Unsubscribe failed, response was: " + response);
                    SendingUnsubscribe.this.subscription.end(CancelReason.UNSUBSCRIBE_FAILED, response.getOperation());
                }
                else {
                    SendingUnsubscribe.log.fine("Unsubscribe successful, response was: " + response);
                    SendingUnsubscribe.this.subscription.end(null, response.getOperation());
                }
            }
        });
    }
    
    static {
        log = Logger.getLogger(SendingUnsubscribe.class.getName());
    }
}
