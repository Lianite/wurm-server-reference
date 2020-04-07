// 
// Decompiled by Procyon v0.5.30
// 

package javax.jnlp;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;

public interface FileContents
{
    String getName() throws IOException;
    
    InputStream getInputStream() throws IOException;
    
    OutputStream getOutputStream(final boolean p0) throws IOException;
    
    long getLength() throws IOException;
    
    boolean canRead() throws IOException;
    
    boolean canWrite() throws IOException;
    
    JNLPRandomAccessFile getRandomAccessFile(final String p0) throws IOException;
    
    long getMaxLength() throws IOException;
    
    long setMaxLength(final long p0) throws IOException;
}
