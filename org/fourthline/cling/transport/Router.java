// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport;

import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.OutgoingDatagramMessage;
import org.fourthline.cling.transport.spi.UpnpStream;
import org.fourthline.cling.model.message.IncomingDatagramMessage;
import org.fourthline.cling.model.NetworkAddress;
import java.util.List;
import java.net.InetAddress;
import org.fourthline.cling.transport.spi.InitializationException;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.UpnpServiceConfiguration;

public interface Router
{
    UpnpServiceConfiguration getConfiguration();
    
    ProtocolFactory getProtocolFactory();
    
    boolean enable() throws RouterException;
    
    boolean disable() throws RouterException;
    
    void shutdown() throws RouterException;
    
    boolean isEnabled() throws RouterException;
    
    void handleStartFailure(final InitializationException p0) throws InitializationException;
    
    List<NetworkAddress> getActiveStreamServers(final InetAddress p0) throws RouterException;
    
    void received(final IncomingDatagramMessage p0);
    
    void received(final UpnpStream p0);
    
    void send(final OutgoingDatagramMessage p0) throws RouterException;
    
    StreamResponseMessage send(final StreamRequestMessage p0) throws RouterException;
    
    void broadcast(final byte[] p0) throws RouterException;
}
