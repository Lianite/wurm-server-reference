// 
// Decompiled by Procyon v0.5.30
// 

package javax.activation;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;

public interface DataSource
{
    InputStream getInputStream() throws IOException;
    
    OutputStream getOutputStream() throws IOException;
    
    String getContentType();
    
    String getName();
}
