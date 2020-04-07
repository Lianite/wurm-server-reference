// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.conn;

import org.apache.http.HttpHost;
import org.apache.http.annotation.Immutable;
import java.net.ConnectException;

@Immutable
public class HttpHostConnectException extends ConnectException
{
    private static final long serialVersionUID = -3194482710275220224L;
    private final HttpHost host;
    
    public HttpHostConnectException(final HttpHost host, final ConnectException cause) {
        super("Connection to " + host + " refused");
        this.host = host;
        this.initCause(cause);
    }
    
    public HttpHost getHost() {
        return this.host;
    }
}
