// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.entity;

import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpEntity;
import org.apache.http.annotation.NotThreadSafe;

@NotThreadSafe
public class BufferedHttpEntity extends HttpEntityWrapper
{
    private final byte[] buffer;
    
    public BufferedHttpEntity(final HttpEntity entity) throws IOException {
        super(entity);
        if (!entity.isRepeatable() || entity.getContentLength() < 0L) {
            this.buffer = EntityUtils.toByteArray(entity);
        }
        else {
            this.buffer = null;
        }
    }
    
    public long getContentLength() {
        if (this.buffer != null) {
            return this.buffer.length;
        }
        return this.wrappedEntity.getContentLength();
    }
    
    public InputStream getContent() throws IOException {
        if (this.buffer != null) {
            return new ByteArrayInputStream(this.buffer);
        }
        return this.wrappedEntity.getContent();
    }
    
    public boolean isChunked() {
        return this.buffer == null && this.wrappedEntity.isChunked();
    }
    
    public boolean isRepeatable() {
        return true;
    }
    
    public void writeTo(final OutputStream outstream) throws IOException {
        if (outstream == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        if (this.buffer != null) {
            outstream.write(this.buffer);
        }
        else {
            this.wrappedEntity.writeTo(outstream);
        }
    }
    
    public boolean isStreaming() {
        return this.buffer == null && this.wrappedEntity.isStreaming();
    }
}
