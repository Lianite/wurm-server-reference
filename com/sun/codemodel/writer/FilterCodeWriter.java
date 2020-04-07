// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel.writer;

import java.io.Writer;
import java.io.IOException;
import java.io.OutputStream;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.CodeWriter;

public class FilterCodeWriter extends CodeWriter
{
    protected CodeWriter core;
    
    public FilterCodeWriter(final CodeWriter core) {
        this.core = core;
    }
    
    public OutputStream openBinary(final JPackage pkg, final String fileName) throws IOException {
        return this.core.openBinary(pkg, fileName);
    }
    
    public Writer openSource(final JPackage pkg, final String fileName) throws IOException {
        return this.core.openSource(pkg, fileName);
    }
    
    public void close() throws IOException {
        this.core.close();
    }
}
