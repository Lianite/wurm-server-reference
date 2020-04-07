// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel.fmt;

import java.io.IOException;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import com.sun.codemodel.JResourceFile;

public class JTextFile extends JResourceFile
{
    private String contents;
    
    public JTextFile(final String name) {
        super(name);
        this.contents = null;
    }
    
    public void setContents(final String _contents) {
        this.contents = _contents;
    }
    
    public void build(final OutputStream out) throws IOException {
        final Writer w = new OutputStreamWriter(out);
        w.write(this.contents);
        w.close();
    }
}
