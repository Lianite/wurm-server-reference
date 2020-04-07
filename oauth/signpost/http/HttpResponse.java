// 
// Decompiled by Procyon v0.5.30
// 

package oauth.signpost.http;

import java.io.InputStream;
import java.io.IOException;

public interface HttpResponse
{
    int getStatusCode() throws IOException;
    
    String getReasonPhrase() throws Exception;
    
    InputStream getContent() throws IOException;
    
    Object unwrap();
}
