// 
// Decompiled by Procyon v0.5.30
// 

package winterwell.jtwitter.guts;

import java.io.ByteArrayOutputStream;

public final class Base64Encoder
{
    static final char[] charTab;
    
    static {
        charTab = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
    }
    
    public static String encode(final String string) {
        return encode(string.getBytes()).toString();
    }
    
    public static String encode(final byte[] data) {
        return encode(data, 0, data.length, null).toString();
    }
    
    public static StringBuffer encode(final byte[] data, final int start, final int len, StringBuffer buf) {
        if (buf == null) {
            buf = new StringBuffer(data.length * 3 / 2);
        }
        final int end = len - 3;
        int i = start;
        int n = 0;
        while (i <= end) {
            final int d = (data[i] & 0xFF) << 16 | (data[i + 1] & 0xFF) << 8 | (data[i + 2] & 0xFF);
            buf.append(Base64Encoder.charTab[d >> 18 & 0x3F]);
            buf.append(Base64Encoder.charTab[d >> 12 & 0x3F]);
            buf.append(Base64Encoder.charTab[d >> 6 & 0x3F]);
            buf.append(Base64Encoder.charTab[d & 0x3F]);
            i += 3;
            if (n++ >= 14) {
                n = 0;
                buf.append("\r\n");
            }
        }
        if (i == start + len - 2) {
            final int d = (data[i] & 0xFF) << 16 | (data[i + 1] & 0xFF) << 8;
            buf.append(Base64Encoder.charTab[d >> 18 & 0x3F]);
            buf.append(Base64Encoder.charTab[d >> 12 & 0x3F]);
            buf.append(Base64Encoder.charTab[d >> 6 & 0x3F]);
            buf.append("=");
        }
        else if (i == start + len - 1) {
            final int d = (data[i] & 0xFF) << 16;
            buf.append(Base64Encoder.charTab[d >> 18 & 0x3F]);
            buf.append(Base64Encoder.charTab[d >> 12 & 0x3F]);
            buf.append("==");
        }
        return buf;
    }
    
    static int decode(final char c) {
        if (c >= 'A' && c <= 'Z') {
            return c - 'A';
        }
        if (c >= 'a' && c <= 'z') {
            return c - 'a' + '\u001a';
        }
        if (c >= '0' && c <= '9') {
            return c - '0' + '\u001a' + '\u001a';
        }
        switch (c) {
            case '+': {
                return 62;
            }
            case '/': {
                return 63;
            }
            case '=': {
                return 0;
            }
            default: {
                throw new RuntimeException(new StringBuffer("unexpected code: ").append(c).toString());
            }
        }
    }
    
    public static byte[] decode(final String s) {
        int i = 0;
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final int len = s.length();
        while (true) {
            if (i >= len || s.charAt(i) > ' ') {
                if (i == len) {
                    break;
                }
                final int tri = (decode(s.charAt(i)) << 18) + (decode(s.charAt(i + 1)) << 12) + (decode(s.charAt(i + 2)) << 6) + decode(s.charAt(i + 3));
                bos.write(tri >> 16 & 0xFF);
                if (s.charAt(i + 2) == '=') {
                    break;
                }
                bos.write(tri >> 8 & 0xFF);
                if (s.charAt(i + 3) == '=') {
                    break;
                }
                bos.write(tri & 0xFF);
                i += 4;
            }
            else {
                ++i;
            }
        }
        return bos.toByteArray();
    }
}
