// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.impl.io;

import java.io.IOException;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.annotation.NotThreadSafe;
import java.io.OutputStream;

@NotThreadSafe
public class ContentLengthOutputStream extends OutputStream
{
    private final SessionOutputBuffer out;
    private final long contentLength;
    private long total;
    private boolean closed;
    
    public ContentLengthOutputStream(final SessionOutputBuffer out, final long contentLength) {
        this.total = 0L;
        this.closed = false;
        if (out == null) {
            throw new IllegalArgumentException("Session output buffer may not be null");
        }
        if (contentLength < 0L) {
            throw new IllegalArgumentException("Content length may not be negative");
        }
        this.out = out;
        this.contentLength = contentLength;
    }
    
    public void close() throws IOException {
        if (!this.closed) {
            this.closed = true;
            this.out.flush();
        }
    }
    
    public void flush() throws IOException {
        this.out.flush();
    }
    
    public void write(final byte[] b, final int off, int len) throws IOException {
        if (this.closed) {
            throw new IOException("Attempted write to closed stream.");
        }
        if (this.total < this.contentLength) {
            final long max = this.contentLength - this.total;
            if (len > max) {
                len = (int)max;
            }
            this.out.write(b, off, len);
            this.total += len;
        }
    }
    
    public void write(final byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }
    
    public void write(final int b) throws IOException {
        if (this.closed) {
            throw new IOException("Attempted write to closed stream.");
        }
        if (this.total < this.contentLength) {
            this.out.write(b);
            ++this.total;
        }
    }
}
