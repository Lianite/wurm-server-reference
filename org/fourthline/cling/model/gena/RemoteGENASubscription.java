// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.gena;

import org.fourthline.cling.model.UnsupportedDataException;
import org.fourthline.cling.model.state.StateVariableValue;
import java.util.Collection;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.message.UpnpResponse;
import java.util.Iterator;
import org.fourthline.cling.model.Location;
import org.fourthline.cling.model.meta.Service;
import java.util.ArrayList;
import org.fourthline.cling.model.Namespace;
import org.fourthline.cling.model.NetworkAddress;
import java.util.List;
import org.fourthline.cling.model.meta.RemoteDevice;
import java.net.URL;
import java.beans.PropertyChangeSupport;
import org.fourthline.cling.model.meta.RemoteService;

public abstract class RemoteGENASubscription extends GENASubscription<RemoteService>
{
    protected PropertyChangeSupport propertyChangeSupport;
    
    protected RemoteGENASubscription(final RemoteService service, final int requestedDurationSeconds) {
        super(service, requestedDurationSeconds);
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }
    
    public synchronized URL getEventSubscriptionURL() {
        return ((Service<RemoteDevice, S>)this.getService()).getDevice().normalizeURI(this.getService().getEventSubscriptionURI());
    }
    
    public synchronized List<URL> getEventCallbackURLs(final List<NetworkAddress> activeStreamServers, final Namespace namespace) {
        final List<URL> callbackURLs = new ArrayList<URL>();
        for (final NetworkAddress activeStreamServer : activeStreamServers) {
            callbackURLs.add(new Location(activeStreamServer, namespace.getEventCallbackPathString(this.getService())).getURL());
        }
        return callbackURLs;
    }
    
    public synchronized void establish() {
        this.established();
    }
    
    public synchronized void fail(final UpnpResponse responseStatus) {
        this.failed(responseStatus);
    }
    
    public synchronized void end(final CancelReason reason, final UpnpResponse response) {
        this.ended(reason, response);
    }
    
    public synchronized void receive(final UnsignedIntegerFourBytes sequence, final Collection<StateVariableValue> newValues) {
        if (this.currentSequence != null) {
            if (this.currentSequence.getValue().equals(this.currentSequence.getBits().getMaxValue()) && sequence.getValue() == 1L) {
                System.err.println("TODO: HANDLE ROLLOVER");
                return;
            }
            if (this.currentSequence.getValue() >= sequence.getValue()) {
                return;
            }
            final long expectedValue = this.currentSequence.getValue() + 1L;
            final int difference;
            if ((difference = (int)(sequence.getValue() - expectedValue)) != 0) {
                this.eventsMissed(difference);
            }
        }
        this.currentSequence = sequence;
        for (final StateVariableValue newValue : newValues) {
            this.currentValues.put(newValue.getStateVariable().getName(), newValue);
        }
        this.eventReceived();
    }
    
    public abstract void invalidMessage(final UnsupportedDataException p0);
    
    public abstract void failed(final UpnpResponse p0);
    
    public abstract void ended(final CancelReason p0, final UpnpResponse p1);
    
    public abstract void eventsMissed(final int p0);
    
    @Override
    public String toString() {
        return "(SID: " + this.getSubscriptionId() + ") " + ((GENASubscription<Object>)this).getService();
    }
}
