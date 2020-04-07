// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.protocol.sync;

import org.fourthline.cling.transport.RouterException;
import java.util.Iterator;
import org.fourthline.cling.model.gena.GENASubscription;
import java.net.URL;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.gena.LocalGENASubscription;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import java.util.logging.Logger;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.gena.OutgoingEventRequestMessage;
import org.fourthline.cling.protocol.SendingSync;

public class SendingEvent extends SendingSync<OutgoingEventRequestMessage, StreamResponseMessage>
{
    private static final Logger log;
    protected final String subscriptionId;
    protected final OutgoingEventRequestMessage[] requestMessages;
    protected final UnsignedIntegerFourBytes currentSequence;
    
    public SendingEvent(final UpnpService upnpService, final LocalGENASubscription subscription) {
        super(upnpService, null);
        this.subscriptionId = subscription.getSubscriptionId();
        this.requestMessages = new OutgoingEventRequestMessage[subscription.getCallbackURLs().size()];
        int i = 0;
        for (final URL url : subscription.getCallbackURLs()) {
            this.requestMessages[i] = new OutgoingEventRequestMessage(subscription, url);
            this.getUpnpService().getConfiguration().getGenaEventProcessor().writeBody(this.requestMessages[i]);
            ++i;
        }
        this.currentSequence = subscription.getCurrentSequence();
        subscription.incrementSequence();
    }
    
    @Override
    protected StreamResponseMessage executeSync() throws RouterException {
        SendingEvent.log.fine("Sending event for subscription: " + this.subscriptionId);
        StreamResponseMessage lastResponse = null;
        for (final OutgoingEventRequestMessage requestMessage : this.requestMessages) {
            if (this.currentSequence.getValue() == 0L) {
                SendingEvent.log.fine("Sending initial event message to callback URL: " + requestMessage.getUri());
            }
            else {
                SendingEvent.log.fine("Sending event message '" + this.currentSequence + "' to callback URL: " + requestMessage.getUri());
            }
            lastResponse = this.getUpnpService().getRouter().send(requestMessage);
            SendingEvent.log.fine("Received event callback response: " + lastResponse);
        }
        return lastResponse;
    }
    
    static {
        log = Logger.getLogger(SendingEvent.class.getName());
    }
}
