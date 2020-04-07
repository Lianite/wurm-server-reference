// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.avtransport.impl.state;

import org.fourthline.cling.support.model.TransportAction;
import org.fourthline.cling.support.model.AVTransport;

public abstract class AbstractState<T extends AVTransport>
{
    private T transport;
    
    public AbstractState(final T transport) {
        this.transport = transport;
    }
    
    public T getTransport() {
        return this.transport;
    }
    
    public abstract TransportAction[] getCurrentTransportActions();
}
