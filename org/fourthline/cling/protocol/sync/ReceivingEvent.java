// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.protocol.sync;

import org.fourthline.cling.protocol.ReceivingAsync;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.transport.RouterException;
import org.fourthline.cling.model.state.StateVariableValue;
import java.util.Collection;
import org.fourthline.cling.model.UnsupportedDataException;
import org.fourthline.cling.model.gena.RemoteGENASubscription;
import org.fourthline.cling.model.message.gena.IncomingEventRequestMessage;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.resource.ServiceEventCallbackResource;
import org.fourthline.cling.UpnpService;
import java.util.logging.Logger;
import org.fourthline.cling.model.message.gena.OutgoingEventResponseMessage;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.protocol.ReceivingSync;

public class ReceivingEvent extends ReceivingSync<StreamRequestMessage, OutgoingEventResponseMessage>
{
    private static final Logger log;
    
    public ReceivingEvent(final UpnpService upnpService, final StreamRequestMessage inputMessage) {
        super(upnpService, inputMessage);
    }
    
    @Override
    protected OutgoingEventResponseMessage executeSync() throws RouterException {
        if (!((ReceivingAsync<StreamRequestMessage>)this).getInputMessage().isContentTypeTextUDA()) {
            ReceivingEvent.log.warning("Received without or with invalid Content-Type: " + ((ReceivingAsync<Object>)this).getInputMessage());
        }
        final ServiceEventCallbackResource resource = this.getUpnpService().getRegistry().getResource(ServiceEventCallbackResource.class, ((ReceivingAsync<StreamRequestMessage>)this).getInputMessage().getUri());
        if (resource == null) {
            ReceivingEvent.log.fine("No local resource found: " + ((ReceivingAsync<Object>)this).getInputMessage());
            return new OutgoingEventResponseMessage(new UpnpResponse(UpnpResponse.Status.NOT_FOUND));
        }
        final IncomingEventRequestMessage requestMessage = new IncomingEventRequestMessage(((ReceivingAsync<StreamRequestMessage>)this).getInputMessage(), resource.getModel());
        if (requestMessage.getSubscrptionId() == null) {
            ReceivingEvent.log.fine("Subscription ID missing in event request: " + ((ReceivingAsync<Object>)this).getInputMessage());
            return new OutgoingEventResponseMessage(new UpnpResponse(UpnpResponse.Status.PRECONDITION_FAILED));
        }
        if (!requestMessage.hasValidNotificationHeaders()) {
            ReceivingEvent.log.fine("Missing NT and/or NTS headers in event request: " + ((ReceivingAsync<Object>)this).getInputMessage());
            return new OutgoingEventResponseMessage(new UpnpResponse(UpnpResponse.Status.BAD_REQUEST));
        }
        if (!requestMessage.hasValidNotificationHeaders()) {
            ReceivingEvent.log.fine("Invalid NT and/or NTS headers in event request: " + ((ReceivingAsync<Object>)this).getInputMessage());
            return new OutgoingEventResponseMessage(new UpnpResponse(UpnpResponse.Status.PRECONDITION_FAILED));
        }
        if (requestMessage.getSequence() == null) {
            ReceivingEvent.log.fine("Sequence missing in event request: " + ((ReceivingAsync<Object>)this).getInputMessage());
            return new OutgoingEventResponseMessage(new UpnpResponse(UpnpResponse.Status.PRECONDITION_FAILED));
        }
        try {
            this.getUpnpService().getConfiguration().getGenaEventProcessor().readBody(requestMessage);
        }
        catch (UnsupportedDataException ex) {
            ReceivingEvent.log.fine("Can't read event message request body, " + ex);
            final RemoteGENASubscription subscription = this.getUpnpService().getRegistry().getRemoteSubscription(requestMessage.getSubscrptionId());
            if (subscription != null) {
                this.getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        subscription.invalidMessage(ex);
                    }
                });
            }
            return new OutgoingEventResponseMessage(new UpnpResponse(UpnpResponse.Status.INTERNAL_SERVER_ERROR));
        }
        final RemoteGENASubscription subscription2 = this.getUpnpService().getRegistry().getWaitRemoteSubscription(requestMessage.getSubscrptionId());
        if (subscription2 == null) {
            ReceivingEvent.log.severe("Invalid subscription ID, no active subscription: " + requestMessage);
            return new OutgoingEventResponseMessage(new UpnpResponse(UpnpResponse.Status.PRECONDITION_FAILED));
        }
        this.getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(new Runnable() {
            @Override
            public void run() {
                ReceivingEvent.log.fine("Calling active subscription with event state variable values");
                subscription2.receive(requestMessage.getSequence(), requestMessage.getStateVariableValues());
            }
        });
        return new OutgoingEventResponseMessage();
    }
    
    static {
        log = Logger.getLogger(ReceivingEvent.class.getName());
    }
}
