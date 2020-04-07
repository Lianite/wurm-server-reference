// 
// Decompiled by Procyon v0.5.30
// 

package javax.servlet.http;

import java.io.IOException;
import java.util.ResourceBundle;
import javax.servlet.ServletOutputStream;

class NoBodyOutputStream extends ServletOutputStream
{
    private static final String LSTRING_FILE = "javax.servlet.http.LocalStrings";
    private static ResourceBundle lStrings;
    private int contentLength;
    
    NoBodyOutputStream() {
        this.contentLength = 0;
    }
    
    int getContentLength() {
        return this.contentLength;
    }
    
    public void write(final int b) {
        ++this.contentLength;
    }
    
    public void write(final byte[] buf, final int offset, final int len) throws IOException {
        if (len >= 0) {
            this.contentLength += len;
            return;
        }
        throw new IOException(NoBodyOutputStream.lStrings.getString("err.io.negativelength"));
    }
    
    static {
        NoBodyOutputStream.lStrings = ResourceBundle.getBundle("javax.servlet.http.LocalStrings");
    }
}
