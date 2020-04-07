// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.validator;

import java.io.IOException;
import java.io.Writer;
import java.io.OutputStream;

public class StringOutputStream extends OutputStream
{
    private final Writer writer;
    
    public StringOutputStream(final Writer _writer) {
        this.writer = _writer;
    }
    
    public void write(final int ch) throws IOException {
        this.writer.write(ch);
    }
    
    public void write(final byte[] data) throws IOException {
        this.write(data, 0, data.length);
    }
    
    public void write(final byte[] data, final int start, final int len) throws IOException {
        final char[] buf = new char[len];
        for (int i = 0; i < len; ++i) {
            buf[i] = (char)(data[i + start] & 0xFF);
        }
        this.writer.write(buf);
    }
    
    public void close() throws IOException {
        this.writer.close();
    }
    
    public void flush() throws IOException {
        this.writer.flush();
    }
}
