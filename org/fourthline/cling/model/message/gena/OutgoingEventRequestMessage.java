// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.gena;

import org.fourthline.cling.model.message.header.EventSequenceHeader;
import org.fourthline.cling.model.message.header.SubscriptionIdHeader;
import org.fourthline.cling.model.message.header.NTSHeader;
import org.fourthline.cling.model.types.NotificationSubtype;
import org.fourthline.cling.model.message.header.NTEventHeader;
import org.fourthline.cling.model.message.header.ContentTypeHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.message.UpnpRequest;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import java.net.URL;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.state.StateVariableValue;
import java.util.Collection;
import org.fourthline.cling.model.message.StreamRequestMessage;

public class OutgoingEventRequestMessage extends StreamRequestMessage
{
    private final Collection<StateVariableValue> stateVariableValues;
    
    public OutgoingEventRequestMessage(final GENASubscription subscription, final URL callbackURL, final UnsignedIntegerFourBytes sequence, final Collection<StateVariableValue> values) {
        super(new UpnpRequest(UpnpRequest.Method.NOTIFY, callbackURL));
        this.getHeaders().add(UpnpHeader.Type.CONTENT_TYPE, new ContentTypeHeader());
        this.getHeaders().add(UpnpHeader.Type.NT, new NTEventHeader());
        this.getHeaders().add(UpnpHeader.Type.NTS, new NTSHeader(NotificationSubtype.PROPCHANGE));
        this.getHeaders().add(UpnpHeader.Type.SID, new SubscriptionIdHeader(subscription.getSubscriptionId()));
        this.getHeaders().add(UpnpHeader.Type.SEQ, new EventSequenceHeader(sequence.getValue()));
        this.stateVariableValues = values;
    }
    
    public OutgoingEventRequestMessage(final GENASubscription subscription, final URL callbackURL) {
        this(subscription, callbackURL, subscription.getCurrentSequence(), subscription.getCurrentValues().values());
    }
    
    public Collection<StateVariableValue> getStateVariableValues() {
        return this.stateVariableValues;
    }
}
