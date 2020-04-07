// 
// Decompiled by Procyon v0.5.30
// 

package javax.activation;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;

class DataHandlerDataSource implements DataSource
{
    DataHandler dataHandler;
    
    public DataHandlerDataSource(final DataHandler dh) {
        this.dataHandler = null;
        this.dataHandler = dh;
    }
    
    public InputStream getInputStream() throws IOException {
        return this.dataHandler.getInputStream();
    }
    
    public OutputStream getOutputStream() throws IOException {
        return this.dataHandler.getOutputStream();
    }
    
    public String getContentType() {
        return this.dataHandler.getContentType();
    }
    
    public String getName() {
        return this.dataHandler.getName();
    }
}
