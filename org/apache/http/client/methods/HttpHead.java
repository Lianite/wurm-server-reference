// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.client.methods;

import java.net.URI;
import org.apache.http.annotation.NotThreadSafe;

@NotThreadSafe
public class HttpHead extends HttpRequestBase
{
    public static final String METHOD_NAME = "HEAD";
    
    public HttpHead() {
    }
    
    public HttpHead(final URI uri) {
        this.setURI(uri);
    }
    
    public HttpHead(final String uri) {
        this.setURI(URI.create(uri));
    }
    
    public String getMethod() {
        return "HEAD";
    }
}
