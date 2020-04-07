// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.protocol.sync;

import org.fourthline.cling.protocol.ReceivingAsync;
import org.fourthline.cling.transport.RouterException;
import org.fourthline.cling.model.gena.LocalGENASubscription;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.gena.IncomingUnsubscribeRequestMessage;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.resource.ServiceEventSubscriptionResource;
import org.fourthline.cling.UpnpService;
import java.util.logging.Logger;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.protocol.ReceivingSync;

public class ReceivingUnsubscribe extends ReceivingSync<StreamRequestMessage, StreamResponseMessage>
{
    private static final Logger log;
    
    public ReceivingUnsubscribe(final UpnpService upnpService, final StreamRequestMessage inputMessage) {
        super(upnpService, inputMessage);
    }
    
    @Override
    protected StreamResponseMessage executeSync() throws RouterException {
        final ServiceEventSubscriptionResource resource = this.getUpnpService().getRegistry().getResource(ServiceEventSubscriptionResource.class, ((ReceivingAsync<StreamRequestMessage>)this).getInputMessage().getUri());
        if (resource == null) {
            ReceivingUnsubscribe.log.fine("No local resource found: " + ((ReceivingAsync<Object>)this).getInputMessage());
            return null;
        }
        ReceivingUnsubscribe.log.fine("Found local event subscription matching relative request URI: " + ((ReceivingAsync<StreamRequestMessage>)this).getInputMessage().getUri());
        final IncomingUnsubscribeRequestMessage requestMessage = new IncomingUnsubscribeRequestMessage(((ReceivingAsync<StreamRequestMessage>)this).getInputMessage(), resource.getModel());
        if (requestMessage.getSubscriptionId() != null && (requestMessage.hasNotificationHeader() || requestMessage.hasCallbackHeader())) {
            ReceivingUnsubscribe.log.fine("Subscription ID and NT or Callback in unsubcribe request: " + ((ReceivingAsync<Object>)this).getInputMessage());
            return new StreamResponseMessage(UpnpResponse.Status.BAD_REQUEST);
        }
        final LocalGENASubscription subscription = this.getUpnpService().getRegistry().getLocalSubscription(requestMessage.getSubscriptionId());
        if (subscription == null) {
            ReceivingUnsubscribe.log.fine("Invalid subscription ID for unsubscribe request: " + ((ReceivingAsync<Object>)this).getInputMessage());
            return new StreamResponseMessage(UpnpResponse.Status.PRECONDITION_FAILED);
        }
        ReceivingUnsubscribe.log.fine("Unregistering subscription: " + subscription);
        if (this.getUpnpService().getRegistry().removeLocalSubscription(subscription)) {
            subscription.end(null);
        }
        else {
            ReceivingUnsubscribe.log.fine("Subscription was already removed from registry");
        }
        return new StreamResponseMessage(UpnpResponse.Status.OK);
    }
    
    static {
        log = Logger.getLogger(ReceivingUnsubscribe.class.getName());
    }
}
