// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.impl.conn;

import java.net.Socket;
import java.io.IOException;
import org.apache.http.HttpHost;
import java.io.InterruptedIOException;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.conn.routing.RouteTracker;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.ClientConnectionOperator;

@Deprecated
public abstract class AbstractPoolEntry
{
    protected final ClientConnectionOperator connOperator;
    protected final OperatedClientConnection connection;
    protected volatile HttpRoute route;
    protected volatile Object state;
    protected volatile RouteTracker tracker;
    
    protected AbstractPoolEntry(final ClientConnectionOperator connOperator, final HttpRoute route) {
        if (connOperator == null) {
            throw new IllegalArgumentException("Connection operator may not be null");
        }
        this.connOperator = connOperator;
        this.connection = connOperator.createConnection();
        this.route = route;
        this.tracker = null;
    }
    
    public Object getState() {
        return this.state;
    }
    
    public void setState(final Object state) {
        this.state = state;
    }
    
    public void open(final HttpRoute route, final HttpContext context, final HttpParams params) throws IOException {
        if (route == null) {
            throw new IllegalArgumentException("Route must not be null.");
        }
        if (params == null) {
            throw new IllegalArgumentException("Parameters must not be null.");
        }
        if (this.tracker != null && this.tracker.isConnected()) {
            throw new IllegalStateException("Connection already open.");
        }
        this.tracker = new RouteTracker(route);
        final HttpHost proxy = route.getProxyHost();
        this.connOperator.openConnection(this.connection, (proxy != null) ? proxy : route.getTargetHost(), route.getLocalAddress(), context, params);
        final RouteTracker localTracker = this.tracker;
        if (localTracker == null) {
            throw new InterruptedIOException("Request aborted");
        }
        if (proxy == null) {
            localTracker.connectTarget(this.connection.isSecure());
        }
        else {
            localTracker.connectProxy(proxy, this.connection.isSecure());
        }
    }
    
    public void tunnelTarget(final boolean secure, final HttpParams params) throws IOException {
        if (params == null) {
            throw new IllegalArgumentException("Parameters must not be null.");
        }
        if (this.tracker == null || !this.tracker.isConnected()) {
            throw new IllegalStateException("Connection not open.");
        }
        if (this.tracker.isTunnelled()) {
            throw new IllegalStateException("Connection is already tunnelled.");
        }
        this.connection.update(null, this.tracker.getTargetHost(), secure, params);
        this.tracker.tunnelTarget(secure);
    }
    
    public void tunnelProxy(final HttpHost next, final boolean secure, final HttpParams params) throws IOException {
        if (next == null) {
            throw new IllegalArgumentException("Next proxy must not be null.");
        }
        if (params == null) {
            throw new IllegalArgumentException("Parameters must not be null.");
        }
        if (this.tracker == null || !this.tracker.isConnected()) {
            throw new IllegalStateException("Connection not open.");
        }
        this.connection.update(null, next, secure, params);
        this.tracker.tunnelProxy(next, secure);
    }
    
    public void layerProtocol(final HttpContext context, final HttpParams params) throws IOException {
        if (params == null) {
            throw new IllegalArgumentException("Parameters must not be null.");
        }
        if (this.tracker == null || !this.tracker.isConnected()) {
            throw new IllegalStateException("Connection not open.");
        }
        if (!this.tracker.isTunnelled()) {
            throw new IllegalStateException("Protocol layering without a tunnel not supported.");
        }
        if (this.tracker.isLayered()) {
            throw new IllegalStateException("Multiple protocol layering not supported.");
        }
        final HttpHost target = this.tracker.getTargetHost();
        this.connOperator.updateSecureConnection(this.connection, target, context, params);
        this.tracker.layerProtocol(this.connection.isSecure());
    }
    
    protected void shutdownEntry() {
        this.tracker = null;
        this.state = null;
    }
}
