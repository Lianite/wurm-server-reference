// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.conn;

import java.net.InetAddress;
import org.apache.http.HttpHost;
import java.net.InetSocketAddress;

public class HttpInetSocketAddress extends InetSocketAddress
{
    private static final long serialVersionUID = -6650701828361907957L;
    private final HttpHost httphost;
    
    public HttpInetSocketAddress(final HttpHost httphost, final InetAddress addr, final int port) {
        super(addr, port);
        if (httphost == null) {
            throw new IllegalArgumentException("HTTP host may not be null");
        }
        this.httphost = httphost;
    }
    
    public HttpHost getHttpHost() {
        return this.httphost;
    }
    
    public String toString() {
        return this.httphost.getHostName() + ":" + this.getPort();
    }
}
