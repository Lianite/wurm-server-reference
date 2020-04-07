// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel.fmt;

import java.util.Hashtable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import com.sun.codemodel.JResourceFile;

public class JPropertyFile extends JResourceFile
{
    private final Properties data;
    
    public JPropertyFile(final String name) {
        super(name);
        this.data = new Properties();
    }
    
    public void add(final String key, final String value) {
        ((Hashtable<String, String>)this.data).put(key, value);
    }
    
    public void build(final OutputStream out) throws IOException {
        this.data.store(out, null);
    }
}
