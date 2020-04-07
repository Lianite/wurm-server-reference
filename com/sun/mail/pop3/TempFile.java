// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.mail.pop3;

import java.io.IOException;
import java.io.File;

class TempFile
{
    private File file;
    private WritableSharedFile sf;
    
    public TempFile(final File dir) throws IOException {
        (this.file = File.createTempFile("pop3.", ".mbox", dir)).deleteOnExit();
        this.sf = new WritableSharedFile(this.file);
    }
    
    public AppendStream getAppendStream() throws IOException {
        return this.sf.getAppendStream();
    }
    
    public void close() {
        try {
            this.sf.close();
        }
        catch (IOException ex) {}
        this.file.delete();
    }
    
    protected void finalize() throws Throwable {
        super.finalize();
        this.close();
    }
}
