// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util;

import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.Writer;
import java.io.StringWriter;
import java.io.Reader;

public class FileCopyUtils
{
    public static String copyToString(final Reader in) throws IOException {
        final StringWriter out = new StringWriter();
        copy(in, out);
        final String str = out.toString();
        if (str.startsWith("\ufeff")) {
            return str.substring(1);
        }
        return str;
    }
    
    public static byte[] copyToByteArray(final InputStream in) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
        copy(in, out);
        return out.toByteArray();
    }
    
    private static void copy(final Reader in, final Writer out) throws IOException {
        try {
            final char[] buffer = new char[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
        }
        finally {
            try {
                in.close();
            }
            catch (IOException ex) {}
            try {
                out.close();
            }
            catch (IOException ex2) {}
        }
    }
    
    private static int copy(final InputStream in, final OutputStream out) throws IOException {
        try {
            int byteCount = 0;
            final byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                byteCount += bytesRead;
            }
            out.flush();
            return byteCount;
        }
        finally {
            try {
                in.close();
            }
            catch (IOException ex) {}
            try {
                out.close();
            }
            catch (IOException ex2) {}
        }
    }
}
