// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel;

import java.io.IOException;
import java.io.OutputStream;

public interface CodeWriter
{
    OutputStream open(final JPackage p0, final String p1) throws IOException;
    
    void close() throws IOException;
}
