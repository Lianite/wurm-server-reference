// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.impl;

import java.io.IOException;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import java.net.Socket;
import org.apache.http.annotation.NotThreadSafe;

@NotThreadSafe
public class DefaultHttpServerConnection extends SocketHttpServerConnection
{
    public void bind(final Socket socket, final HttpParams params) throws IOException {
        if (socket == null) {
            throw new IllegalArgumentException("Socket may not be null");
        }
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        this.assertNotOpen();
        socket.setTcpNoDelay(HttpConnectionParams.getTcpNoDelay(params));
        socket.setSoTimeout(HttpConnectionParams.getSoTimeout(params));
        socket.setKeepAlive(HttpConnectionParams.getSoKeepalive(params));
        final int linger = HttpConnectionParams.getLinger(params);
        if (linger >= 0) {
            socket.setSoLinger(linger > 0, linger);
        }
        super.bind(socket, params);
    }
}
