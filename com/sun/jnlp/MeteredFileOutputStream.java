// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.jnlp;

import java.io.IOException;
import java.io.FileNotFoundException;
import com.sun.deploy.resources.ResourceManager;
import java.io.File;
import java.io.FileOutputStream;

final class MeteredFileOutputStream extends FileOutputStream
{
    static String _message;
    private FileContentsImpl _contents;
    private long _written;
    
    MeteredFileOutputStream(final File file, final boolean b, final FileContentsImpl contents) throws FileNotFoundException {
        super(file.getAbsolutePath(), b);
        this._written = 0L;
        this._contents = contents;
        this._written = file.length();
        if (MeteredFileOutputStream._message == null) {
            MeteredFileOutputStream._message = ResourceManager.getString("APIImpl.persistence.filesizemessage");
        }
    }
    
    public void write(final int n) throws IOException {
        this.checkWrite(1);
        super.write(n);
        ++this._written;
    }
    
    public void write(final byte[] array, final int n, final int n2) throws IOException {
        this.checkWrite(n2);
        super.write(array, n, n2);
        this._written += n2;
    }
    
    public void write(final byte[] array) throws IOException {
        this.write(array, 0, array.length);
    }
    
    private void checkWrite(final int n) throws IOException {
        if (this._written + n > this._contents.getMaxLength()) {
            throw new IOException(MeteredFileOutputStream._message);
        }
    }
    
    static {
        MeteredFileOutputStream._message = null;
    }
}
