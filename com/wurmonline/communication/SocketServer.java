// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.communication;

import java.util.HashMap;
import java.util.Iterator;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.List;
import java.nio.channels.ServerSocketChannel;

public class SocketServer
{
    private final ServerSocketChannel ssc;
    private final ServerListener serverListener;
    private final List<SocketConnection> connections;
    public static final ReentrantReadWriteLock CONNECTIONS_RW_LOCK;
    private final int acceptedPort;
    public boolean intraServer;
    private static final Logger logger;
    private static Map<String, Long> connectedIps;
    public static long MIN_MILLIS_BETWEEN_CONNECTIONS;
    
    public SocketServer(final byte[] ips, final int port, final int acceptedPort, final ServerListener serverListener) throws IOException {
        this.connections = new LinkedList<SocketConnection>();
        this.intraServer = false;
        this.serverListener = serverListener;
        this.acceptedPort = acceptedPort;
        final InetAddress hostip = InetAddress.getByAddress(ips);
        SocketServer.logger.info("Creating Wurm SocketServer on " + hostip + ':' + port);
        this.ssc = ServerSocketChannel.open();
        this.ssc.socket().bind(new InetSocketAddress(hostip, port));
        this.ssc.configureBlocking(false);
    }
    
    public void tick() throws IOException {
        SocketChannel socketChannel;
        while ((socketChannel = this.ssc.accept()) != null) {
            try {
                if (socketChannel.socket().getPort() != this.acceptedPort) {
                    if (!this.intraServer) {
                        SocketServer.logger.log(Level.INFO, "Accepted player connection: " + socketChannel.socket());
                    }
                }
                else if (!this.intraServer) {
                    SocketServer.logger.log(Level.INFO, socketChannel.socket().getRemoteSocketAddress() + " connected from the correct port");
                }
                boolean keepGoing = true;
                if (!this.intraServer && SocketServer.MIN_MILLIS_BETWEEN_CONNECTIONS > 0L) {
                    final String remoteIp = socketChannel.socket().getRemoteSocketAddress().toString().substring(0, socketChannel.socket().getRemoteSocketAddress().toString().indexOf(":"));
                    final Long lastConnTime = SocketServer.connectedIps.get(remoteIp);
                    if (lastConnTime != null) {
                        final long lct = lastConnTime;
                        if (System.currentTimeMillis() - lct < SocketServer.MIN_MILLIS_BETWEEN_CONNECTIONS) {
                            SocketServer.logger.log(Level.INFO, "Disconnecting " + remoteIp + " due to too many connections.");
                            if (socketChannel != null && socketChannel.socket() != null) {
                                try {
                                    socketChannel.socket().close();
                                }
                                catch (IOException iox) {
                                    iox.printStackTrace();
                                }
                            }
                            if (socketChannel != null) {
                                try {
                                    socketChannel.close();
                                }
                                catch (IOException iox) {
                                    iox.printStackTrace();
                                }
                            }
                            keepGoing = false;
                        }
                    }
                    else {
                        SocketServer.connectedIps.put(remoteIp, new Long(System.currentTimeMillis()));
                    }
                }
                if (!keepGoing) {
                    continue;
                }
                socketChannel.configureBlocking(false);
                final SocketConnection socketConnection = new SocketConnection(socketChannel, true, this.intraServer);
                SocketServer.CONNECTIONS_RW_LOCK.writeLock().lock();
                try {
                    this.connections.add(socketConnection);
                }
                finally {
                    SocketServer.CONNECTIONS_RW_LOCK.writeLock().unlock();
                }
                this.serverListener.clientConnected(socketConnection);
                continue;
            }
            catch (IOException e) {
                try {
                    socketChannel.close();
                }
                catch (Exception ex) {}
                throw e;
            }
            break;
        }
        SocketServer.CONNECTIONS_RW_LOCK.writeLock().lock();
        try {
            final Iterator<SocketConnection> it = this.connections.iterator();
            while (it.hasNext()) {
                final SocketConnection socketConnection = it.next();
                if (!socketConnection.isConnected()) {
                    socketConnection.disconnect();
                    this.serverListener.clientException(socketConnection, new Exception());
                    it.remove();
                }
                else {
                    try {
                        socketConnection.tick();
                    }
                    catch (Exception e2) {
                        socketConnection.disconnect();
                        this.serverListener.clientException(socketConnection, e2);
                        it.remove();
                    }
                }
            }
        }
        finally {
            SocketServer.CONNECTIONS_RW_LOCK.writeLock().unlock();
        }
    }
    
    public int getNumberOfConnections() {
        SocketServer.CONNECTIONS_RW_LOCK.readLock().lock();
        try {
            if (this.connections != null) {
                return this.connections.size();
            }
            return 0;
        }
        finally {
            SocketServer.CONNECTIONS_RW_LOCK.readLock().unlock();
        }
    }
    
    static {
        CONNECTIONS_RW_LOCK = new ReentrantReadWriteLock();
        logger = Logger.getLogger(SocketServer.class.getName());
        SocketServer.connectedIps = new HashMap<String, Long>();
        SocketServer.MIN_MILLIS_BETWEEN_CONNECTIONS = 1000L;
    }
}
