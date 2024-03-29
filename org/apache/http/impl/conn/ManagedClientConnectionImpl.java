// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.impl.conn;

import org.apache.http.pool.PoolEntry;
import java.util.concurrent.TimeUnit;
import org.apache.http.HttpHost;
import org.apache.http.conn.routing.RouteTracker;
import java.io.InterruptedIOException;
import org.apache.http.params.HttpParams;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.protocol.HttpContext;
import java.net.Socket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSession;
import java.net.InetAddress;
import org.apache.http.HttpRequest;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpConnectionMetrics;
import java.io.IOException;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.conn.ManagedClientConnection;

@NotThreadSafe
class ManagedClientConnectionImpl implements ManagedClientConnection
{
    private final ClientConnectionManager manager;
    private final ClientConnectionOperator operator;
    private volatile HttpPoolEntry poolEntry;
    private volatile boolean reusable;
    private volatile long duration;
    
    ManagedClientConnectionImpl(final ClientConnectionManager manager, final ClientConnectionOperator operator, final HttpPoolEntry entry) {
        if (manager == null) {
            throw new IllegalArgumentException("Connection manager may not be null");
        }
        if (operator == null) {
            throw new IllegalArgumentException("Connection operator may not be null");
        }
        if (entry == null) {
            throw new IllegalArgumentException("HTTP pool entry may not be null");
        }
        this.manager = manager;
        this.operator = operator;
        this.poolEntry = entry;
        this.reusable = false;
        this.duration = Long.MAX_VALUE;
    }
    
    HttpPoolEntry getPoolEntry() {
        return this.poolEntry;
    }
    
    HttpPoolEntry detach() {
        final HttpPoolEntry local = this.poolEntry;
        this.poolEntry = null;
        return local;
    }
    
    public ClientConnectionManager getManager() {
        return this.manager;
    }
    
    private OperatedClientConnection getConnection() {
        final HttpPoolEntry local = this.poolEntry;
        if (local == null) {
            return null;
        }
        return ((PoolEntry<T, OperatedClientConnection>)local).getConnection();
    }
    
    private OperatedClientConnection ensureConnection() {
        final HttpPoolEntry local = this.poolEntry;
        if (local == null) {
            throw new ConnectionShutdownException();
        }
        return ((PoolEntry<T, OperatedClientConnection>)local).getConnection();
    }
    
    private HttpPoolEntry ensurePoolEntry() {
        final HttpPoolEntry local = this.poolEntry;
        if (local == null) {
            throw new ConnectionShutdownException();
        }
        return local;
    }
    
    public void close() throws IOException {
        final HttpPoolEntry local = this.poolEntry;
        if (local != null) {
            final OperatedClientConnection conn = ((PoolEntry<T, OperatedClientConnection>)local).getConnection();
            local.getTracker().reset();
            conn.close();
        }
    }
    
    public void shutdown() throws IOException {
        final HttpPoolEntry local = this.poolEntry;
        if (local != null) {
            final OperatedClientConnection conn = ((PoolEntry<T, OperatedClientConnection>)local).getConnection();
            local.getTracker().reset();
            conn.shutdown();
        }
    }
    
    public boolean isOpen() {
        final OperatedClientConnection conn = this.getConnection();
        return conn != null && conn.isOpen();
    }
    
    public boolean isStale() {
        final OperatedClientConnection conn = this.getConnection();
        return conn == null || conn.isStale();
    }
    
    public void setSocketTimeout(final int timeout) {
        final OperatedClientConnection conn = this.ensureConnection();
        conn.setSocketTimeout(timeout);
    }
    
    public int getSocketTimeout() {
        final OperatedClientConnection conn = this.ensureConnection();
        return conn.getSocketTimeout();
    }
    
    public HttpConnectionMetrics getMetrics() {
        final OperatedClientConnection conn = this.ensureConnection();
        return conn.getMetrics();
    }
    
    public void flush() throws IOException {
        final OperatedClientConnection conn = this.ensureConnection();
        conn.flush();
    }
    
    public boolean isResponseAvailable(final int timeout) throws IOException {
        final OperatedClientConnection conn = this.ensureConnection();
        return conn.isResponseAvailable(timeout);
    }
    
    public void receiveResponseEntity(final HttpResponse response) throws HttpException, IOException {
        final OperatedClientConnection conn = this.ensureConnection();
        conn.receiveResponseEntity(response);
    }
    
    public HttpResponse receiveResponseHeader() throws HttpException, IOException {
        final OperatedClientConnection conn = this.ensureConnection();
        return conn.receiveResponseHeader();
    }
    
    public void sendRequestEntity(final HttpEntityEnclosingRequest request) throws HttpException, IOException {
        final OperatedClientConnection conn = this.ensureConnection();
        conn.sendRequestEntity(request);
    }
    
    public void sendRequestHeader(final HttpRequest request) throws HttpException, IOException {
        final OperatedClientConnection conn = this.ensureConnection();
        conn.sendRequestHeader(request);
    }
    
    public InetAddress getLocalAddress() {
        final OperatedClientConnection conn = this.ensureConnection();
        return conn.getLocalAddress();
    }
    
    public int getLocalPort() {
        final OperatedClientConnection conn = this.ensureConnection();
        return conn.getLocalPort();
    }
    
    public InetAddress getRemoteAddress() {
        final OperatedClientConnection conn = this.ensureConnection();
        return conn.getRemoteAddress();
    }
    
    public int getRemotePort() {
        final OperatedClientConnection conn = this.ensureConnection();
        return conn.getRemotePort();
    }
    
    public boolean isSecure() {
        final OperatedClientConnection conn = this.ensureConnection();
        return conn.isSecure();
    }
    
    public SSLSession getSSLSession() {
        final OperatedClientConnection conn = this.ensureConnection();
        SSLSession result = null;
        final Socket sock = conn.getSocket();
        if (sock instanceof SSLSocket) {
            result = ((SSLSocket)sock).getSession();
        }
        return result;
    }
    
    public Object getAttribute(final String id) {
        final OperatedClientConnection conn = this.ensureConnection();
        if (conn instanceof HttpContext) {
            return ((HttpContext)conn).getAttribute(id);
        }
        return null;
    }
    
    public Object removeAttribute(final String id) {
        final OperatedClientConnection conn = this.ensureConnection();
        if (conn instanceof HttpContext) {
            return ((HttpContext)conn).removeAttribute(id);
        }
        return null;
    }
    
    public void setAttribute(final String id, final Object obj) {
        final OperatedClientConnection conn = this.ensureConnection();
        if (conn instanceof HttpContext) {
            ((HttpContext)conn).setAttribute(id, obj);
        }
    }
    
    public HttpRoute getRoute() {
        final HttpPoolEntry local = this.ensurePoolEntry();
        return local.getEffectiveRoute();
    }
    
    public void open(final HttpRoute route, final HttpContext context, final HttpParams params) throws IOException {
        if (route == null) {
            throw new IllegalArgumentException("Route may not be null");
        }
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        final OperatedClientConnection conn;
        synchronized (this) {
            if (this.poolEntry == null) {
                throw new ConnectionShutdownException();
            }
            final RouteTracker tracker = this.poolEntry.getTracker();
            if (tracker.isConnected()) {
                throw new IllegalStateException("Connection already open");
            }
            conn = ((PoolEntry<T, OperatedClientConnection>)this.poolEntry).getConnection();
        }
        final HttpHost proxy = route.getProxyHost();
        this.operator.openConnection(conn, (proxy != null) ? proxy : route.getTargetHost(), route.getLocalAddress(), context, params);
        synchronized (this) {
            if (this.poolEntry == null) {
                throw new InterruptedIOException();
            }
            final RouteTracker tracker2 = this.poolEntry.getTracker();
            if (proxy == null) {
                tracker2.connectTarget(conn.isSecure());
            }
            else {
                tracker2.connectProxy(proxy, conn.isSecure());
            }
        }
    }
    
    public void tunnelTarget(final boolean secure, final HttpParams params) throws IOException {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        final HttpHost target;
        final OperatedClientConnection conn;
        synchronized (this) {
            if (this.poolEntry == null) {
                throw new ConnectionShutdownException();
            }
            final RouteTracker tracker = this.poolEntry.getTracker();
            if (!tracker.isConnected()) {
                throw new IllegalStateException("Connection not open");
            }
            if (tracker.isTunnelled()) {
                throw new IllegalStateException("Connection is already tunnelled");
            }
            target = tracker.getTargetHost();
            conn = ((PoolEntry<T, OperatedClientConnection>)this.poolEntry).getConnection();
        }
        conn.update(null, target, secure, params);
        synchronized (this) {
            if (this.poolEntry == null) {
                throw new InterruptedIOException();
            }
            final RouteTracker tracker = this.poolEntry.getTracker();
            tracker.tunnelTarget(secure);
        }
    }
    
    public void tunnelProxy(final HttpHost next, final boolean secure, final HttpParams params) throws IOException {
        if (next == null) {
            throw new IllegalArgumentException("Next proxy amy not be null");
        }
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        final OperatedClientConnection conn;
        synchronized (this) {
            if (this.poolEntry == null) {
                throw new ConnectionShutdownException();
            }
            final RouteTracker tracker = this.poolEntry.getTracker();
            if (!tracker.isConnected()) {
                throw new IllegalStateException("Connection not open");
            }
            conn = ((PoolEntry<T, OperatedClientConnection>)this.poolEntry).getConnection();
        }
        conn.update(null, next, secure, params);
        synchronized (this) {
            if (this.poolEntry == null) {
                throw new InterruptedIOException();
            }
            final RouteTracker tracker = this.poolEntry.getTracker();
            tracker.tunnelProxy(next, secure);
        }
    }
    
    public void layerProtocol(final HttpContext context, final HttpParams params) throws IOException {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        final HttpHost target;
        final OperatedClientConnection conn;
        synchronized (this) {
            if (this.poolEntry == null) {
                throw new ConnectionShutdownException();
            }
            final RouteTracker tracker = this.poolEntry.getTracker();
            if (!tracker.isConnected()) {
                throw new IllegalStateException("Connection not open");
            }
            if (!tracker.isTunnelled()) {
                throw new IllegalStateException("Protocol layering without a tunnel not supported");
            }
            if (tracker.isLayered()) {
                throw new IllegalStateException("Multiple protocol layering not supported");
            }
            target = tracker.getTargetHost();
            conn = ((PoolEntry<T, OperatedClientConnection>)this.poolEntry).getConnection();
        }
        this.operator.updateSecureConnection(conn, target, context, params);
        synchronized (this) {
            if (this.poolEntry == null) {
                throw new InterruptedIOException();
            }
            final RouteTracker tracker = this.poolEntry.getTracker();
            tracker.layerProtocol(conn.isSecure());
        }
    }
    
    public Object getState() {
        final HttpPoolEntry local = this.ensurePoolEntry();
        return local.getState();
    }
    
    public void setState(final Object state) {
        final HttpPoolEntry local = this.ensurePoolEntry();
        local.setState(state);
    }
    
    public void markReusable() {
        this.reusable = true;
    }
    
    public void unmarkReusable() {
        this.reusable = false;
    }
    
    public boolean isMarkedReusable() {
        return this.reusable;
    }
    
    public void setIdleDuration(final long duration, final TimeUnit unit) {
        if (duration > 0L) {
            this.duration = unit.toMillis(duration);
        }
        else {
            this.duration = -1L;
        }
    }
    
    public void releaseConnection() {
        synchronized (this) {
            if (this.poolEntry == null) {
                return;
            }
            this.manager.releaseConnection(this, this.duration, TimeUnit.MILLISECONDS);
            this.poolEntry = null;
        }
    }
    
    public void abortConnection() {
        synchronized (this) {
            if (this.poolEntry == null) {
                return;
            }
            this.reusable = false;
            final OperatedClientConnection conn = ((PoolEntry<T, OperatedClientConnection>)this.poolEntry).getConnection();
            try {
                conn.shutdown();
            }
            catch (IOException ex) {}
            this.manager.releaseConnection(this, this.duration, TimeUnit.MILLISECONDS);
            this.poolEntry = null;
        }
    }
}
