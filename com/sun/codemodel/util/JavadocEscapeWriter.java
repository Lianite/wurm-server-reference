// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel.util;

import java.io.IOException;
import java.io.Writer;
import java.io.FilterWriter;

public class JavadocEscapeWriter extends FilterWriter
{
    public JavadocEscapeWriter(final Writer next) {
        super(next);
    }
    
    public void write(final int ch) throws IOException {
        if (ch == 60) {
            this.out.write("&lt;");
        }
        else if (ch == 38) {
            this.out.write("&amp;");
        }
        else {
            this.out.write(ch);
        }
    }
    
    public void write(final char[] buf, final int off, final int len) throws IOException {
        for (int i = 0; i < len; ++i) {
            this.write(buf[off + i]);
        }
    }
    
    public void write(final char[] buf) throws IOException {
        this.write(buf, 0, buf.length);
    }
    
    public void write(final String buf, final int off, final int len) throws IOException {
        this.write(buf.toCharArray(), off, len);
    }
    
    public void write(final String buf) throws IOException {
        this.write(buf.toCharArray(), 0, buf.length());
    }
}
