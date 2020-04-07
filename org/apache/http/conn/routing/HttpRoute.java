// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.conn.routing;

import org.apache.http.util.LangUtils;
import java.net.InetAddress;
import org.apache.http.HttpHost;
import org.apache.http.annotation.Immutable;

@Immutable
public final class HttpRoute implements RouteInfo, Cloneable
{
    private static final HttpHost[] EMPTY_HTTP_HOST_ARRAY;
    private final HttpHost targetHost;
    private final InetAddress localAddress;
    private final HttpHost[] proxyChain;
    private final TunnelType tunnelled;
    private final LayerType layered;
    private final boolean secure;
    
    private HttpRoute(final InetAddress local, final HttpHost target, final HttpHost[] proxies, final boolean secure, TunnelType tunnelled, LayerType layered) {
        if (target == null) {
            throw new IllegalArgumentException("Target host may not be null.");
        }
        if (proxies == null) {
            throw new IllegalArgumentException("Proxies may not be null.");
        }
        if (tunnelled == TunnelType.TUNNELLED && proxies.length == 0) {
            throw new IllegalArgumentException("Proxy required if tunnelled.");
        }
        if (tunnelled == null) {
            tunnelled = TunnelType.PLAIN;
        }
        if (layered == null) {
            layered = LayerType.PLAIN;
        }
        this.targetHost = target;
        this.localAddress = local;
        this.proxyChain = proxies;
        this.secure = secure;
        this.tunnelled = tunnelled;
        this.layered = layered;
    }
    
    public HttpRoute(final HttpHost target, final InetAddress local, final HttpHost[] proxies, final boolean secure, final TunnelType tunnelled, final LayerType layered) {
        this(local, target, toChain(proxies), secure, tunnelled, layered);
    }
    
    public HttpRoute(final HttpHost target, final InetAddress local, final HttpHost proxy, final boolean secure, final TunnelType tunnelled, final LayerType layered) {
        this(local, target, toChain(proxy), secure, tunnelled, layered);
    }
    
    public HttpRoute(final HttpHost target, final InetAddress local, final boolean secure) {
        this(local, target, HttpRoute.EMPTY_HTTP_HOST_ARRAY, secure, TunnelType.PLAIN, LayerType.PLAIN);
    }
    
    public HttpRoute(final HttpHost target) {
        this(null, target, HttpRoute.EMPTY_HTTP_HOST_ARRAY, false, TunnelType.PLAIN, LayerType.PLAIN);
    }
    
    public HttpRoute(final HttpHost target, final InetAddress local, final HttpHost proxy, final boolean secure) {
        this(local, target, toChain(proxy), secure, secure ? TunnelType.TUNNELLED : TunnelType.PLAIN, secure ? LayerType.LAYERED : LayerType.PLAIN);
        if (proxy == null) {
            throw new IllegalArgumentException("Proxy host may not be null.");
        }
    }
    
    private static HttpHost[] toChain(final HttpHost proxy) {
        if (proxy == null) {
            return HttpRoute.EMPTY_HTTP_HOST_ARRAY;
        }
        return new HttpHost[] { proxy };
    }
    
    private static HttpHost[] toChain(final HttpHost[] proxies) {
        if (proxies == null || proxies.length < 1) {
            return HttpRoute.EMPTY_HTTP_HOST_ARRAY;
        }
        for (final HttpHost proxy : proxies) {
            if (proxy == null) {
                throw new IllegalArgumentException("Proxy chain may not contain null elements.");
            }
        }
        final HttpHost[] result = new HttpHost[proxies.length];
        System.arraycopy(proxies, 0, result, 0, proxies.length);
        return result;
    }
    
    public final HttpHost getTargetHost() {
        return this.targetHost;
    }
    
    public final InetAddress getLocalAddress() {
        return this.localAddress;
    }
    
    public final int getHopCount() {
        return this.proxyChain.length + 1;
    }
    
    public final HttpHost getHopTarget(final int hop) {
        if (hop < 0) {
            throw new IllegalArgumentException("Hop index must not be negative: " + hop);
        }
        final int hopcount = this.getHopCount();
        if (hop >= hopcount) {
            throw new IllegalArgumentException("Hop index " + hop + " exceeds route length " + hopcount);
        }
        HttpHost result = null;
        if (hop < hopcount - 1) {
            result = this.proxyChain[hop];
        }
        else {
            result = this.targetHost;
        }
        return result;
    }
    
    public final HttpHost getProxyHost() {
        return (this.proxyChain.length == 0) ? null : this.proxyChain[0];
    }
    
    public final TunnelType getTunnelType() {
        return this.tunnelled;
    }
    
    public final boolean isTunnelled() {
        return this.tunnelled == TunnelType.TUNNELLED;
    }
    
    public final LayerType getLayerType() {
        return this.layered;
    }
    
    public final boolean isLayered() {
        return this.layered == LayerType.LAYERED;
    }
    
    public final boolean isSecure() {
        return this.secure;
    }
    
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof HttpRoute) {
            final HttpRoute that = (HttpRoute)obj;
            return this.secure == that.secure && this.tunnelled == that.tunnelled && this.layered == that.layered && LangUtils.equals(this.targetHost, that.targetHost) && LangUtils.equals(this.localAddress, that.localAddress) && LangUtils.equals(this.proxyChain, that.proxyChain);
        }
        return false;
    }
    
    public final int hashCode() {
        int hash = 17;
        hash = LangUtils.hashCode(hash, this.targetHost);
        hash = LangUtils.hashCode(hash, this.localAddress);
        for (int i = 0; i < this.proxyChain.length; ++i) {
            hash = LangUtils.hashCode(hash, this.proxyChain[i]);
        }
        hash = LangUtils.hashCode(hash, this.secure);
        hash = LangUtils.hashCode(hash, this.tunnelled);
        hash = LangUtils.hashCode(hash, this.layered);
        return hash;
    }
    
    public final String toString() {
        final StringBuilder cab = new StringBuilder(50 + this.getHopCount() * 30);
        if (this.localAddress != null) {
            cab.append(this.localAddress);
            cab.append("->");
        }
        cab.append('{');
        if (this.tunnelled == TunnelType.TUNNELLED) {
            cab.append('t');
        }
        if (this.layered == LayerType.LAYERED) {
            cab.append('l');
        }
        if (this.secure) {
            cab.append('s');
        }
        cab.append("}->");
        for (final HttpHost aProxyChain : this.proxyChain) {
            cab.append(aProxyChain);
            cab.append("->");
        }
        cab.append(this.targetHost);
        return cab.toString();
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    static {
        EMPTY_HTTP_HOST_ARRAY = new HttpHost[0];
    }
}
