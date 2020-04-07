// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.io.UnsupportedEncodingException;

public class Uri
{
    private static String utf8;
    private static final String HEX_DIGITS = "0123456789abcdef";
    private static final String excluded = "<>\"{}|\\^`";
    
    public static boolean isValid(final String s) {
        return isValidPercent(s) && isValidFragment(s) && isValidScheme(s);
    }
    
    public static String escapeDisallowedChars(final String s) {
        StringBuffer buf = null;
        final int len = s.length();
        int done = 0;
    Label_0009:
        while (true) {
            int i = done;
            while (true) {
                while (i != len) {
                    if (isExcluded(s.charAt(i))) {
                        if (buf == null) {
                            buf = new StringBuffer();
                        }
                        if (i > done) {
                            buf.append(s.substring(done, i));
                            done = i;
                        }
                        if (i == len) {
                            return buf.toString();
                        }
                        ++i;
                        while (i < len && isExcluded(s.charAt(i))) {
                            ++i;
                        }
                        final String tem = s.substring(done, i);
                        byte[] bytes;
                        try {
                            bytes = tem.getBytes(Uri.utf8);
                        }
                        catch (UnsupportedEncodingException e) {
                            Uri.utf8 = "UTF8";
                            try {
                                bytes = tem.getBytes(Uri.utf8);
                            }
                            catch (UnsupportedEncodingException e2) {
                                return s;
                            }
                        }
                        for (int j = 0; j < bytes.length; ++j) {
                            buf.append('%');
                            buf.append("0123456789abcdef".charAt((bytes[j] & 0xFF) >> 4));
                            buf.append("0123456789abcdef".charAt(bytes[j] & 0xF));
                        }
                        done = i;
                        continue Label_0009;
                    }
                    else {
                        ++i;
                    }
                }
                if (done == 0) {
                    return s;
                }
                continue;
            }
        }
    }
    
    private static boolean isExcluded(final char c) {
        return c <= ' ' || c >= '\u007f' || "<>\"{}|\\^`".indexOf(c) >= 0;
    }
    
    private static boolean isAlpha(final char c) {
        return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z');
    }
    
    private static boolean isHexDigit(final char c) {
        return ('a' <= c && c <= 'f') || ('A' <= c && c <= 'F') || isDigit(c);
    }
    
    private static boolean isDigit(final char c) {
        return '0' <= c && c <= '9';
    }
    
    private static boolean isSchemeChar(final char c) {
        return isAlpha(c) || isDigit(c) || c == '+' || c == '-' || c == '.';
    }
    
    private static boolean isValidPercent(final String s) {
        for (int len = s.length(), i = 0; i < len; ++i) {
            if (s.charAt(i) == '%') {
                if (i + 2 >= len) {
                    return false;
                }
                if (!isHexDigit(s.charAt(i + 1)) || !isHexDigit(s.charAt(i + 2))) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private static boolean isValidFragment(final String s) {
        final int i = s.indexOf(35);
        return i < 0 || s.indexOf(35, i + 1) < 0;
    }
    
    private static boolean isValidScheme(final String s) {
        if (!isAbsolute(s)) {
            return true;
        }
        int i = s.indexOf(58);
        if (i == 0 || i + 1 == s.length() || !isAlpha(s.charAt(0))) {
            return false;
        }
        while (--i > 0) {
            if (!isSchemeChar(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    public static String resolve(final String baseUri, final String uriReference) {
        if (!isAbsolute(uriReference) && baseUri != null && isAbsolute(baseUri)) {
            try {
                return new URL(new URL(baseUri), uriReference).toString();
            }
            catch (MalformedURLException ex) {}
        }
        return uriReference;
    }
    
    public static boolean hasFragmentId(final String uri) {
        return uri.indexOf(35) >= 0;
    }
    
    public static boolean isAbsolute(final String uri) {
        int i = uri.indexOf(58);
        if (i < 0) {
            return false;
        }
        while (--i >= 0) {
            switch (uri.charAt(i)) {
                case '#':
                case '/':
                case '?': {
                    return false;
                }
                default: {
                    continue;
                }
            }
        }
        return true;
    }
    
    static {
        Uri.utf8 = "UTF-8";
    }
}
