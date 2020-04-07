// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message;

import java.net.InetAddress;

public class IncomingDatagramMessage<O extends UpnpOperation> extends UpnpMessage<O>
{
    private InetAddress sourceAddress;
    private int sourcePort;
    private InetAddress localAddress;
    
    public IncomingDatagramMessage(final O operation, final InetAddress sourceAddress, final int sourcePort, final InetAddress localAddress) {
        super(operation);
        this.sourceAddress = sourceAddress;
        this.sourcePort = sourcePort;
        this.localAddress = localAddress;
    }
    
    protected IncomingDatagramMessage(final IncomingDatagramMessage<O> source) {
        super(source);
        this.sourceAddress = source.getSourceAddress();
        this.sourcePort = source.getSourcePort();
        this.localAddress = source.getLocalAddress();
    }
    
    public InetAddress getSourceAddress() {
        return this.sourceAddress;
    }
    
    public int getSourcePort() {
        return this.sourcePort;
    }
    
    public InetAddress getLocalAddress() {
        return this.localAddress;
    }
}
