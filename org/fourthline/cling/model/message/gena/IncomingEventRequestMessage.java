// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.gena;

import org.fourthline.cling.model.types.NotificationSubtype;
import org.fourthline.cling.model.message.header.NTSHeader;
import org.fourthline.cling.model.message.header.NTEventHeader;
import org.fourthline.cling.model.message.header.EventSequenceHeader;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.message.header.SubscriptionIdHeader;
import java.util.ArrayList;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.state.StateVariableValue;
import java.util.List;
import org.fourthline.cling.model.message.StreamRequestMessage;

public class IncomingEventRequestMessage extends StreamRequestMessage
{
    private final List<StateVariableValue> stateVariableValues;
    private final RemoteService service;
    
    public IncomingEventRequestMessage(final StreamRequestMessage source, final RemoteService service) {
        super(source);
        this.stateVariableValues = new ArrayList<StateVariableValue>();
        this.service = service;
    }
    
    public RemoteService getService() {
        return this.service;
    }
    
    public List<StateVariableValue> getStateVariableValues() {
        return this.stateVariableValues;
    }
    
    public String getSubscrptionId() {
        final SubscriptionIdHeader header = this.getHeaders().getFirstHeader(UpnpHeader.Type.SID, SubscriptionIdHeader.class);
        return (header != null) ? header.getValue() : null;
    }
    
    public UnsignedIntegerFourBytes getSequence() {
        final EventSequenceHeader header = this.getHeaders().getFirstHeader(UpnpHeader.Type.SEQ, EventSequenceHeader.class);
        return (header != null) ? header.getValue() : null;
    }
    
    public boolean hasNotificationHeaders() {
        final UpnpHeader ntHeader = this.getHeaders().getFirstHeader(UpnpHeader.Type.NT);
        final UpnpHeader ntsHeader = this.getHeaders().getFirstHeader(UpnpHeader.Type.NTS);
        return ntHeader != null && ntHeader.getValue() != null && ntsHeader != null && ntsHeader.getValue() != null;
    }
    
    public boolean hasValidNotificationHeaders() {
        final NTEventHeader ntHeader = this.getHeaders().getFirstHeader(UpnpHeader.Type.NT, NTEventHeader.class);
        final NTSHeader ntsHeader = this.getHeaders().getFirstHeader(UpnpHeader.Type.NTS, NTSHeader.class);
        return ntHeader != null && ntHeader.getValue() != null && ntsHeader != null && ntsHeader.getValue().equals(NotificationSubtype.PROPCHANGE);
    }
    
    @Override
    public String toString() {
        return super.toString() + " SEQUENCE: " + this.getSequence().getValue();
    }
}
