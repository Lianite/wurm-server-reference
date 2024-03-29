// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.entity;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import org.apache.http.annotation.NotThreadSafe;

@NotThreadSafe
public class BasicHttpEntity extends AbstractHttpEntity
{
    private InputStream content;
    private long length;
    
    public BasicHttpEntity() {
        this.length = -1L;
    }
    
    public long getContentLength() {
        return this.length;
    }
    
    public InputStream getContent() throws IllegalStateException {
        if (this.content == null) {
            throw new IllegalStateException("Content has not been provided");
        }
        return this.content;
    }
    
    public boolean isRepeatable() {
        return false;
    }
    
    public void setContentLength(final long len) {
        this.length = len;
    }
    
    public void setContent(final InputStream instream) {
        this.content = instream;
    }
    
    public void writeTo(final OutputStream outstream) throws IOException {
        if (outstream == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        final InputStream instream = this.getContent();
        try {
            final byte[] tmp = new byte[2048];
            int l;
            while ((l = instream.read(tmp)) != -1) {
                outstream.write(tmp, 0, l);
            }
        }
        finally {
            instream.close();
        }
    }
    
    public boolean isStreaming() {
        return this.content != null;
    }
    
    @Deprecated
    public void consumeContent() throws IOException {
        if (this.content != null) {
            this.content.close();
        }
    }
}
