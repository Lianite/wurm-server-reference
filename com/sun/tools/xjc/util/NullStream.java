// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.util;

import java.io.IOException;
import java.io.OutputStream;

public class NullStream extends OutputStream
{
    public void write(final int b) throws IOException {
    }
    
    public void close() throws IOException {
    }
    
    public void flush() throws IOException {
    }
    
    public void write(final byte[] b, final int off, final int len) throws IOException {
    }
    
    public void write(final byte[] b) throws IOException {
    }
}
