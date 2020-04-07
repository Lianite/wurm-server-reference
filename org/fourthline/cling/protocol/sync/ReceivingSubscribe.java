// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.protocol.sync;

import org.fourthline.cling.model.message.UpnpMessage;
import org.fourthline.cling.protocol.ReceivingAsync;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.seamless.util.Exceptions;
import org.fourthline.cling.model.gena.CancelReason;
import java.net.URL;
import java.util.List;
import org.fourthline.cling.transport.RouterException;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.gena.IncomingSubscribeRequestMessage;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.resource.ServiceEventSubscriptionResource;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.gena.LocalGENASubscription;
import java.util.logging.Logger;
import org.fourthline.cling.model.message.gena.OutgoingSubscribeResponseMessage;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.protocol.ReceivingSync;

public class ReceivingSubscribe extends ReceivingSync<StreamRequestMessage, OutgoingSubscribeResponseMessage>
{
    private static final Logger log;
    protected LocalGENASubscription subscription;
    
    public ReceivingSubscribe(final UpnpService upnpService, final StreamRequestMessage inputMessage) {
        super(upnpService, inputMessage);
    }
    
    @Override
    protected OutgoingSubscribeResponseMessage executeSync() throws RouterException {
        final ServiceEventSubscriptionResource resource = this.getUpnpService().getRegistry().getResource(ServiceEventSubscriptionResource.class, ((ReceivingAsync<StreamRequestMessage>)this).getInputMessage().getUri());
        if (resource == null) {
            ReceivingSubscribe.log.fine("No local resource found: " + ((ReceivingAsync<Object>)this).getInputMessage());
            return null;
        }
        ReceivingSubscribe.log.fine("Found local event subscription matching relative request URI: " + ((ReceivingAsync<StreamRequestMessage>)this).getInputMessage().getUri());
        final IncomingSubscribeRequestMessage requestMessage = new IncomingSubscribeRequestMessage(((ReceivingAsync<StreamRequestMessage>)this).getInputMessage(), resource.getModel());
        if (requestMessage.getSubscriptionId() != null && (requestMessage.hasNotificationHeader() || requestMessage.getCallbackURLs() != null)) {
            ReceivingSubscribe.log.fine("Subscription ID and NT or Callback in subscribe request: " + ((ReceivingAsync<Object>)this).getInputMessage());
            return new OutgoingSubscribeResponseMessage(UpnpResponse.Status.BAD_REQUEST);
        }
        if (requestMessage.getSubscriptionId() != null) {
            return this.processRenewal(resource.getModel(), requestMessage);
        }
        if (requestMessage.hasNotificationHeader() && requestMessage.getCallbackURLs() != null) {
            return this.processNewSubscription(resource.getModel(), requestMessage);
        }
        ReceivingSubscribe.log.fine("No subscription ID, no NT or Callback, neither subscription or renewal: " + ((ReceivingAsync<Object>)this).getInputMessage());
        return new OutgoingSubscribeResponseMessage(UpnpResponse.Status.PRECONDITION_FAILED);
    }
    
    protected OutgoingSubscribeResponseMessage processRenewal(final LocalService service, final IncomingSubscribeRequestMessage requestMessage) {
        this.subscription = this.getUpnpService().getRegistry().getLocalSubscription(requestMessage.getSubscriptionId());
        if (this.subscription == null) {
            ReceivingSubscribe.log.fine("Invalid subscription ID for renewal request: " + ((ReceivingAsync<Object>)this).getInputMessage());
            return new OutgoingSubscribeResponseMessage(UpnpResponse.Status.PRECONDITION_FAILED);
        }
        ReceivingSubscribe.log.fine("Renewing subscription: " + this.subscription);
        this.subscription.setSubscriptionDuration(requestMessage.getRequestedTimeoutSeconds());
        if (this.getUpnpService().getRegistry().updateLocalSubscription(this.subscription)) {
            return new OutgoingSubscribeResponseMessage(this.subscription);
        }
        ReceivingSubscribe.log.fine("Subscription went away before it could be renewed: " + ((ReceivingAsync<Object>)this).getInputMessage());
        return new OutgoingSubscribeResponseMessage(UpnpResponse.Status.PRECONDITION_FAILED);
    }
    
    protected OutgoingSubscribeResponseMessage processNewSubscription(final LocalService service, final IncomingSubscribeRequestMessage requestMessage) {
        final List<URL> callbackURLs = requestMessage.getCallbackURLs();
        if (callbackURLs == null || callbackURLs.size() == 0) {
            ReceivingSubscribe.log.fine("Missing or invalid Callback URLs in subscribe request: " + ((ReceivingAsync<Object>)this).getInputMessage());
            return new OutgoingSubscribeResponseMessage(UpnpResponse.Status.PRECONDITION_FAILED);
        }
        if (!requestMessage.hasNotificationHeader()) {
            ReceivingSubscribe.log.fine("Missing or invalid NT header in subscribe request: " + ((ReceivingAsync<Object>)this).getInputMessage());
            return new OutgoingSubscribeResponseMessage(UpnpResponse.Status.PRECONDITION_FAILED);
        }
        Integer timeoutSeconds;
        if (this.getUpnpService().getConfiguration().isReceivedSubscriptionTimeoutIgnored()) {
            timeoutSeconds = null;
        }
        else {
            timeoutSeconds = requestMessage.getRequestedTimeoutSeconds();
        }
        try {
            this.subscription = new LocalGENASubscription(service, timeoutSeconds, callbackURLs) {
                @Override
                public void established() {
                }
                
                @Override
                public void ended(final CancelReason reason) {
                }
                
                @Override
                public void eventReceived() {
                    ReceivingSubscribe.this.getUpnpService().getConfiguration().getSyncProtocolExecutorService().execute(ReceivingSubscribe.this.getUpnpService().getProtocolFactory().createSendingEvent(this));
                }
            };
        }
        catch (Exception ex) {
            ReceivingSubscribe.log.warning("Couldn't create local subscription to service: " + Exceptions.unwrap(ex));
            return new OutgoingSubscribeResponseMessage(UpnpResponse.Status.INTERNAL_SERVER_ERROR);
        }
        ReceivingSubscribe.log.fine("Adding subscription to registry: " + this.subscription);
        this.getUpnpService().getRegistry().addLocalSubscription(this.subscription);
        ReceivingSubscribe.log.fine("Returning subscription response, waiting to send initial event");
        return new OutgoingSubscribeResponseMessage(this.subscription);
    }
    
    @Override
    public void responseSent(final StreamResponseMessage responseMessage) {
        if (this.subscription == null) {
            return;
        }
        if (responseMessage != null && !responseMessage.getOperation().isFailed() && this.subscription.getCurrentSequence().getValue() == 0L) {
            ReceivingSubscribe.log.fine("Establishing subscription");
            this.subscription.registerOnService();
            this.subscription.establish();
            ReceivingSubscribe.log.fine("Response to subscription sent successfully, now sending initial event asynchronously");
            this.getUpnpService().getConfiguration().getAsyncProtocolExecutor().execute(this.getUpnpService().getProtocolFactory().createSendingEvent(this.subscription));
        }
        else if (this.subscription.getCurrentSequence().getValue() == 0L) {
            ReceivingSubscribe.log.fine("Subscription request's response aborted, not sending initial event");
            if (responseMessage == null) {
                ReceivingSubscribe.log.fine("Reason: No response at all from subscriber");
            }
            else {
                ReceivingSubscribe.log.fine("Reason: " + ((UpnpMessage<Object>)responseMessage).getOperation());
            }
            ReceivingSubscribe.log.fine("Removing subscription from registry: " + this.subscription);
            this.getUpnpService().getRegistry().removeLocalSubscription(this.subscription);
        }
    }
    
    @Override
    public void responseException(final Throwable t) {
        if (this.subscription == null) {
            return;
        }
        ReceivingSubscribe.log.fine("Response could not be send to subscriber, removing local GENA subscription: " + this.subscription);
        this.getUpnpService().getRegistry().removeLocalSubscription(this.subscription);
    }
    
    static {
        log = Logger.getLogger(ReceivingSubscribe.class.getName());
    }
}
