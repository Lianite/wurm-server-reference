// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.message;

import org.apache.http.params.HttpProtocolParams;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.HttpRequest;

@NotThreadSafe
public class BasicHttpRequest extends AbstractHttpMessage implements HttpRequest
{
    private final String method;
    private final String uri;
    private RequestLine requestline;
    
    public BasicHttpRequest(final String method, final String uri) {
        if (method == null) {
            throw new IllegalArgumentException("Method name may not be null");
        }
        if (uri == null) {
            throw new IllegalArgumentException("Request URI may not be null");
        }
        this.method = method;
        this.uri = uri;
        this.requestline = null;
    }
    
    public BasicHttpRequest(final String method, final String uri, final ProtocolVersion ver) {
        this(new BasicRequestLine(method, uri, ver));
    }
    
    public BasicHttpRequest(final RequestLine requestline) {
        if (requestline == null) {
            throw new IllegalArgumentException("Request line may not be null");
        }
        this.requestline = requestline;
        this.method = requestline.getMethod();
        this.uri = requestline.getUri();
    }
    
    public ProtocolVersion getProtocolVersion() {
        return this.getRequestLine().getProtocolVersion();
    }
    
    public RequestLine getRequestLine() {
        if (this.requestline == null) {
            final ProtocolVersion ver = HttpProtocolParams.getVersion(this.getParams());
            this.requestline = new BasicRequestLine(this.method, this.uri, ver);
        }
        return this.requestline;
    }
    
    public String toString() {
        return this.method + " " + this.uri + " " + this.headergroup;
    }
}
