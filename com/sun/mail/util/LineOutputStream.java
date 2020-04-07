// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.mail.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;

public class LineOutputStream extends FilterOutputStream
{
    private static byte[] newline;
    
    public LineOutputStream(final OutputStream out) {
        super(out);
    }
    
    public void writeln(final String s) throws IOException {
        final byte[] bytes = ASCIIUtility.getBytes(s);
        this.out.write(bytes);
        this.out.write(LineOutputStream.newline);
    }
    
    public void writeln() throws IOException {
        this.out.write(LineOutputStream.newline);
    }
    
    static {
        (LineOutputStream.newline = new byte[2])[0] = 13;
        LineOutputStream.newline[1] = 10;
    }
}
