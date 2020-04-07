// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.protocol.sync;

import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.transport.RouterException;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.gena.RemoteGENASubscription;
import java.util.logging.Logger;
import org.fourthline.cling.model.message.gena.IncomingSubscribeResponseMessage;
import org.fourthline.cling.model.message.gena.OutgoingRenewalRequestMessage;
import org.fourthline.cling.protocol.SendingSync;

public class SendingRenewal extends SendingSync<OutgoingRenewalRequestMessage, IncomingSubscribeResponseMessage>
{
    private static final Logger log;
    protected final RemoteGENASubscription subscription;
    
    public SendingRenewal(final UpnpService upnpService, final RemoteGENASubscription subscription) {
        super(upnpService, new OutgoingRenewalRequestMessage(subscription, upnpService.getConfiguration().getEventSubscriptionHeaders(subscription.getService())));
        this.subscription = subscription;
    }
    
    @Override
    protected IncomingSubscribeResponseMessage executeSync() throws RouterException {
        SendingRenewal.log.fine("Sending subscription renewal request: " + ((SendingSync<Object, OUT>)this).getInputMessage());
        StreamResponseMessage response;
        try {
            response = this.getUpnpService().getRouter().send(((SendingSync<StreamRequestMessage, OUT>)this).getInputMessage());
        }
        catch (RouterException ex) {
            this.onRenewalFailure();
            throw ex;
        }
        if (response == null) {
            this.onRenewalFailure();
            return null;
        }
        final IncomingSubscribeResponseMessage responseMessage = new IncomingSubscribeResponseMessage(response);
        if (response.getOperation().isFailed()) {
            SendingRenewal.log.fine("Subscription renewal failed, response was: " + response);
            this.getUpnpService().getRegistry().removeRemoteSubscription(this.subscription);
            this.getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    SendingRenewal.this.subscription.end(CancelReason.RENEWAL_FAILED, responseMessage.getOperation());
                }
            });
        }
        else if (!responseMessage.isValidHeaders()) {
            SendingRenewal.log.severe("Subscription renewal failed, invalid or missing (SID, Timeout) response headers");
            this.getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    SendingRenewal.this.subscription.end(CancelReason.RENEWAL_FAILED, responseMessage.getOperation());
                }
            });
        }
        else {
            SendingRenewal.log.fine("Subscription renewed, updating in registry, response was: " + response);
            this.subscription.setActualSubscriptionDurationSeconds(responseMessage.getSubscriptionDurationSeconds());
            this.getUpnpService().getRegistry().updateRemoteSubscription(this.subscription);
        }
        return responseMessage;
    }
    
    protected void onRenewalFailure() {
        SendingRenewal.log.fine("Subscription renewal failed, removing subscription from registry");
        this.getUpnpService().getRegistry().removeRemoteSubscription(this.subscription);
        this.getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(new Runnable() {
            @Override
            public void run() {
                SendingRenewal.this.subscription.end(CancelReason.RENEWAL_FAILED, null);
            }
        });
    }
    
    static {
        log = Logger.getLogger(SendingRenewal.class.getName());
    }
}
