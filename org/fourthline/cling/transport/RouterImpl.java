// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport;

import java.util.concurrent.TimeUnit;
import java.net.BindException;
import java.net.DatagramPacket;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.OutgoingDatagramMessage;
import org.fourthline.cling.transport.spi.UpnpStream;
import org.fourthline.cling.protocol.ReceivingAsync;
import org.fourthline.cling.protocol.ProtocolCreationException;
import java.util.logging.Level;
import org.fourthline.cling.model.message.IncomingDatagramMessage;
import java.util.ArrayList;
import java.util.Collections;
import org.fourthline.cling.model.NetworkAddress;
import java.util.List;
import org.seamless.util.Exceptions;
import java.util.Iterator;
import org.fourthline.cling.transport.spi.InitializationException;
import org.fourthline.cling.transport.spi.NoNetworkException;
import javax.enterprise.inject.Default;
import javax.enterprise.event.Observes;
import java.util.HashMap;
import org.fourthline.cling.transport.spi.StreamServer;
import org.fourthline.cling.transport.spi.DatagramIO;
import java.net.InetAddress;
import org.fourthline.cling.transport.spi.MulticastReceiver;
import java.net.NetworkInterface;
import java.util.Map;
import org.fourthline.cling.transport.spi.StreamClient;
import org.fourthline.cling.transport.spi.NetworkAddressFactory;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.UpnpServiceConfiguration;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RouterImpl implements Router
{
    private static Logger log;
    protected UpnpServiceConfiguration configuration;
    protected ProtocolFactory protocolFactory;
    protected volatile boolean enabled;
    protected ReentrantReadWriteLock routerLock;
    protected Lock readLock;
    protected Lock writeLock;
    protected NetworkAddressFactory networkAddressFactory;
    protected StreamClient streamClient;
    protected final Map<NetworkInterface, MulticastReceiver> multicastReceivers;
    protected final Map<InetAddress, DatagramIO> datagramIOs;
    protected final Map<InetAddress, StreamServer> streamServers;
    
    protected RouterImpl() {
        this.routerLock = new ReentrantReadWriteLock(true);
        this.readLock = this.routerLock.readLock();
        this.writeLock = this.routerLock.writeLock();
        this.multicastReceivers = new HashMap<NetworkInterface, MulticastReceiver>();
        this.datagramIOs = new HashMap<InetAddress, DatagramIO>();
        this.streamServers = new HashMap<InetAddress, StreamServer>();
    }
    
    public RouterImpl(final UpnpServiceConfiguration configuration, final ProtocolFactory protocolFactory) {
        this.routerLock = new ReentrantReadWriteLock(true);
        this.readLock = this.routerLock.readLock();
        this.writeLock = this.routerLock.writeLock();
        this.multicastReceivers = new HashMap<NetworkInterface, MulticastReceiver>();
        this.datagramIOs = new HashMap<InetAddress, DatagramIO>();
        this.streamServers = new HashMap<InetAddress, StreamServer>();
        RouterImpl.log.info("Creating Router: " + this.getClass().getName());
        this.configuration = configuration;
        this.protocolFactory = protocolFactory;
    }
    
    public boolean enable(@Observes @Default final EnableRouter event) throws RouterException {
        return this.enable();
    }
    
    public boolean disable(@Observes @Default final DisableRouter event) throws RouterException {
        return this.disable();
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
        this.lock(this.writeLock);
        try {
            if (!this.enabled) {
                try {
                    RouterImpl.log.fine("Starting networking services...");
                    this.networkAddressFactory = this.getConfiguration().createNetworkAddressFactory();
                    this.startInterfaceBasedTransports(this.networkAddressFactory.getNetworkInterfaces());
                    this.startAddressBasedTransports(this.networkAddressFactory.getBindAddresses());
                    if (!this.networkAddressFactory.hasUsableNetwork()) {
                        throw new NoNetworkException("No usable network interface and/or addresses available, check the log for errors.");
                    }
                    this.streamClient = this.getConfiguration().createStreamClient();
                    return this.enabled = true;
                }
                catch (InitializationException ex) {
                    this.handleStartFailure(ex);
                }
            }
            return false;
        }
        finally {
            this.unlock(this.writeLock);
        }
    }
    
    @Override
    public boolean disable() throws RouterException {
        this.lock(this.writeLock);
        try {
            if (this.enabled) {
                RouterImpl.log.fine("Disabling network services...");
                if (this.streamClient != null) {
                    RouterImpl.log.fine("Stopping stream client connection management/pool");
                    this.streamClient.stop();
                    this.streamClient = null;
                }
                for (final Map.Entry<InetAddress, StreamServer> entry : this.streamServers.entrySet()) {
                    RouterImpl.log.fine("Stopping stream server on address: " + entry.getKey());
                    entry.getValue().stop();
                }
                this.streamServers.clear();
                for (final Map.Entry<NetworkInterface, MulticastReceiver> entry2 : this.multicastReceivers.entrySet()) {
                    RouterImpl.log.fine("Stopping multicast receiver on interface: " + entry2.getKey().getDisplayName());
                    entry2.getValue().stop();
                }
                this.multicastReceivers.clear();
                for (final Map.Entry<InetAddress, DatagramIO> entry3 : this.datagramIOs.entrySet()) {
                    RouterImpl.log.fine("Stopping datagram I/O on address: " + entry3.getKey());
                    entry3.getValue().stop();
                }
                this.datagramIOs.clear();
                this.networkAddressFactory = null;
                this.enabled = false;
                return true;
            }
            return false;
        }
        finally {
            this.unlock(this.writeLock);
        }
    }
    
    @Override
    public void shutdown() throws RouterException {
        this.disable();
    }
    
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
    
    @Override
    public void handleStartFailure(final InitializationException ex) throws InitializationException {
        if (ex instanceof NoNetworkException) {
            RouterImpl.log.info("Unable to initialize network router, no network found.");
        }
        else {
            RouterImpl.log.severe("Unable to initialize network router: " + ex);
            RouterImpl.log.severe("Cause: " + Exceptions.unwrap(ex));
        }
    }
    
    @Override
    public List<NetworkAddress> getActiveStreamServers(final InetAddress preferredAddress) throws RouterException {
        this.lock(this.readLock);
        try {
            if (!this.enabled || this.streamServers.size() <= 0) {
                return (List<NetworkAddress>)Collections.EMPTY_LIST;
            }
            final List<NetworkAddress> streamServerAddresses = new ArrayList<NetworkAddress>();
            final StreamServer preferredServer;
            if (preferredAddress != null && (preferredServer = this.streamServers.get(preferredAddress)) != null) {
                streamServerAddresses.add(new NetworkAddress(preferredAddress, preferredServer.getPort(), this.networkAddressFactory.getHardwareAddress(preferredAddress)));
                return streamServerAddresses;
            }
            for (final Map.Entry<InetAddress, StreamServer> entry : this.streamServers.entrySet()) {
                final byte[] hardwareAddress = this.networkAddressFactory.getHardwareAddress(entry.getKey());
                streamServerAddresses.add(new NetworkAddress(entry.getKey(), entry.getValue().getPort(), hardwareAddress));
            }
            return streamServerAddresses;
        }
        finally {
            this.unlock(this.readLock);
        }
    }
    
    @Override
    public void received(final IncomingDatagramMessage msg) {
        if (!this.enabled) {
            RouterImpl.log.fine("Router disabled, ignoring incoming message: " + msg);
            return;
        }
        try {
            final ReceivingAsync protocol = this.getProtocolFactory().createReceivingAsync(msg);
            if (protocol == null) {
                if (RouterImpl.log.isLoggable(Level.FINEST)) {
                    RouterImpl.log.finest("No protocol, ignoring received message: " + msg);
                }
                return;
            }
            if (RouterImpl.log.isLoggable(Level.FINE)) {
                RouterImpl.log.fine("Received asynchronous message: " + msg);
            }
            this.getConfiguration().getAsyncProtocolExecutor().execute(protocol);
        }
        catch (ProtocolCreationException ex) {
            RouterImpl.log.warning("Handling received datagram failed - " + Exceptions.unwrap(ex).toString());
        }
    }
    
    @Override
    public void received(final UpnpStream stream) {
        if (!this.enabled) {
            RouterImpl.log.fine("Router disabled, ignoring incoming: " + stream);
            return;
        }
        RouterImpl.log.fine("Received synchronous stream: " + stream);
        this.getConfiguration().getSyncProtocolExecutorService().execute(stream);
    }
    
    @Override
    public void send(final OutgoingDatagramMessage msg) throws RouterException {
        this.lock(this.readLock);
        try {
            if (this.enabled) {
                for (final DatagramIO datagramIO : this.datagramIOs.values()) {
                    datagramIO.send(msg);
                }
            }
            else {
                RouterImpl.log.fine("Router disabled, not sending datagram: " + msg);
            }
        }
        finally {
            this.unlock(this.readLock);
        }
    }
    
    @Override
    public StreamResponseMessage send(final StreamRequestMessage msg) throws RouterException {
        this.lock(this.readLock);
        try {
            if (this.enabled) {
                if (this.streamClient == null) {
                    RouterImpl.log.fine("No StreamClient available, not sending: " + msg);
                    return null;
                }
                RouterImpl.log.fine("Sending via TCP unicast stream: " + msg);
                try {
                    return this.streamClient.sendRequest(msg);
                }
                catch (InterruptedException ex) {
                    throw new RouterException("Sending stream request was interrupted", ex);
                }
            }
            RouterImpl.log.fine("Router disabled, not sending stream request: " + msg);
            return null;
        }
        finally {
            this.unlock(this.readLock);
        }
    }
    
    @Override
    public void broadcast(final byte[] bytes) throws RouterException {
        this.lock(this.readLock);
        try {
            if (this.enabled) {
                for (final Map.Entry<InetAddress, DatagramIO> entry : this.datagramIOs.entrySet()) {
                    final InetAddress broadcast = this.networkAddressFactory.getBroadcastAddress(entry.getKey());
                    if (broadcast != null) {
                        RouterImpl.log.fine("Sending UDP datagram to broadcast address: " + broadcast.getHostAddress());
                        final DatagramPacket packet = new DatagramPacket(bytes, bytes.length, broadcast, 9);
                        entry.getValue().send(packet);
                    }
                }
            }
            else {
                RouterImpl.log.fine("Router disabled, not broadcasting bytes: " + bytes.length);
            }
        }
        finally {
            this.unlock(this.readLock);
        }
    }
    
    protected void startInterfaceBasedTransports(final Iterator<NetworkInterface> interfaces) throws InitializationException {
        while (interfaces.hasNext()) {
            final NetworkInterface networkInterface = interfaces.next();
            final MulticastReceiver multicastReceiver = this.getConfiguration().createMulticastReceiver(this.networkAddressFactory);
            if (multicastReceiver == null) {
                RouterImpl.log.info("Configuration did not create a MulticastReceiver for: " + networkInterface);
            }
            else {
                try {
                    if (RouterImpl.log.isLoggable(Level.FINE)) {
                        RouterImpl.log.fine("Init multicast receiver on interface: " + networkInterface.getDisplayName());
                    }
                    multicastReceiver.init(networkInterface, this, this.networkAddressFactory, this.getConfiguration().getDatagramProcessor());
                    this.multicastReceivers.put(networkInterface, multicastReceiver);
                }
                catch (InitializationException ex) {
                    throw ex;
                }
            }
        }
        for (final Map.Entry<NetworkInterface, MulticastReceiver> entry : this.multicastReceivers.entrySet()) {
            if (RouterImpl.log.isLoggable(Level.FINE)) {
                RouterImpl.log.fine("Starting multicast receiver on interface: " + entry.getKey().getDisplayName());
            }
            this.getConfiguration().getMulticastReceiverExecutor().execute(entry.getValue());
        }
    }
    
    protected void startAddressBasedTransports(final Iterator<InetAddress> addresses) throws InitializationException {
        while (addresses.hasNext()) {
            final InetAddress address = addresses.next();
            final StreamServer streamServer = this.getConfiguration().createStreamServer(this.networkAddressFactory);
            if (streamServer == null) {
                RouterImpl.log.info("Configuration did not create a StreamServer for: " + address);
            }
            else {
                try {
                    if (RouterImpl.log.isLoggable(Level.FINE)) {
                        RouterImpl.log.fine("Init stream server on address: " + address);
                    }
                    streamServer.init(address, this);
                    this.streamServers.put(address, streamServer);
                }
                catch (InitializationException ex) {
                    final Throwable cause = Exceptions.unwrap(ex);
                    if (cause instanceof BindException) {
                        RouterImpl.log.warning("Failed to init StreamServer: " + cause);
                        if (RouterImpl.log.isLoggable(Level.FINE)) {
                            RouterImpl.log.log(Level.FINE, "Initialization exception root cause", cause);
                        }
                        RouterImpl.log.warning("Removing unusable address: " + address);
                        addresses.remove();
                        continue;
                    }
                    throw ex;
                }
            }
            final DatagramIO datagramIO = this.getConfiguration().createDatagramIO(this.networkAddressFactory);
            if (datagramIO == null) {
                RouterImpl.log.info("Configuration did not create a StreamServer for: " + address);
            }
            else {
                try {
                    if (RouterImpl.log.isLoggable(Level.FINE)) {
                        RouterImpl.log.fine("Init datagram I/O on address: " + address);
                    }
                    datagramIO.init(address, this, this.getConfiguration().getDatagramProcessor());
                    this.datagramIOs.put(address, datagramIO);
                }
                catch (InitializationException ex2) {
                    throw ex2;
                }
            }
        }
        for (final Map.Entry<InetAddress, StreamServer> entry : this.streamServers.entrySet()) {
            if (RouterImpl.log.isLoggable(Level.FINE)) {
                RouterImpl.log.fine("Starting stream server on address: " + entry.getKey());
            }
            this.getConfiguration().getStreamServerExecutorService().execute(entry.getValue());
        }
        for (final Map.Entry<InetAddress, DatagramIO> entry2 : this.datagramIOs.entrySet()) {
            if (RouterImpl.log.isLoggable(Level.FINE)) {
                RouterImpl.log.fine("Starting datagram I/O on address: " + entry2.getKey());
            }
            this.getConfiguration().getDatagramIOExecutor().execute(entry2.getValue());
        }
    }
    
    protected void lock(final Lock lock, final int timeoutMilliseconds) throws RouterException {
        try {
            RouterImpl.log.finest("Trying to obtain lock with timeout milliseconds '" + timeoutMilliseconds + "': " + lock.getClass().getSimpleName());
            if (!lock.tryLock(timeoutMilliseconds, TimeUnit.MILLISECONDS)) {
                throw new RouterException("Router wasn't available exclusively after waiting " + timeoutMilliseconds + "ms, lock failed: " + lock.getClass().getSimpleName());
            }
            RouterImpl.log.finest("Acquired router lock: " + lock.getClass().getSimpleName());
        }
        catch (InterruptedException ex) {
            throw new RouterException("Interruption while waiting for exclusive access: " + lock.getClass().getSimpleName(), ex);
        }
    }
    
    protected void lock(final Lock lock) throws RouterException {
        this.lock(lock, this.getLockTimeoutMillis());
    }
    
    protected void unlock(final Lock lock) {
        RouterImpl.log.finest("Releasing router lock: " + lock.getClass().getSimpleName());
        lock.unlock();
    }
    
    protected int getLockTimeoutMillis() {
        return 6000;
    }
    
    static {
        RouterImpl.log = Logger.getLogger(Router.class.getName());
    }
}
