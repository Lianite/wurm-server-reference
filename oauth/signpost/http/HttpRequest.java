// 
// Decompiled by Procyon v0.5.30
// 

package oauth.signpost.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public interface HttpRequest
{
    String getMethod();
    
    String getRequestUrl();
    
    void setRequestUrl(final String p0);
    
    void setHeader(final String p0, final String p1);
    
    String getHeader(final String p0);
    
    Map<String, String> getAllHeaders();
    
    InputStream getMessagePayload() throws IOException;
    
    String getContentType();
    
    Object unwrap();
}
