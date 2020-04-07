// 
// Decompiled by Procyon v0.5.30
// 

package oauth.signpost.basic;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.net.HttpURLConnection;
import oauth.signpost.http.HttpRequest;

public class HttpURLConnectionRequestAdapter implements HttpRequest
{
    protected HttpURLConnection connection;
    
    public HttpURLConnectionRequestAdapter(final HttpURLConnection connection) {
        this.connection = connection;
    }
    
    public String getMethod() {
        return this.connection.getRequestMethod();
    }
    
    public String getRequestUrl() {
        return this.connection.getURL().toExternalForm();
    }
    
    public void setRequestUrl(final String url) {
    }
    
    public void setHeader(final String name, final String value) {
        this.connection.setRequestProperty(name, value);
    }
    
    public String getHeader(final String name) {
        return this.connection.getRequestProperty(name);
    }
    
    public Map<String, String> getAllHeaders() {
        final Map<String, List<String>> origHeaders = this.connection.getRequestProperties();
        final Map<String, String> headers = new HashMap<String, String>(origHeaders.size());
        for (final String name : origHeaders.keySet()) {
            final List<String> values = origHeaders.get(name);
            if (!values.isEmpty()) {
                headers.put(name, values.get(0));
            }
        }
        return headers;
    }
    
    public InputStream getMessagePayload() throws IOException {
        return null;
    }
    
    public String getContentType() {
        return this.connection.getRequestProperty("Content-Type");
    }
    
    public HttpURLConnection unwrap() {
        return this.connection;
    }
}
