// 
// Decompiled by Procyon v0.5.30
// 

package oauth.signpost.basic;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import oauth.signpost.http.HttpRequest;

public class UrlStringRequestAdapter implements HttpRequest
{
    private String url;
    
    public UrlStringRequestAdapter(final String url) {
        this.url = url;
    }
    
    public String getMethod() {
        return "GET";
    }
    
    public String getRequestUrl() {
        return this.url;
    }
    
    public void setRequestUrl(final String url) {
        this.url = url;
    }
    
    public void setHeader(final String name, final String value) {
    }
    
    public String getHeader(final String name) {
        return null;
    }
    
    public Map<String, String> getAllHeaders() {
        return Collections.emptyMap();
    }
    
    public InputStream getMessagePayload() throws IOException {
        return null;
    }
    
    public String getContentType() {
        return null;
    }
    
    public Object unwrap() {
        return this.url;
    }
}
