// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.mock;

import org.fourthline.cling.model.message.StreamResponseMessage;
import java.net.UnknownHostException;
import java.util.Arrays;
import org.fourthline.cling.model.NetworkAddress;
import java.net.InetAddress;
import org.fourthline.cling.transport.spi.InitializationException;
import org.fourthline.cling.transport.RouterException;
import java.util.ArrayList;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.transport.spi.UpnpStream;
import org.fourthline.cling.model.message.OutgoingDatagramMessage;
import org.fourthline.cling.model.message.IncomingDatagramMessage;
import java.util.List;
import javax.enterprise.inject.Alternative;
import org.fourthline.cling.transport.Router;

@Alternative
public class MockRouter implements Router
{
    public int counter;
    public List<IncomingDatagramMessage> incomingDatagramMessages;
    public List<OutgoingDatagramMessage> outgoingDatagramMessages;
    public List<UpnpStream> receivedUpnpStreams;
    public List<StreamRequestMessage> sentStreamRequestMessages;
    public List<byte[]> broadcastedBytes;
    protected UpnpServiceConfiguration configuration;
    protected ProtocolFactory protocolFactory;
    
    public MockRouter(final UpnpServiceConfiguration configuration, final ProtocolFactory protocolFactory) {
        this.counter = -1;
        this.incomingDatagramMessages = new ArrayList<IncomingDatagramMessage>();
        this.outgoingDatagramMessages = new ArrayList<OutgoingDatagramMessage>();
        this.receivedUpnpStreams = new ArrayList<UpnpStream>();
        this.sentStreamRequestMessages = new ArrayList<StreamRequestMessage>();
        this.broadcastedBytes = new ArrayList<byte[]>();
        this.configuration = configuration;
        this.protocolFactory = protocolFactory;
    }
    
    @Override
    public UpnpServiceConfiguration getConfiguration() {
        return this.configuration;
    }
    
    @Override
    public ProtocolFactory getProtocolFactory() {
        return this.protocolFactory;
    }
    
    @Override
    public boolean enable() throws RouterException {
        return false;
    }
    
    @Override
    public boolean disable() throws RouterException {
        return false;
    }
    
    @Override
    public void shutdown() throws RouterException {
    }
    
    @Override
    public boolean isEnabled() throws RouterException {
        return false;
    }
    
    @Override
    public void handleStartFailure(final InitializationException ex) throws InitializationException {
    }
    
    @Override
    public List<NetworkAddress> getActiveStreamServers(final InetAddress preferredAddress) throws RouterException {
        try {
            return Arrays.asList(new NetworkAddress(InetAddress.getByName("127.0.0.1"), 0));
        }
        catch (UnknownHostException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @Override
    public void received(final IncomingDatagramMessage msg) {
        this.incomingDatagramMessages.add(msg);
    }
    
    @Override
    public void received(final UpnpStream stream) {
        this.receivedUpnpStreams.add(stream);
    }
    
    @Override
    public void send(final OutgoingDatagramMessage msg) throws RouterException {
        this.outgoingDatagramMessages.add(msg);
    }
    
    @Override
    public StreamResponseMessage send(final StreamRequestMessage msg) throws RouterException {
        this.sentStreamRequestMessages.add(msg);
        ++this.counter;
        return (this.getStreamResponseMessages() != null) ? this.getStreamResponseMessages()[this.counter] : this.getStreamResponseMessage(msg);
    }
    
    @Override
    public void broadcast(final byte[] bytes) {
        this.broadcastedBytes.add(bytes);
    }
    
    public void resetStreamRequestMessageCounter() {
        this.counter = -1;
    }
    
    public List<IncomingDatagramMessage> getIncomingDatagramMessages() {
        return this.incomingDatagramMessages;
    }
    
    public List<OutgoingDatagramMessage> getOutgoingDatagramMessages() {
        return this.outgoingDatagramMessages;
    }
    
    public List<UpnpStream> getReceivedUpnpStreams() {
        return this.receivedUpnpStreams;
    }
    
    public List<StreamRequestMessage> getSentStreamRequestMessages() {
        return this.sentStreamRequestMessages;
    }
    
    public List<byte[]> getBroadcastedBytes() {
        return this.broadcastedBytes;
    }
    
    public StreamResponseMessage[] getStreamResponseMessages() {
        return null;
    }
    
    public StreamResponseMessage getStreamResponseMessage(final StreamRequestMessage request) {
        return null;
    }
}
