// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.conn;

import java.util.Iterator;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import org.apache.http.params.HttpConnectionParams;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import org.apache.http.params.HttpParams;
import java.net.InetAddress;
import java.net.Socket;
import org.apache.http.annotation.Immutable;
import org.apache.http.conn.scheme.SocketFactory;

@Deprecated
@Immutable
public final class MultihomePlainSocketFactory implements SocketFactory
{
    private static final MultihomePlainSocketFactory DEFAULT_FACTORY;
    
    public static MultihomePlainSocketFactory getSocketFactory() {
        return MultihomePlainSocketFactory.DEFAULT_FACTORY;
    }
    
    public Socket createSocket() {
        return new Socket();
    }
    
    public Socket connectSocket(Socket sock, final String host, final int port, final InetAddress localAddress, int localPort, final HttpParams params) throws IOException {
        if (host == null) {
            throw new IllegalArgumentException("Target host may not be null.");
        }
        if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null.");
        }
        if (sock == null) {
            sock = this.createSocket();
        }
        if (localAddress != null || localPort > 0) {
            if (localPort < 0) {
                localPort = 0;
            }
            final InetSocketAddress isa = new InetSocketAddress(localAddress, localPort);
            sock.bind(isa);
        }
        final int timeout = HttpConnectionParams.getConnectionTimeout(params);
        final InetAddress[] inetadrs = InetAddress.getAllByName(host);
        final List<InetAddress> addresses = new ArrayList<InetAddress>(inetadrs.length);
        addresses.addAll(Arrays.asList(inetadrs));
        Collections.shuffle(addresses);
        IOException lastEx = null;
        for (final InetAddress remoteAddress : addresses) {
            try {
                sock.connect(new InetSocketAddress(remoteAddress, port), timeout);
            }
            catch (SocketTimeoutException ex2) {
                throw new ConnectTimeoutException("Connect to " + remoteAddress + " timed out");
            }
            catch (IOException ex) {
                sock = new Socket();
                lastEx = ex;
                continue;
            }
            break;
        }
        if (lastEx != null) {
            throw lastEx;
        }
        return sock;
    }
    
    public final boolean isSecure(final Socket sock) throws IllegalArgumentException {
        if (sock == null) {
            throw new IllegalArgumentException("Socket may not be null.");
        }
        if (sock.getClass() != Socket.class) {
            throw new IllegalArgumentException("Socket not created by this factory.");
        }
        if (sock.isClosed()) {
            throw new IllegalArgumentException("Socket is closed.");
        }
        return false;
    }
    
    static {
        DEFAULT_FACTORY = new MultihomePlainSocketFactory();
    }
}
