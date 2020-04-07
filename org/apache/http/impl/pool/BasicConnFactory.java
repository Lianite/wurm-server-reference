// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.impl.pool;

import java.net.SocketAddress;
import java.net.InetSocketAddress;
import org.apache.http.params.HttpConnectionParams;
import java.io.IOException;
import org.apache.http.impl.DefaultHttpClientConnection;
import java.net.Socket;
import org.apache.http.params.HttpParams;
import javax.net.ssl.SSLSocketFactory;
import org.apache.http.annotation.Immutable;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpHost;
import org.apache.http.pool.ConnFactory;

@Immutable
public class BasicConnFactory implements ConnFactory<HttpHost, HttpClientConnection>
{
    private final SSLSocketFactory sslfactory;
    private final HttpParams params;
    
    public BasicConnFactory(final SSLSocketFactory sslfactory, final HttpParams params) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP params may not be null");
        }
        this.sslfactory = sslfactory;
        this.params = params;
    }
    
    public BasicConnFactory(final HttpParams params) {
        this(null, params);
    }
    
    protected HttpClientConnection create(final Socket socket, final HttpParams params) throws IOException {
        final DefaultHttpClientConnection conn = new DefaultHttpClientConnection();
        conn.bind(socket, params);
        return conn;
    }
    
    public HttpClientConnection create(final HttpHost host) throws IOException {
        final String scheme = host.getSchemeName();
        Socket socket = null;
        if ("http".equalsIgnoreCase(scheme)) {
            socket = new Socket();
        }
        if ("https".equalsIgnoreCase(scheme) && this.sslfactory != null) {
            socket = this.sslfactory.createSocket();
        }
        if (socket == null) {
            throw new IOException(scheme + " scheme is not supported");
        }
        final int connectTimeout = HttpConnectionParams.getConnectionTimeout(this.params);
        final int soTimeout = HttpConnectionParams.getSoTimeout(this.params);
        socket.setSoTimeout(soTimeout);
        socket.connect(new InetSocketAddress(host.getHostName(), host.getPort()), connectTimeout);
        return this.create(socket, this.params);
    }
}
