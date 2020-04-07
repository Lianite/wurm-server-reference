// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.meta;

public class StateVariableEventDetails
{
    private final boolean sendEvents;
    private final int eventMaximumRateMilliseconds;
    private final int eventMinimumDelta;
    
    public StateVariableEventDetails() {
        this(true, 0, 0);
    }
    
    public StateVariableEventDetails(final boolean sendEvents) {
        this(sendEvents, 0, 0);
    }
    
    public StateVariableEventDetails(final boolean sendEvents, final int eventMaximumRateMilliseconds, final int eventMinimumDelta) {
        this.sendEvents = sendEvents;
        this.eventMaximumRateMilliseconds = eventMaximumRateMilliseconds;
        this.eventMinimumDelta = eventMinimumDelta;
    }
    
    public boolean isSendEvents() {
        return this.sendEvents;
    }
    
    public int getEventMaximumRateMilliseconds() {
        return this.eventMaximumRateMilliseconds;
    }
    
    public int getEventMinimumDelta() {
        return this.eventMinimumDelta;
    }
}
