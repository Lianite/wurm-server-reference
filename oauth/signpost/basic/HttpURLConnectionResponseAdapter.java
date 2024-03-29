// 
// Decompiled by Procyon v0.5.30
// 

package oauth.signpost.basic;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import oauth.signpost.http.HttpResponse;

public class HttpURLConnectionResponseAdapter implements HttpResponse
{
    private HttpURLConnection connection;
    
    public HttpURLConnectionResponseAdapter(final HttpURLConnection connection) {
        this.connection = connection;
    }
    
    public InputStream getContent() throws IOException {
        try {
            return this.connection.getInputStream();
        }
        catch (IOException e) {
            return this.connection.getErrorStream();
        }
    }
    
    public int getStatusCode() throws IOException {
        return this.connection.getResponseCode();
    }
    
    public String getReasonPhrase() throws Exception {
        return this.connection.getResponseMessage();
    }
    
    public Object unwrap() {
        return this.connection;
    }
}
