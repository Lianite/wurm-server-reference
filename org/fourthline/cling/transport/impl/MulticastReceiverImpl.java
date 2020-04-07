// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.impl;

import org.fourthline.cling.transport.spi.MulticastReceiverConfiguration;
import java.net.InetAddress;
import org.fourthline.cling.model.UnsupportedDataException;
import java.net.SocketException;
import java.net.Inet6Address;
import java.net.DatagramPacket;
import org.fourthline.cling.transport.spi.InitializationException;
import java.net.SocketAddress;
import java.net.MulticastSocket;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import org.fourthline.cling.transport.spi.DatagramProcessor;
import org.fourthline.cling.transport.spi.NetworkAddressFactory;
import org.fourthline.cling.transport.Router;
import java.util.logging.Logger;
import org.fourthline.cling.transport.spi.MulticastReceiver;

public class MulticastReceiverImpl implements MulticastReceiver<MulticastReceiverConfigurationImpl>
{
    private static Logger log;
    protected final MulticastReceiverConfigurationImpl configuration;
    protected Router router;
    protected NetworkAddressFactory networkAddressFactory;
    protected DatagramProcessor datagramProcessor;
    protected NetworkInterface multicastInterface;
    protected InetSocketAddress multicastAddress;
    protected MulticastSocket socket;
    
    public MulticastReceiverImpl(final MulticastReceiverConfigurationImpl configuration) {
        this.configuration = configuration;
    }
    
    @Override
    public MulticastReceiverConfigurationImpl getConfiguration() {
        return this.configuration;
    }
    
    @Override
    public synchronized void init(final NetworkInterface networkInterface, final Router router, final NetworkAddressFactory networkAddressFactory, final DatagramProcessor datagramProcessor) throws InitializationException {
        this.router = router;
        this.networkAddressFactory = networkAddressFactory;
        this.datagramProcessor = datagramProcessor;
        this.multicastInterface = networkInterface;
        try {
            MulticastReceiverImpl.log.info("Creating wildcard socket (for receiving multicast datagrams) on port: " + this.configuration.getPort());
            this.multicastAddress = new InetSocketAddress(this.configuration.getGroup(), this.configuration.getPort());
            (this.socket = new MulticastSocket(this.configuration.getPort())).setReuseAddress(true);
            this.socket.setReceiveBufferSize(32768);
            MulticastReceiverImpl.log.info("Joining multicast group: " + this.multicastAddress + " on network interface: " + this.multicastInterface.getDisplayName());
            this.socket.joinGroup(this.multicastAddress, this.multicastInterface);
        }
        catch (Exception ex) {
            throw new InitializationException("Could not initialize " + this.getClass().getSimpleName() + ": " + ex);
        }
    }
    
    @Override
    public synchronized void stop() {
        if (this.socket != null && !this.socket.isClosed()) {
            try {
                MulticastReceiverImpl.log.fine("Leaving multicast group");
                this.socket.leaveGroup(this.multicastAddress, this.multicastInterface);
            }
            catch (Exception ex) {
                MulticastReceiverImpl.log.fine("Could not leave multicast group: " + ex);
            }
            this.socket.close();
        }
    }
    
    @Override
    public void run() {
        MulticastReceiverImpl.log.fine("Entering blocking receiving loop, listening for UDP datagrams on: " + this.socket.getLocalAddress());
        try {
            while (true) {
                final byte[] buf = new byte[this.getConfiguration().getMaxDatagramBytes()];
                final DatagramPacket datagram = new DatagramPacket(buf, buf.length);
                this.socket.receive(datagram);
                final InetAddress receivedOnLocalAddress = this.networkAddressFactory.getLocalAddress(this.multicastInterface, this.multicastAddress.getAddress() instanceof Inet6Address, datagram.getAddress());
                MulticastReceiverImpl.log.fine("UDP datagram received from: " + datagram.getAddress().getHostAddress() + ":" + datagram.getPort() + " on local interface: " + this.multicastInterface.getDisplayName() + " and address: " + receivedOnLocalAddress.getHostAddress());
                this.router.received(this.datagramProcessor.read(receivedOnLocalAddress, datagram));
            }
        }
        catch (SocketException ex2) {
            MulticastReceiverImpl.log.fine("Socket closed");
            try {
                if (!this.socket.isClosed()) {
                    MulticastReceiverImpl.log.fine("Closing multicast socket");
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
    
    static {
        MulticastReceiverImpl.log = Logger.getLogger(MulticastReceiver.class.getName());
    }
}
