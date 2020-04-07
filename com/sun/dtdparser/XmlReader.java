// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.dtdparser;

import java.io.CharConversionException;
import java.io.ByteArrayInputStream;
import java.io.PushbackInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.io.Reader;

final class XmlReader extends Reader
{
    private static final int MAXPUSHBACK = 512;
    private Reader in;
    private String assignedEncoding;
    private boolean closed;
    private static final Hashtable charsets;
    
    public static Reader createReader(final InputStream in) throws IOException {
        return new XmlReader(in);
    }
    
    public static Reader createReader(final InputStream in, final String encoding) throws IOException {
        if (encoding == null) {
            return new XmlReader(in);
        }
        if ("UTF-8".equalsIgnoreCase(encoding) || "UTF8".equalsIgnoreCase(encoding)) {
            return new Utf8Reader(in);
        }
        if ("US-ASCII".equalsIgnoreCase(encoding) || "ASCII".equalsIgnoreCase(encoding)) {
            return new AsciiReader(in);
        }
        if ("ISO-8859-1".equalsIgnoreCase(encoding)) {
            return new Iso8859_1Reader(in);
        }
        return new InputStreamReader(in, std2java(encoding));
    }
    
    private static String std2java(final String encoding) {
        String temp = encoding.toUpperCase();
        temp = XmlReader.charsets.get(temp);
        return (temp != null) ? temp : encoding;
    }
    
    public String getEncoding() {
        return this.assignedEncoding;
    }
    
    private XmlReader(final InputStream stream) throws IOException {
        super(stream);
        PushbackInputStream pb;
        if (stream instanceof PushbackInputStream) {
            pb = (PushbackInputStream)stream;
        }
        else {
            pb = new PushbackInputStream(stream, 512);
        }
        final byte[] buf = new byte[4];
        final int len = pb.read(buf);
        if (len > 0) {
            pb.unread(buf, 0, len);
        }
        Label_0326: {
            if (len == 4) {
                switch (buf[0] & 0xFF) {
                    case 0: {
                        if (buf[1] == 60 && buf[2] == 0 && buf[3] == 63) {
                            this.setEncoding(pb, "UnicodeBig");
                            return;
                        }
                        break;
                    }
                    case 60: {
                        switch (buf[1] & 0xFF) {
                            default: {
                                break Label_0326;
                            }
                            case 0: {
                                if (buf[2] == 63 && buf[3] == 0) {
                                    this.setEncoding(pb, "UnicodeLittle");
                                    return;
                                }
                                break Label_0326;
                            }
                            case 63: {
                                if (buf[2] != 120) {
                                    break Label_0326;
                                }
                                if (buf[3] != 109) {
                                    break Label_0326;
                                }
                                this.useEncodingDecl(pb, "UTF8");
                                return;
                            }
                        }
                        break;
                    }
                    case 76: {
                        if (buf[1] == 111 && (0xFF & buf[2]) == 0xA7 && (0xFF & buf[3]) == 0x94) {
                            this.useEncodingDecl(pb, "CP037");
                            return;
                        }
                        break;
                    }
                    case 254: {
                        if ((buf[1] & 0xFF) != 0xFF) {
                            break;
                        }
                        this.setEncoding(pb, "UTF-16");
                        return;
                    }
                    case 255: {
                        if ((buf[1] & 0xFF) != 0xFE) {
                            break;
                        }
                        this.setEncoding(pb, "UTF-16");
                        return;
                    }
                }
            }
        }
        this.setEncoding(pb, "UTF-8");
    }
    
    private void useEncodingDecl(final PushbackInputStream pb, final String encoding) throws IOException {
        final byte[] buffer = new byte[512];
        final int len = pb.read(buffer, 0, buffer.length);
        pb.unread(buffer, 0, len);
        final Reader r = new InputStreamReader(new ByteArrayInputStream(buffer, 4, len), encoding);
        int c;
        if ((c = r.read()) != 108) {
            this.setEncoding(pb, "UTF-8");
            return;
        }
        final StringBuffer buf = new StringBuffer();
        StringBuffer keyBuf = null;
        String key = null;
        boolean sawEq = false;
        char quoteChar = '\0';
        boolean sawQuestion = false;
    Label_0513:
        for (int i = 0; i < 507; ++i) {
            if ((c = r.read()) == -1) {
                break;
            }
            if (c != 32 && c != 9 && c != 10) {
                if (c != 13) {
                    if (i == 0) {
                        break;
                    }
                    if (c == 63) {
                        sawQuestion = true;
                    }
                    else if (sawQuestion) {
                        if (c == 62) {
                            break;
                        }
                        sawQuestion = false;
                    }
                    if (key == null || !sawEq) {
                        if (keyBuf == null) {
                            if (!Character.isWhitespace((char)c)) {
                                keyBuf = buf;
                                buf.setLength(0);
                                buf.append((char)c);
                                sawEq = false;
                            }
                        }
                        else if (Character.isWhitespace((char)c)) {
                            key = keyBuf.toString();
                        }
                        else if (c == 61) {
                            if (key == null) {
                                key = keyBuf.toString();
                            }
                            sawEq = true;
                            keyBuf = null;
                            quoteChar = '\0';
                        }
                        else {
                            keyBuf.append((char)c);
                        }
                    }
                    else if (!Character.isWhitespace((char)c)) {
                        if (c == 34 || c == 39) {
                            if (quoteChar == '\0') {
                                quoteChar = (char)c;
                                buf.setLength(0);
                                continue;
                            }
                            if (c == quoteChar) {
                                if ("encoding".equals(key)) {
                                    this.assignedEncoding = buf.toString();
                                    for (i = 0; i < this.assignedEncoding.length(); ++i) {
                                        c = this.assignedEncoding.charAt(i);
                                        if (c < 65 || c > 90) {
                                            if (c < 97 || c > 122) {
                                                if (i == 0) {
                                                    break Label_0513;
                                                }
                                                if (i <= 0 || (c != 45 && (c < 48 || c > 57) && c != 46 && c != 95)) {
                                                    break Label_0513;
                                                }
                                            }
                                        }
                                    }
                                    this.setEncoding(pb, this.assignedEncoding);
                                    return;
                                }
                                key = null;
                                continue;
                            }
                        }
                        buf.append((char)c);
                    }
                }
            }
        }
        this.setEncoding(pb, "UTF-8");
    }
    
    private void setEncoding(final InputStream stream, final String encoding) throws IOException {
        this.assignedEncoding = encoding;
        this.in = createReader(stream, encoding);
    }
    
    public int read(final char[] buf, final int off, final int len) throws IOException {
        if (this.closed) {
            return -1;
        }
        final int val = this.in.read(buf, off, len);
        if (val == -1) {
            this.close();
        }
        return val;
    }
    
    public int read() throws IOException {
        if (this.closed) {
            throw new IOException("closed");
        }
        final int val = this.in.read();
        if (val == -1) {
            this.close();
        }
        return val;
    }
    
    public boolean markSupported() {
        return this.in != null && this.in.markSupported();
    }
    
    public void mark(final int value) throws IOException {
        if (this.in != null) {
            this.in.mark(value);
        }
    }
    
    public void reset() throws IOException {
        if (this.in != null) {
            this.in.reset();
        }
    }
    
    public long skip(final long value) throws IOException {
        return (this.in == null) ? 0L : this.in.skip(value);
    }
    
    public boolean ready() throws IOException {
        return this.in != null && this.in.ready();
    }
    
    public void close() throws IOException {
        if (this.closed) {
            return;
        }
        this.in.close();
        this.in = null;
        this.closed = true;
    }
    
    static {
        (charsets = new Hashtable(31)).put("UTF-16", "Unicode");
        XmlReader.charsets.put("ISO-10646-UCS-2", "Unicode");
        XmlReader.charsets.put("EBCDIC-CP-US", "cp037");
        XmlReader.charsets.put("EBCDIC-CP-CA", "cp037");
        XmlReader.charsets.put("EBCDIC-CP-NL", "cp037");
        XmlReader.charsets.put("EBCDIC-CP-WT", "cp037");
        XmlReader.charsets.put("EBCDIC-CP-DK", "cp277");
        XmlReader.charsets.put("EBCDIC-CP-NO", "cp277");
        XmlReader.charsets.put("EBCDIC-CP-FI", "cp278");
        XmlReader.charsets.put("EBCDIC-CP-SE", "cp278");
        XmlReader.charsets.put("EBCDIC-CP-IT", "cp280");
        XmlReader.charsets.put("EBCDIC-CP-ES", "cp284");
        XmlReader.charsets.put("EBCDIC-CP-GB", "cp285");
        XmlReader.charsets.put("EBCDIC-CP-FR", "cp297");
        XmlReader.charsets.put("EBCDIC-CP-AR1", "cp420");
        XmlReader.charsets.put("EBCDIC-CP-HE", "cp424");
        XmlReader.charsets.put("EBCDIC-CP-BE", "cp500");
        XmlReader.charsets.put("EBCDIC-CP-CH", "cp500");
        XmlReader.charsets.put("EBCDIC-CP-ROECE", "cp870");
        XmlReader.charsets.put("EBCDIC-CP-YU", "cp870");
        XmlReader.charsets.put("EBCDIC-CP-IS", "cp871");
        XmlReader.charsets.put("EBCDIC-CP-AR2", "cp918");
    }
    
    abstract static class BaseReader extends Reader
    {
        protected InputStream instream;
        protected byte[] buffer;
        protected int start;
        protected int finish;
        
        BaseReader(final InputStream stream) {
            super(stream);
            this.instream = stream;
            this.buffer = new byte[8192];
        }
        
        public boolean ready() throws IOException {
            return this.instream == null || this.finish - this.start > 0 || this.instream.available() != 0;
        }
        
        public void close() throws IOException {
            if (this.instream != null) {
                this.instream.close();
                final boolean b = false;
                this.finish = (b ? 1 : 0);
                this.start = (b ? 1 : 0);
                this.buffer = null;
                this.instream = null;
            }
        }
    }
    
    static final class Utf8Reader extends BaseReader
    {
        private char nextChar;
        
        Utf8Reader(final InputStream stream) {
            super(stream);
        }
        
        public int read(final char[] buf, final int offset, final int len) throws IOException {
            int i = 0;
            int c = 0;
            if (len <= 0) {
                return 0;
            }
            if (this.nextChar != '\0') {
                buf[offset + i++] = this.nextChar;
                this.nextChar = '\0';
            }
            while (i < len) {
                if (this.finish <= this.start) {
                    if (this.instream == null) {
                        c = -1;
                        break;
                    }
                    this.start = 0;
                    this.finish = this.instream.read(this.buffer, 0, this.buffer.length);
                    if (this.finish <= 0) {
                        this.close();
                        c = -1;
                        break;
                    }
                }
                c = (this.buffer[this.start] & 0xFF);
                if ((c & 0x80) == 0x0) {
                    ++this.start;
                    buf[offset + i++] = (char)c;
                }
                else {
                    int off = this.start;
                    try {
                        if ((this.buffer[off] & 0xE0) == 0xC0) {
                            c = (this.buffer[off++] & 0x1F) << 6;
                            c += (this.buffer[off++] & 0x3F);
                        }
                        else if ((this.buffer[off] & 0xF0) == 0xE0) {
                            c = (this.buffer[off++] & 0xF) << 12;
                            c += (this.buffer[off++] & 0x3F) << 6;
                            c += (this.buffer[off++] & 0x3F);
                        }
                        else {
                            if ((this.buffer[off] & 0xF8) != 0xF0) {
                                throw new CharConversionException("Unconvertible UTF-8 character beginning with 0x" + Integer.toHexString(this.buffer[this.start] & 0xFF));
                            }
                            c = (this.buffer[off++] & 0x7) << 18;
                            c += (this.buffer[off++] & 0x3F) << 12;
                            c += (this.buffer[off++] & 0x3F) << 6;
                            c += (this.buffer[off++] & 0x3F);
                            if (c > 1114111) {
                                throw new CharConversionException("UTF-8 encoding of character 0x00" + Integer.toHexString(c) + " can't be converted to Unicode.");
                            }
                            c -= 65536;
                            this.nextChar = (char)(56320 + (c & 0x3FF));
                            c = 55296 + (c >> 10);
                        }
                    }
                    catch (ArrayIndexOutOfBoundsException e) {
                        c = 0;
                    }
                    if (off > this.finish) {
                        System.arraycopy(this.buffer, this.start, this.buffer, 0, this.finish - this.start);
                        this.finish -= this.start;
                        this.start = 0;
                        off = this.instream.read(this.buffer, this.finish, this.buffer.length - this.finish);
                        if (off < 0) {
                            this.close();
                            throw new CharConversionException("Partial UTF-8 char");
                        }
                        this.finish += off;
                    }
                    else {
                        ++this.start;
                        while (this.start < off) {
                            if ((this.buffer[this.start] & 0xC0) != 0x80) {
                                this.close();
                                throw new CharConversionException("Malformed UTF-8 char -- is an XML encoding declaration missing?");
                            }
                            ++this.start;
                        }
                        buf[offset + i++] = (char)c;
                        if (this.nextChar == '\0' || i >= len) {
                            continue;
                        }
                        buf[offset + i++] = this.nextChar;
                        this.nextChar = '\0';
                    }
                }
            }
            if (i > 0) {
                return i;
            }
            return (c == -1) ? -1 : 0;
        }
    }
    
    static final class AsciiReader extends BaseReader
    {
        AsciiReader(final InputStream in) {
            super(in);
        }
        
        public int read(final char[] buf, final int offset, final int len) throws IOException {
            if (this.instream == null) {
                return -1;
            }
            int i;
            for (i = 0; i < len; ++i) {
                if (this.start >= this.finish) {
                    this.start = 0;
                    this.finish = this.instream.read(this.buffer, 0, this.buffer.length);
                    if (this.finish <= 0) {
                        if (this.finish <= 0) {
                            this.close();
                            break;
                        }
                        break;
                    }
                }
                final int c = this.buffer[this.start++];
                if ((c & 0x80) != 0x0) {
                    throw new CharConversionException("Illegal ASCII character, 0x" + Integer.toHexString(c & 0xFF));
                }
                buf[offset + i] = (char)c;
            }
            if (i == 0 && this.finish <= 0) {
                return -1;
            }
            return i;
        }
    }
    
    static final class Iso8859_1Reader extends BaseReader
    {
        Iso8859_1Reader(final InputStream in) {
            super(in);
        }
        
        public int read(final char[] buf, final int offset, final int len) throws IOException {
            if (this.instream == null) {
                return -1;
            }
            int i;
            for (i = 0; i < len; ++i) {
                if (this.start >= this.finish) {
                    this.start = 0;
                    this.finish = this.instream.read(this.buffer, 0, this.buffer.length);
                    if (this.finish <= 0) {
                        if (this.finish <= 0) {
                            this.close();
                            break;
                        }
                        break;
                    }
                }
                buf[offset + i] = (char)(0xFF & this.buffer[this.start++]);
            }
            if (i == 0 && this.finish <= 0) {
                return -1;
            }
            return i;
        }
    }
}
