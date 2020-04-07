// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.entity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import org.apache.http.annotation.NotThreadSafe;

@NotThreadSafe
public class SerializableEntity extends AbstractHttpEntity
{
    private byte[] objSer;
    private Serializable objRef;
    
    public SerializableEntity(final Serializable ser, final boolean bufferize) throws IOException {
        if (ser == null) {
            throw new IllegalArgumentException("Source object may not be null");
        }
        if (bufferize) {
            this.createBytes(ser);
        }
        else {
            this.objRef = ser;
        }
    }
    
    private void createBytes(final Serializable ser) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(ser);
        out.flush();
        this.objSer = baos.toByteArray();
    }
    
    public InputStream getContent() throws IOException, IllegalStateException {
        if (this.objSer == null) {
            this.createBytes(this.objRef);
        }
        return new ByteArrayInputStream(this.objSer);
    }
    
    public long getContentLength() {
        if (this.objSer == null) {
            return -1L;
        }
        return this.objSer.length;
    }
    
    public boolean isRepeatable() {
        return true;
    }
    
    public boolean isStreaming() {
        return this.objSer == null;
    }
    
    public void writeTo(final OutputStream outstream) throws IOException {
        if (outstream == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        if (this.objSer == null) {
            final ObjectOutputStream out = new ObjectOutputStream(outstream);
            out.writeObject(this.objRef);
            out.flush();
        }
        else {
            outstream.write(this.objSer);
            outstream.flush();
        }
    }
}
