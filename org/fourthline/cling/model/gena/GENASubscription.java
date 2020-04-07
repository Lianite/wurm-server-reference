// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.gena;

import java.util.LinkedHashMap;
import org.fourthline.cling.model.state.StateVariableValue;
import java.util.Map;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.meta.Service;

public abstract class GENASubscription<S extends Service>
{
    protected S service;
    protected String subscriptionId;
    protected int requestedDurationSeconds;
    protected int actualDurationSeconds;
    protected UnsignedIntegerFourBytes currentSequence;
    protected Map<String, StateVariableValue<S>> currentValues;
    
    protected GENASubscription(final S service) {
        this.requestedDurationSeconds = 1800;
        this.currentValues = new LinkedHashMap<String, StateVariableValue<S>>();
        this.service = service;
    }
    
    public GENASubscription(final S service, final int requestedDurationSeconds) {
        this(service);
        this.requestedDurationSeconds = requestedDurationSeconds;
    }
    
    public synchronized S getService() {
        return this.service;
    }
    
    public synchronized String getSubscriptionId() {
        return this.subscriptionId;
    }
    
    public synchronized void setSubscriptionId(final String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }
    
    public synchronized int getRequestedDurationSeconds() {
        return this.requestedDurationSeconds;
    }
    
    public synchronized int getActualDurationSeconds() {
        return this.actualDurationSeconds;
    }
    
    public synchronized void setActualSubscriptionDurationSeconds(final int seconds) {
        this.actualDurationSeconds = seconds;
    }
    
    public synchronized UnsignedIntegerFourBytes getCurrentSequence() {
        return this.currentSequence;
    }
    
    public synchronized Map<String, StateVariableValue<S>> getCurrentValues() {
        return this.currentValues;
    }
    
    public abstract void established();
    
    public abstract void eventReceived();
    
    @Override
    public String toString() {
        return "(GENASubscription, SID: " + this.getSubscriptionId() + ", SEQUENCE: " + this.getCurrentSequence() + ")";
    }
}
