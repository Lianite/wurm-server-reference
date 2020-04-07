// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.spi;

import java.net.DatagramPacket;
import org.fourthline.cling.model.message.OutgoingDatagramMessage;
import org.fourthline.cling.transport.Router;
import java.net.InetAddress;

public interface DatagramIO<C extends DatagramIOConfiguration> extends Runnable
{
    void init(final InetAddress p0, final Router p1, final DatagramProcessor p2) throws InitializationException;
    
    void stop();
    
    C getConfiguration();
    
    void send(final OutgoingDatagramMessage p0);
    
    void send(final DatagramPacket p0);
}
