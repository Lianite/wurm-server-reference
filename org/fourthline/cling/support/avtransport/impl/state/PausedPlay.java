// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.avtransport.impl.state;

import org.fourthline.cling.support.model.TransportAction;
import java.net.URI;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable;
import org.fourthline.cling.support.lastchange.EventedValue;
import org.fourthline.cling.support.model.TransportInfo;
import org.fourthline.cling.support.model.TransportState;
import java.util.logging.Logger;
import org.fourthline.cling.support.model.AVTransport;

public abstract class PausedPlay<T extends AVTransport> extends AbstractState<T>
{
    private static final Logger log;
    
    public PausedPlay(final T transport) {
        super(transport);
    }
    
    public void onEntry() {
        PausedPlay.log.fine("Setting transport state to PAUSED_PLAYBACK");
        this.getTransport().setTransportInfo(new TransportInfo(TransportState.PAUSED_PLAYBACK, this.getTransport().getTransportInfo().getCurrentTransportStatus(), this.getTransport().getTransportInfo().getCurrentSpeed()));
        this.getTransport().getLastChange().setEventedValue(this.getTransport().getInstanceId(), new AVTransportVariable.TransportState(TransportState.PAUSED_PLAYBACK), new AVTransportVariable.CurrentTransportActions(this.getCurrentTransportActions()));
    }
    
    public abstract Class<? extends AbstractState<?>> setTransportURI(final URI p0, final String p1);
    
    public abstract Class<? extends AbstractState<?>> stop();
    
    public abstract Class<? extends AbstractState<?>> play(final String p0);
    
    @Override
    public TransportAction[] getCurrentTransportActions() {
        return new TransportAction[] { TransportAction.Stop, TransportAction.Play };
    }
    
    static {
        log = Logger.getLogger(PausedPlay.class.getName());
    }
}
