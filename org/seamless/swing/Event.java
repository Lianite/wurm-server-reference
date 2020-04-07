// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.swing;

public interface Event<PAYLOAD>
{
    PAYLOAD getPayload();
    
    void addFiredInController(final Controller p0);
    
    boolean alreadyFired(final Controller p0);
}
