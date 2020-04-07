// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.client.entity;

import java.io.OutputStream;
import java.io.IOException;
import org.apache.http.HttpEntity;
import java.io.InputStream;
import org.apache.http.entity.HttpEntityWrapper;

abstract class DecompressingEntity extends HttpEntityWrapper
{
    private static final int BUFFER_SIZE = 2048;
    private InputStream content;
    
    public DecompressingEntity(final HttpEntity wrapped) {
        super(wrapped);
    }
    
    abstract InputStream decorate(final InputStream p0) throws IOException;
    
    private InputStream getDecompressingStream() throws IOException {
        final InputStream in = this.wrappedEntity.getContent();
        try {
            return this.decorate(in);
        }
        catch (IOException ex) {
            in.close();
            throw ex;
        }
    }
    
    public InputStream getContent() throws IOException {
        if (this.wrappedEntity.isStreaming()) {
            if (this.content == null) {
                this.content = this.getDecompressingStream();
            }
            return this.content;
        }
        return this.getDecompressingStream();
    }
    
    public void writeTo(final OutputStream outstream) throws IOException {
        if (outstream == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        final InputStream instream = this.getContent();
        try {
            final byte[] buffer = new byte[2048];
            int l;
            while ((l = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, l);
            }
        }
        finally {
            instream.close();
        }
    }
}
