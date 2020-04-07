// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.avtransport.impl.state;

import org.fourthline.cling.support.model.TransportAction;
import org.fourthline.cling.support.model.SeekMode;
import java.net.URI;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable;
import org.fourthline.cling.support.lastchange.EventedValue;
import org.fourthline.cling.support.model.TransportInfo;
import org.fourthline.cling.support.model.TransportState;
import java.util.logging.Logger;
import org.fourthline.cling.support.model.AVTransport;

public abstract class Stopped<T extends AVTransport> extends AbstractState<T>
{
    private static final Logger log;
    
    public Stopped(final T transport) {
        super(transport);
    }
    
    public void onEntry() {
        Stopped.log.fine("Setting transport state to STOPPED");
        this.getTransport().setTransportInfo(new TransportInfo(TransportState.STOPPED, this.getTransport().getTransportInfo().getCurrentTransportStatus(), this.getTransport().getTransportInfo().getCurrentSpeed()));
        this.getTransport().getLastChange().setEventedValue(this.getTransport().getInstanceId(), new AVTransportVariable.TransportState(TransportState.STOPPED), new AVTransportVariable.CurrentTransportActions(this.getCurrentTransportActions()));
    }
    
    public abstract Class<? extends AbstractState<?>> setTransportURI(final URI p0, final String p1);
    
    public abstract Class<? extends AbstractState<?>> stop();
    
    public abstract Class<? extends AbstractState<?>> play(final String p0);
    
    public abstract Class<? extends AbstractState<?>> next();
    
    public abstract Class<? extends AbstractState<?>> previous();
    
    public abstract Class<? extends AbstractState<?>> seek(final SeekMode p0, final String p1);
    
    @Override
    public TransportAction[] getCurrentTransportActions() {
        return new TransportAction[] { TransportAction.Stop, TransportAction.Play, TransportAction.Next, TransportAction.Previous, TransportAction.Seek };
    }
    
    static {
        log = Logger.getLogger(Stopped.class.getName());
    }
}
