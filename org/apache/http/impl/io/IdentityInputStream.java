// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.impl.io;

import java.io.IOException;
import org.apache.http.io.BufferInfo;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.annotation.NotThreadSafe;
import java.io.InputStream;

@NotThreadSafe
public class IdentityInputStream extends InputStream
{
    private final SessionInputBuffer in;
    private boolean closed;
    
    public IdentityInputStream(final SessionInputBuffer in) {
        this.closed = false;
        if (in == null) {
            throw new IllegalArgumentException("Session input buffer may not be null");
        }
        this.in = in;
    }
    
    public int available() throws IOException {
        if (this.in instanceof BufferInfo) {
            return ((BufferInfo)this.in).length();
        }
        return 0;
    }
    
    public void close() throws IOException {
        this.closed = true;
    }
    
    public int read() throws IOException {
        if (this.closed) {
            return -1;
        }
        return this.in.read();
    }
    
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (this.closed) {
            return -1;
        }
        return this.in.read(b, off, len);
    }
}
