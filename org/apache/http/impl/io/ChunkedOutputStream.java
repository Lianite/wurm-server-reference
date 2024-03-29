// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.impl.io;

import java.io.IOException;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.annotation.NotThreadSafe;
import java.io.OutputStream;

@NotThreadSafe
public class ChunkedOutputStream extends OutputStream
{
    private final SessionOutputBuffer out;
    private byte[] cache;
    private int cachePosition;
    private boolean wroteLastChunk;
    private boolean closed;
    
    public ChunkedOutputStream(final SessionOutputBuffer out, final int bufferSize) throws IOException {
        this.cachePosition = 0;
        this.wroteLastChunk = false;
        this.closed = false;
        this.cache = new byte[bufferSize];
        this.out = out;
    }
    
    public ChunkedOutputStream(final SessionOutputBuffer out) throws IOException {
        this(out, 2048);
    }
    
    protected void flushCache() throws IOException {
        if (this.cachePosition > 0) {
            this.out.writeLine(Integer.toHexString(this.cachePosition));
            this.out.write(this.cache, 0, this.cachePosition);
            this.out.writeLine("");
            this.cachePosition = 0;
        }
    }
    
    protected void flushCacheWithAppend(final byte[] bufferToAppend, final int off, final int len) throws IOException {
        this.out.writeLine(Integer.toHexString(this.cachePosition + len));
        this.out.write(this.cache, 0, this.cachePosition);
        this.out.write(bufferToAppend, off, len);
        this.out.writeLine("");
        this.cachePosition = 0;
    }
    
    protected void writeClosingChunk() throws IOException {
        this.out.writeLine("0");
        this.out.writeLine("");
    }
    
    public void finish() throws IOException {
        if (!this.wroteLastChunk) {
            this.flushCache();
            this.writeClosingChunk();
            this.wroteLastChunk = true;
        }
    }
    
    public void write(final int b) throws IOException {
        if (this.closed) {
            throw new IOException("Attempted write to closed stream.");
        }
        this.cache[this.cachePosition] = (byte)b;
        ++this.cachePosition;
        if (this.cachePosition == this.cache.length) {
            this.flushCache();
        }
    }
    
    public void write(final byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }
    
    public void write(final byte[] src, final int off, final int len) throws IOException {
        if (this.closed) {
            throw new IOException("Attempted write to closed stream.");
        }
        if (len >= this.cache.length - this.cachePosition) {
            this.flushCacheWithAppend(src, off, len);
        }
        else {
            System.arraycopy(src, off, this.cache, this.cachePosition, len);
            this.cachePosition += len;
        }
    }
    
    public void flush() throws IOException {
        this.flushCache();
        this.out.flush();
    }
    
    public void close() throws IOException {
        if (!this.closed) {
            this.closed = true;
            this.finish();
            this.out.flush();
        }
    }
}
