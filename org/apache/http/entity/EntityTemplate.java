// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.entity;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class EntityTemplate extends AbstractHttpEntity
{
    private final ContentProducer contentproducer;
    
    public EntityTemplate(final ContentProducer contentproducer) {
        if (contentproducer == null) {
            throw new IllegalArgumentException("Content producer may not be null");
        }
        this.contentproducer = contentproducer;
    }
    
    public long getContentLength() {
        return -1L;
    }
    
    public InputStream getContent() throws IOException {
        final ByteArrayOutputStream buf = new ByteArrayOutputStream();
        this.writeTo(buf);
        return new ByteArrayInputStream(buf.toByteArray());
    }
    
    public boolean isRepeatable() {
        return true;
    }
    
    public void writeTo(final OutputStream outstream) throws IOException {
        if (outstream == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        this.contentproducer.writeTo(outstream);
    }
    
    public boolean isStreaming() {
        return false;
    }
}
