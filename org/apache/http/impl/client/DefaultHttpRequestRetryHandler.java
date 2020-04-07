// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.impl.client;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import javax.net.ssl.SSLException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.io.InterruptedIOException;
import org.apache.http.protocol.HttpContext;
import java.io.IOException;
import org.apache.http.annotation.Immutable;
import org.apache.http.client.HttpRequestRetryHandler;

@Immutable
public class DefaultHttpRequestRetryHandler implements HttpRequestRetryHandler
{
    private final int retryCount;
    private final boolean requestSentRetryEnabled;
    
    public DefaultHttpRequestRetryHandler(final int retryCount, final boolean requestSentRetryEnabled) {
        this.retryCount = retryCount;
        this.requestSentRetryEnabled = requestSentRetryEnabled;
    }
    
    public DefaultHttpRequestRetryHandler() {
        this(3, false);
    }
    
    public boolean retryRequest(final IOException exception, final int executionCount, final HttpContext context) {
        if (exception == null) {
            throw new IllegalArgumentException("Exception parameter may not be null");
        }
        if (context == null) {
            throw new IllegalArgumentException("HTTP context may not be null");
        }
        if (executionCount > this.retryCount) {
            return false;
        }
        if (exception instanceof InterruptedIOException) {
            return false;
        }
        if (exception instanceof UnknownHostException) {
            return false;
        }
        if (exception instanceof ConnectException) {
            return false;
        }
        if (exception instanceof SSLException) {
            return false;
        }
        final HttpRequest request = (HttpRequest)context.getAttribute("http.request");
        if (this.requestIsAborted(request)) {
            return false;
        }
        if (this.handleAsIdempotent(request)) {
            return true;
        }
        final Boolean b = (Boolean)context.getAttribute("http.request_sent");
        final boolean sent = b != null && b;
        return !sent || this.requestSentRetryEnabled;
    }
    
    public boolean isRequestSentRetryEnabled() {
        return this.requestSentRetryEnabled;
    }
    
    public int getRetryCount() {
        return this.retryCount;
    }
    
    protected boolean handleAsIdempotent(final HttpRequest request) {
        return !(request instanceof HttpEntityEnclosingRequest);
    }
    
    protected boolean requestIsAborted(final HttpRequest request) {
        HttpRequest req = request;
        if (request instanceof RequestWrapper) {
            req = ((RequestWrapper)request).getOriginal();
        }
        return req instanceof HttpUriRequest && ((HttpUriRequest)req).isAborted();
    }
}
