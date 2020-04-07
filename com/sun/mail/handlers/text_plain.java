// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.mail.handlers;

import java.io.FilterOutputStream;
import javax.mail.internet.MimeUtility;
import javax.mail.internet.ContentType;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.InputStreamReader;
import java.io.IOException;
import javax.activation.DataSource;
import java.awt.datatransfer.DataFlavor;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;

public class text_plain implements DataContentHandler
{
    private static ActivationDataFlavor myDF;
    static /* synthetic */ Class class$java$lang$String;
    
    protected ActivationDataFlavor getDF() {
        return text_plain.myDF;
    }
    
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { this.getDF() };
    }
    
    public Object getTransferData(final DataFlavor df, final DataSource ds) throws IOException {
        if (this.getDF().equals(df)) {
            return this.getContent(ds);
        }
        return null;
    }
    
    public Object getContent(final DataSource ds) throws IOException {
        String enc = null;
        InputStreamReader is = null;
        try {
            enc = this.getCharset(ds.getContentType());
            is = new InputStreamReader(ds.getInputStream(), enc);
        }
        catch (IllegalArgumentException iex) {
            throw new UnsupportedEncodingException(enc);
        }
        try {
            int pos;
            char[] buf;
            int count;
            int size;
            char[] tbuf = null;
            for (pos = 0, buf = new char[1024]; (count = is.read(buf, pos, buf.length - pos)) != -1; buf = tbuf) {
                pos += count;
                if (pos >= buf.length) {
                    size = buf.length;
                    if (size < 262144) {
                        size += size;
                    }
                    else {
                        size += 262144;
                    }
                    tbuf = new char[size];
                    System.arraycopy(buf, 0, tbuf, 0, pos);
                }
            }
            return new String(buf, 0, pos);
        }
        finally {
            try {
                is.close();
            }
            catch (IOException ex) {}
        }
    }
    
    public void writeTo(final Object obj, final String type, final OutputStream os) throws IOException {
        if (!(obj instanceof String)) {
            throw new IOException("\"" + this.getDF().getMimeType() + "\" DataContentHandler requires String object, " + "was given object of type " + obj.getClass().toString());
        }
        String enc = null;
        OutputStreamWriter osw = null;
        try {
            enc = this.getCharset(type);
            osw = new OutputStreamWriter(new NoCloseOutputStream(os), enc);
        }
        catch (IllegalArgumentException iex) {
            throw new UnsupportedEncodingException(enc);
        }
        final String s = (String)obj;
        osw.write(s, 0, s.length());
        osw.close();
    }
    
    private String getCharset(final String type) {
        try {
            final ContentType ct = new ContentType(type);
            String charset = ct.getParameter("charset");
            if (charset == null) {
                charset = "us-ascii";
            }
            return MimeUtility.javaCharset(charset);
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    static /* synthetic */ Class class$(final String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x) {
            throw new NoClassDefFoundError().initCause(x);
        }
    }
    
    static {
        text_plain.myDF = new ActivationDataFlavor((text_plain.class$java$lang$String == null) ? (text_plain.class$java$lang$String = class$("java.lang.String")) : text_plain.class$java$lang$String, "text/plain", "Text String");
    }
    
    private static class NoCloseOutputStream extends FilterOutputStream
    {
        public NoCloseOutputStream(final OutputStream os) {
            super(os);
        }
        
        public void close() {
        }
    }
}
