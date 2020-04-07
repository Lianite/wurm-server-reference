// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel.fmt;

import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import com.sun.codemodel.JResourceFile;

public final class JBinaryFile extends JResourceFile
{
    private final ByteArrayOutputStream baos;
    
    public JBinaryFile(final String name) {
        super(name);
        this.baos = new ByteArrayOutputStream();
    }
    
    public OutputStream getDataStore() {
        return this.baos;
    }
    
    public void build(final OutputStream os) throws IOException {
        os.write(this.baos.toByteArray());
    }
}
