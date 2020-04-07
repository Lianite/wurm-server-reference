// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.entity;

import java.io.OutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.File;
import org.apache.http.annotation.NotThreadSafe;

@NotThreadSafe
public class FileEntity extends AbstractHttpEntity implements Cloneable
{
    protected final File file;
    
    public FileEntity(final File file, final String contentType) {
        if (file == null) {
            throw new IllegalArgumentException("File may not be null");
        }
        this.file = file;
        this.setContentType(contentType);
    }
    
    public FileEntity(final File file, final ContentType contentType) {
        if (file == null) {
            throw new IllegalArgumentException("File may not be null");
        }
        this.file = file;
        if (contentType != null) {
            this.setContentType(contentType.toString());
        }
    }
    
    public FileEntity(final File file) {
        if (file == null) {
            throw new IllegalArgumentException("File may not be null");
        }
        this.file = file;
    }
    
    public boolean isRepeatable() {
        return true;
    }
    
    public long getContentLength() {
        return this.file.length();
    }
    
    public InputStream getContent() throws IOException {
        return new FileInputStream(this.file);
    }
    
    public void writeTo(final OutputStream outstream) throws IOException {
        if (outstream == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        final InputStream instream = new FileInputStream(this.file);
        try {
            final byte[] tmp = new byte[4096];
            int l;
            while ((l = instream.read(tmp)) != -1) {
                outstream.write(tmp, 0, l);
            }
            outstream.flush();
        }
        finally {
            instream.close();
        }
    }
    
    public boolean isStreaming() {
        return false;
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
