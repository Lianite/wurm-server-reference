// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.client.methods;

import org.apache.http.params.HttpParams;
import org.apache.http.client.utils.CloneUtils;
import org.apache.http.message.HeaderGroup;
import java.io.IOException;
import org.apache.http.message.BasicRequestLine;
import org.apache.http.RequestLine;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.ProtocolVersion;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.http.conn.ConnectionReleaseTrigger;
import org.apache.http.conn.ClientConnectionRequest;
import java.net.URI;
import java.util.concurrent.locks.Lock;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.message.AbstractHttpMessage;

@NotThreadSafe
public abstract class HttpRequestBase extends AbstractHttpMessage implements HttpUriRequest, AbortableHttpRequest, Cloneable
{
    private Lock abortLock;
    private volatile boolean aborted;
    private URI uri;
    private ClientConnectionRequest connRequest;
    private ConnectionReleaseTrigger releaseTrigger;
    
    public HttpRequestBase() {
        this.abortLock = new ReentrantLock();
    }
    
    public abstract String getMethod();
    
    public ProtocolVersion getProtocolVersion() {
        return HttpProtocolParams.getVersion(this.getParams());
    }
    
    public URI getURI() {
        return this.uri;
    }
    
    public RequestLine getRequestLine() {
        final String method = this.getMethod();
        final ProtocolVersion ver = this.getProtocolVersion();
        final URI uri = this.getURI();
        String uritext = null;
        if (uri != null) {
            uritext = uri.toASCIIString();
        }
        if (uritext == null || uritext.length() == 0) {
            uritext = "/";
        }
        return new BasicRequestLine(method, uritext, ver);
    }
    
    public void setURI(final URI uri) {
        this.uri = uri;
    }
    
    public void setConnectionRequest(final ClientConnectionRequest connRequest) throws IOException {
        if (this.aborted) {
            throw new IOException("Request already aborted");
        }
        this.abortLock.lock();
        try {
            this.connRequest = connRequest;
        }
        finally {
            this.abortLock.unlock();
        }
    }
    
    public void setReleaseTrigger(final ConnectionReleaseTrigger releaseTrigger) throws IOException {
        if (this.aborted) {
            throw new IOException("Request already aborted");
        }
        this.abortLock.lock();
        try {
            this.releaseTrigger = releaseTrigger;
        }
        finally {
            this.abortLock.unlock();
        }
    }
    
    private void cleanup() {
        if (this.connRequest != null) {
            this.connRequest.abortRequest();
            this.connRequest = null;
        }
        if (this.releaseTrigger != null) {
            try {
                this.releaseTrigger.abortConnection();
            }
            catch (IOException ex) {}
            this.releaseTrigger = null;
        }
    }
    
    public void abort() {
        if (this.aborted) {
            return;
        }
        this.abortLock.lock();
        try {
            this.aborted = true;
            this.cleanup();
        }
        finally {
            this.abortLock.unlock();
        }
    }
    
    public boolean isAborted() {
        return this.aborted;
    }
    
    public void reset() {
        this.abortLock.lock();
        try {
            this.cleanup();
            this.aborted = false;
        }
        finally {
            this.abortLock.unlock();
        }
    }
    
    public void releaseConnection() {
        this.reset();
    }
    
    public Object clone() throws CloneNotSupportedException {
        final HttpRequestBase clone = (HttpRequestBase)super.clone();
        clone.abortLock = new ReentrantLock();
        clone.aborted = false;
        clone.releaseTrigger = null;
        clone.connRequest = null;
        clone.headergroup = (HeaderGroup)CloneUtils.clone(this.headergroup);
        clone.params = (HttpParams)CloneUtils.clone(this.params);
        return clone;
    }
    
    public String toString() {
        return this.getMethod() + " " + this.getURI() + " " + this.getProtocolVersion();
    }
}
