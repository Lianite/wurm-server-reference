// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message;

import java.net.InetAddress;

public abstract class OutgoingDatagramMessage<O extends UpnpOperation> extends UpnpMessage<O>
{
    private InetAddress destinationAddress;
    private int destinationPort;
    private UpnpHeaders headers;
    
    protected OutgoingDatagramMessage(final O operation, final InetAddress destinationAddress, final int destinationPort) {
        super(operation);
        this.headers = new UpnpHeaders(false);
        this.destinationAddress = destinationAddress;
        this.destinationPort = destinationPort;
    }
    
    protected OutgoingDatagramMessage(final O operation, final BodyType bodyType, final Object body, final InetAddress destinationAddress, final int destinationPort) {
        super(operation, bodyType, body);
        this.headers = new UpnpHeaders(false);
        this.destinationAddress = destinationAddress;
        this.destinationPort = destinationPort;
    }
    
    public InetAddress getDestinationAddress() {
        return this.destinationAddress;
    }
    
    public int getDestinationPort() {
        return this.destinationPort;
    }
    
    @Override
    public UpnpHeaders getHeaders() {
        return this.headers;
    }
}
