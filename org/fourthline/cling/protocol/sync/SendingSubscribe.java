// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.protocol.sync;

import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.transport.RouterException;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.NetworkAddress;
import java.util.List;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.gena.RemoteGENASubscription;
import java.util.logging.Logger;
import org.fourthline.cling.model.message.gena.IncomingSubscribeResponseMessage;
import org.fourthline.cling.model.message.gena.OutgoingSubscribeRequestMessage;
import org.fourthline.cling.protocol.SendingSync;

public class SendingSubscribe extends SendingSync<OutgoingSubscribeRequestMessage, IncomingSubscribeResponseMessage>
{
    private static final Logger log;
    protected final RemoteGENASubscription subscription;
    
    public SendingSubscribe(final UpnpService upnpService, final RemoteGENASubscription subscription, final List<NetworkAddress> activeStreamServers) {
        super(upnpService, new OutgoingSubscribeRequestMessage(subscription, subscription.getEventCallbackURLs(activeStreamServers, upnpService.getConfiguration().getNamespace()), upnpService.getConfiguration().getEventSubscriptionHeaders(subscription.getService())));
        this.subscription = subscription;
    }
    
    @Override
    protected IncomingSubscribeResponseMessage executeSync() throws RouterException {
        if (!((SendingSync<OutgoingSubscribeRequestMessage, OUT>)this).getInputMessage().hasCallbackURLs()) {
            SendingSubscribe.log.fine("Subscription failed, no active local callback URLs available (network disabled?)");
            this.getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    SendingSubscribe.this.subscription.fail(null);
                }
            });
            return null;
        }
        SendingSubscribe.log.fine("Sending subscription request: " + ((SendingSync<Object, OUT>)this).getInputMessage());
        try {
            this.getUpnpService().getRegistry().registerPendingRemoteSubscription(this.subscription);
            StreamResponseMessage response = null;
            try {
                response = this.getUpnpService().getRouter().send(((SendingSync<StreamRequestMessage, OUT>)this).getInputMessage());
            }
            catch (RouterException ex) {
                this.onSubscriptionFailure();
                return null;
            }
            if (response == null) {
                this.onSubscriptionFailure();
                return null;
            }
            final IncomingSubscribeResponseMessage responseMessage = new IncomingSubscribeResponseMessage(response);
            if (response.getOperation().isFailed()) {
                SendingSubscribe.log.fine("Subscription failed, response was: " + responseMessage);
                this.getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        SendingSubscribe.this.subscription.fail(responseMessage.getOperation());
                    }
                });
            }
            else if (!responseMessage.isValidHeaders()) {
                SendingSubscribe.log.severe("Subscription failed, invalid or missing (SID, Timeout) response headers");
                this.getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        SendingSubscribe.this.subscription.fail(responseMessage.getOperation());
                    }
                });
            }
            else {
                SendingSubscribe.log.fine("Subscription established, adding to registry, response was: " + response);
                this.subscription.setSubscriptionId(responseMessage.getSubscriptionId());
                this.subscription.setActualSubscriptionDurationSeconds(responseMessage.getSubscriptionDurationSeconds());
                this.getUpnpService().getRegistry().addRemoteSubscription(this.subscription);
                this.getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        SendingSubscribe.this.subscription.establish();
                    }
                });
            }
            return responseMessage;
        }
        finally {
            this.getUpnpService().getRegistry().unregisterPendingRemoteSubscription(this.subscription);
        }
    }
    
    protected void onSubscriptionFailure() {
        SendingSubscribe.log.fine("Subscription failed");
        this.getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(new Runnable() {
            @Override
            public void run() {
                SendingSubscribe.this.subscription.fail(null);
            }
        });
    }
    
    static {
        log = Logger.getLogger(SendingSubscribe.class.getName());
    }
}
