// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.impl.client;

import org.apache.http.auth.AuthScheme;
import org.apache.http.HttpHost;
import java.util.HashMap;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.client.AuthCache;

@NotThreadSafe
public class BasicAuthCache implements AuthCache
{
    private final HashMap<HttpHost, AuthScheme> map;
    
    public BasicAuthCache() {
        this.map = new HashMap<HttpHost, AuthScheme>();
    }
    
    protected HttpHost getKey(final HttpHost host) {
        if (host.getPort() <= 0) {
            final int port = host.getSchemeName().equalsIgnoreCase("https") ? 443 : 80;
            return new HttpHost(host.getHostName(), port, host.getSchemeName());
        }
        return host;
    }
    
    public void put(final HttpHost host, final AuthScheme authScheme) {
        if (host == null) {
            throw new IllegalArgumentException("HTTP host may not be null");
        }
        this.map.put(this.getKey(host), authScheme);
    }
    
    public AuthScheme get(final HttpHost host) {
        if (host == null) {
            throw new IllegalArgumentException("HTTP host may not be null");
        }
        return this.map.get(this.getKey(host));
    }
    
    public void remove(final HttpHost host) {
        if (host == null) {
            throw new IllegalArgumentException("HTTP host may not be null");
        }
        this.map.remove(this.getKey(host));
    }
    
    public void clear() {
        this.map.clear();
    }
    
    public String toString() {
        return this.map.toString();
    }
}
