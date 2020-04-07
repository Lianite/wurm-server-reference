// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc;

import java.io.File;
import java.io.OutputStream;
import java.io.IOException;
import java.io.Writer;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.writer.FilterCodeWriter;

final class ProgressCodeWriter extends FilterCodeWriter
{
    private int current;
    private final int totalFileCount;
    private final XJCListener progress;
    
    public ProgressCodeWriter(final CodeWriter output, final XJCListener progress, final int totalFileCount) {
        super(output);
        this.progress = progress;
        this.totalFileCount = totalFileCount;
        if (progress == null) {
            throw new IllegalArgumentException();
        }
    }
    
    public Writer openSource(final JPackage pkg, final String fileName) throws IOException {
        this.report(pkg, fileName);
        return super.openSource(pkg, fileName);
    }
    
    public OutputStream openBinary(final JPackage pkg, final String fileName) throws IOException {
        this.report(pkg, fileName);
        return super.openBinary(pkg, fileName);
    }
    
    private void report(final JPackage pkg, final String fileName) {
        String name = pkg.name().replace('.', File.separatorChar);
        if (name.length() != 0) {
            name += File.separatorChar;
        }
        name += fileName;
        if (this.progress.isCanceled()) {
            throw new AbortException();
        }
        this.progress.generatedFile(name, this.current++, this.totalFileCount);
    }
}
