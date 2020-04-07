// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.codemodel.util;

import java.io.IOException;
import java.io.Writer;
import java.io.FilterWriter;

public class UnicodeEscapeWriter extends FilterWriter
{
    public UnicodeEscapeWriter(final Writer next) {
        super(next);
    }
    
    public final void write(final int ch) throws IOException {
        if (!this.requireEscaping(ch)) {
            this.out.write(ch);
        }
        else {
            this.out.write("\\u");
            final String s = Integer.toHexString(ch);
            for (int i = s.length(); i < 4; ++i) {
                this.out.write(48);
            }
            this.out.write(s);
        }
    }
    
    protected boolean requireEscaping(final int ch) {
        return ch >= 128 || (ch < 32 && " \t\r\n".indexOf(ch) == -1);
    }
    
    public final void write(final char[] buf, final int off, final int len) throws IOException {
        for (int i = 0; i < len; ++i) {
            this.write(buf[off + i]);
        }
    }
    
    public final void write(final char[] buf) throws IOException {
        this.write(buf, 0, buf.length);
    }
    
    public final void write(final String buf, final int off, final int len) throws IOException {
        this.write(buf.toCharArray(), off, len);
    }
    
    public final void write(final String buf) throws IOException {
        this.write(buf.toCharArray(), 0, buf.length());
    }
}
