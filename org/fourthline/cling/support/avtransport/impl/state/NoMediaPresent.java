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

public abstract class NoMediaPresent<T extends AVTransport> extends AbstractState<T>
{
    private static final Logger log;
    
    public NoMediaPresent(final T transport) {
        super(transport);
    }
    
    public void onEntry() {
        NoMediaPresent.log.fine("Setting transport state to NO_MEDIA_PRESENT");
        this.getTransport().setTransportInfo(new TransportInfo(TransportState.NO_MEDIA_PRESENT, this.getTransport().getTransportInfo().getCurrentTransportStatus(), this.getTransport().getTransportInfo().getCurrentSpeed()));
        this.getTransport().getLastChange().setEventedValue(this.getTransport().getInstanceId(), new AVTransportVariable.TransportState(TransportState.NO_MEDIA_PRESENT), new AVTransportVariable.CurrentTransportActions(this.getCurrentTransportActions()));
    }
    
    public abstract Class<? extends AbstractState> setTransportURI(final URI p0, final String p1);
    
    @Override
    public TransportAction[] getCurrentTransportActions() {
        return new TransportAction[] { TransportAction.Stop };
    }
    
    static {
        log = Logger.getLogger(Stopped.class.getName());
    }
}
