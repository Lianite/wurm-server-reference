// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.impl.conn;

import org.apache.http.HttpRequest;
import org.apache.http.HttpException;
import org.apache.http.Header;
import org.apache.http.message.LineParser;
import org.apache.http.HttpResponse;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.HttpResponseFactory;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.params.HttpParams;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.HashMap;
import org.apache.commons.logging.LogFactory;
import java.util.Map;
import org.apache.http.HttpHost;
import java.net.Socket;
import org.apache.commons.logging.Log;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.protocol.HttpContext;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.impl.SocketHttpClientConnection;

@NotThreadSafe
public class DefaultClientConnection extends SocketHttpClientConnection implements OperatedClientConnection, HttpContext
{
    private final Log log;
    private final Log headerLog;
    private final Log wireLog;
    private volatile Socket socket;
    private HttpHost targetHost;
    private boolean connSecure;
    private volatile boolean shutdown;
    private final Map<String, Object> attributes;
    
    public DefaultClientConnection() {
        this.log = LogFactory.getLog(this.getClass());
        this.headerLog = LogFactory.getLog("org.apache.http.headers");
        this.wireLog = LogFactory.getLog("org.apache.http.wire");
        this.attributes = new HashMap<String, Object>();
    }
    
    public final HttpHost getTargetHost() {
        return this.targetHost;
    }
    
    public final boolean isSecure() {
        return this.connSecure;
    }
    
    public final Socket getSocket() {
        return this.socket;
    }
    
    public void opening(final Socket sock, final HttpHost target) throws IOException {
        this.assertNotOpen();
        this.socket = sock;
        this.targetHost = target;
        if (this.shutdown) {
            sock.close();
            throw new InterruptedIOException("Connection already shutdown");
        }
    }
    
    public void openCompleted(final boolean secure, final HttpParams params) throws IOException {
        this.assertNotOpen();
        if (params == null) {
            throw new IllegalArgumentException("Parameters must not be null.");
        }
        this.connSecure = secure;
        this.bind(this.socket, params);
    }
    
    public void shutdown() throws IOException {
        this.shutdown = true;
        try {
            super.shutdown();
            if (this.log.isDebugEnabled()) {
                this.log.debug("Connection " + this + " shut down");
            }
            final Socket sock = this.socket;
            if (sock != null) {
                sock.close();
            }
        }
        catch (IOException ex) {
            this.log.debug("I/O error shutting down connection", ex);
        }
    }
    
    public void close() throws IOException {
        try {
            super.close();
            if (this.log.isDebugEnabled()) {
                this.log.debug("Connection " + this + " closed");
            }
        }
        catch (IOException ex) {
            this.log.debug("I/O error closing connection", ex);
        }
    }
    
    protected SessionInputBuffer createSessionInputBuffer(final Socket socket, int buffersize, final HttpParams params) throws IOException {
        if (buffersize == -1) {
            buffersize = 8192;
        }
        SessionInputBuffer inbuffer = super.createSessionInputBuffer(socket, buffersize, params);
        if (this.wireLog.isDebugEnabled()) {
            inbuffer = new LoggingSessionInputBuffer(inbuffer, new Wire(this.wireLog), HttpProtocolParams.getHttpElementCharset(params));
        }
        return inbuffer;
    }
    
    protected SessionOutputBuffer createSessionOutputBuffer(final Socket socket, int buffersize, final HttpParams params) throws IOException {
        if (buffersize == -1) {
            buffersize = 8192;
        }
        SessionOutputBuffer outbuffer = super.createSessionOutputBuffer(socket, buffersize, params);
        if (this.wireLog.isDebugEnabled()) {
            outbuffer = new LoggingSessionOutputBuffer(outbuffer, new Wire(this.wireLog), HttpProtocolParams.getHttpElementCharset(params));
        }
        return outbuffer;
    }
    
    protected HttpMessageParser<HttpResponse> createResponseParser(final SessionInputBuffer buffer, final HttpResponseFactory responseFactory, final HttpParams params) {
        return new DefaultHttpResponseParser(buffer, null, responseFactory, params);
    }
    
    public void update(final Socket sock, final HttpHost target, final boolean secure, final HttpParams params) throws IOException {
        this.assertOpen();
        if (target == null) {
            throw new IllegalArgumentException("Target host must not be null.");
        }
        if (params == null) {
            throw new IllegalArgumentException("Parameters must not be null.");
        }
        if (sock != null) {
            this.bind(this.socket = sock, params);
        }
        this.targetHost = target;
        this.connSecure = secure;
    }
    
    public HttpResponse receiveResponseHeader() throws HttpException, IOException {
        final HttpResponse response = super.receiveResponseHeader();
        if (this.log.isDebugEnabled()) {
            this.log.debug("Receiving response: " + response.getStatusLine());
        }
        if (this.headerLog.isDebugEnabled()) {
            this.headerLog.debug("<< " + response.getStatusLine().toString());
            final Header[] arr$;
            final Header[] headers = arr$ = response.getAllHeaders();
            for (final Header header : arr$) {
                this.headerLog.debug("<< " + header.toString());
            }
        }
        return response;
    }
    
    public void sendRequestHeader(final HttpRequest request) throws HttpException, IOException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Sending request: " + request.getRequestLine());
        }
        super.sendRequestHeader(request);
        if (this.headerLog.isDebugEnabled()) {
            this.headerLog.debug(">> " + request.getRequestLine().toString());
            final Header[] arr$;
            final Header[] headers = arr$ = request.getAllHeaders();
            for (final Header header : arr$) {
                this.headerLog.debug(">> " + header.toString());
            }
        }
    }
    
    public Object getAttribute(final String id) {
        return this.attributes.get(id);
    }
    
    public Object removeAttribute(final String id) {
        return this.attributes.remove(id);
    }
    
    public void setAttribute(final String id, final Object obj) {
        this.attributes.put(id, obj);
    }
}
