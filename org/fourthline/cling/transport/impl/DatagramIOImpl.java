// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.impl;

import org.fourthline.cling.transport.spi.DatagramIOConfiguration;
import java.util.logging.Level;
import org.fourthline.cling.model.message.OutgoingDatagramMessage;
import org.fourthline.cling.model.UnsupportedDataException;
import java.net.SocketException;
import java.net.DatagramPacket;
import org.fourthline.cling.transport.spi.InitializationException;
import java.net.SocketAddress;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.InetSocketAddress;
import org.fourthline.cling.transport.spi.DatagramProcessor;
import org.fourthline.cling.transport.Router;
import java.util.logging.Logger;
import org.fourthline.cling.transport.spi.DatagramIO;

public class DatagramIOImpl implements DatagramIO<DatagramIOConfigurationImpl>
{
    private static Logger log;
    protected final DatagramIOConfigurationImpl configuration;
    protected Router router;
    protected DatagramProcessor datagramProcessor;
    protected InetSocketAddress localAddress;
    protected MulticastSocket socket;
    
    public DatagramIOImpl(final DatagramIOConfigurationImpl configuration) {
        this.configuration = configuration;
    }
    
    @Override
    public DatagramIOConfigurationImpl getConfiguration() {
        return this.configuration;
    }
    
    @Override
    public synchronized void init(final InetAddress bindAddress, final Router router, final DatagramProcessor datagramProcessor) throws InitializationException {
        this.router = router;
        this.datagramProcessor = datagramProcessor;
        try {
            DatagramIOImpl.log.info("Creating bound socket (for datagram input/output) on: " + bindAddress);
            this.localAddress = new InetSocketAddress(bindAddress, 0);
            (this.socket = new MulticastSocket(this.localAddress)).setTimeToLive(this.configuration.getTimeToLive());
            this.socket.setReceiveBufferSize(262144);
        }
        catch (Exception ex) {
            throw new InitializationException("Could not initialize " + this.getClass().getSimpleName() + ": " + ex);
        }
    }
    
    @Override
    public synchronized void stop() {
        if (this.socket != null && !this.socket.isClosed()) {
            this.socket.close();
        }
    }
    
    @Override
    public void run() {
        DatagramIOImpl.log.fine("Entering blocking receiving loop, listening for UDP datagrams on: " + this.socket.getLocalAddress());
        try {
            while (true) {
                final byte[] buf = new byte[this.getConfiguration().getMaxDatagramBytes()];
                final DatagramPacket datagram = new DatagramPacket(buf, buf.length);
                this.socket.receive(datagram);
                DatagramIOImpl.log.fine("UDP datagram received from: " + datagram.getAddress().getHostAddress() + ":" + datagram.getPort() + " on: " + this.localAddress);
                this.router.received(this.datagramProcessor.read(this.localAddress.getAddress(), datagram));
            }
        }
        catch (SocketException ex2) {
            DatagramIOImpl.log.fine("Socket closed");
            try {
                if (!this.socket.isClosed()) {
                    DatagramIOImpl.log.fine("Closing unicast socket");
                    this.socket.close();
                }
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        catch (UnsupportedDataException ex3) {}
        catch (Exception ex4) {}
    }
    
    @Override
    public synchronized void send(final OutgoingDatagramMessage message) {
        if (DatagramIOImpl.log.isLoggable(Level.FINE)) {
            DatagramIOImpl.log.fine("Sending message from address: " + this.localAddress);
        }
        final DatagramPacket packet = this.datagramProcessor.write(message);
        if (DatagramIOImpl.log.isLoggable(Level.FINE)) {
            DatagramIOImpl.log.fine("Sending UDP datagram packet to: " + message.getDestinationAddress() + ":" + message.getDestinationPort());
        }
        this.send(packet);
    }
    
    @Override
    public synchronized void send(final DatagramPacket datagram) {
        if (DatagramIOImpl.log.isLoggable(Level.FINE)) {
            DatagramIOImpl.log.fine("Sending message from address: " + this.localAddress);
        }
        try {
            this.socket.send(datagram);
        }
        catch (SocketException ex3) {
            DatagramIOImpl.log.fine("Socket closed, aborting datagram send to: " + datagram.getAddress());
        }
        catch (RuntimeException ex) {
            throw ex;
        }
        catch (Exception ex2) {
            DatagramIOImpl.log.log(Level.SEVERE, "Exception sending datagram to: " + datagram.getAddress() + ": " + ex2, ex2);
        }
    }
    
    static {
        DatagramIOImpl.log = Logger.getLogger(DatagramIO.class.getName());
    }
}
