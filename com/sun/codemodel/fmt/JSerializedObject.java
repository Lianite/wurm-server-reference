// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel.fmt;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import com.sun.codemodel.JResourceFile;

public class JSerializedObject extends JResourceFile
{
    private final Object obj;
    
    public JSerializedObject(final String name, final Object obj) throws IOException {
        super(name);
        this.obj = obj;
    }
    
    protected void build(final OutputStream os) throws IOException {
        final ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(this.obj);
        oos.close();
    }
}
