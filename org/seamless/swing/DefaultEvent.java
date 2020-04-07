// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.swing;

import java.util.HashSet;
import java.util.Set;

public class DefaultEvent<PAYLOAD> implements Event
{
    PAYLOAD payload;
    Set<Controller> firedInControllers;
    
    public DefaultEvent() {
        this.firedInControllers = new HashSet<Controller>();
    }
    
    public DefaultEvent(final PAYLOAD payload) {
        this.firedInControllers = new HashSet<Controller>();
        this.payload = payload;
    }
    
    public PAYLOAD getPayload() {
        return this.payload;
    }
    
    public void setPayload(final PAYLOAD payload) {
        this.payload = payload;
    }
    
    public void addFiredInController(final Controller seenController) {
        this.firedInControllers.add(seenController);
    }
    
    public boolean alreadyFired(final Controller controller) {
        return this.firedInControllers.contains(controller);
    }
}
