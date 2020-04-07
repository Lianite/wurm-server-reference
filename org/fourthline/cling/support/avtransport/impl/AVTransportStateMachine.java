// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.avtransport.impl;

import org.fourthline.cling.support.model.SeekMode;
import java.net.URI;
import org.fourthline.cling.support.avtransport.impl.state.AbstractState;
import org.seamless.statemachine.StateMachine;

public interface AVTransportStateMachine extends StateMachine<AbstractState>
{
    void setTransportURI(final URI p0, final String p1);
    
    void setNextTransportURI(final URI p0, final String p1);
    
    void stop();
    
    void play(final String p0);
    
    void pause();
    
    void record();
    
    void seek(final SeekMode p0, final String p1);
    
    void next();
    
    void previous();
}
