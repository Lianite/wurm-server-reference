// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.api.util;

import java.io.Writer;
import java.io.IOException;
import java.io.File;
import java.io.OutputStream;
import com.sun.codemodel.JPackage;
import com.sun.mirror.apt.Filer;
import com.sun.codemodel.CodeWriter;

public final class FilerCodeWriter extends CodeWriter
{
    private final Filer filer;
    
    public FilerCodeWriter(final Filer filer) {
        this.filer = filer;
    }
    
    public OutputStream openBinary(final JPackage pkg, final String fileName) throws IOException {
        Filer.Location loc;
        if (fileName.endsWith(".java")) {
            loc = Filer.Location.SOURCE_TREE;
        }
        else {
            loc = Filer.Location.CLASS_TREE;
        }
        return this.filer.createBinaryFile(loc, pkg.name(), new File(fileName));
    }
    
    public Writer openSource(final JPackage pkg, final String fileName) throws IOException {
        String name;
        if (pkg.isUnnamed()) {
            name = fileName;
        }
        else {
            name = pkg.name() + '.' + fileName;
        }
        name = name.substring(0, name.length() - 5);
        return this.filer.createSourceFile(name);
    }
    
    public void close() {
    }
}
