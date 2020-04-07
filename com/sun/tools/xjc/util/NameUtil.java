// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.util;

import java.util.ArrayList;

public class NameUtil
{
    protected boolean isPunct(final char c) {
        return c == '-' || c == '.' || c == ':' || c == '_' || c == '·' || c == '\u0387' || c == '\u06dd' || c == '\u06de';
    }
    
    protected boolean isDigit(final char c) {
        return (c >= '0' && c <= '9') || Character.isDigit(c);
    }
    
    protected boolean isUpper(final char c) {
        return (c >= 'A' && c <= 'Z') || Character.isUpperCase(c);
    }
    
    protected boolean isLower(final char c) {
        return (c >= 'a' && c <= 'z') || Character.isLowerCase(c);
    }
    
    protected boolean isLetter(final char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || Character.isLetter(c);
    }
    
    public String capitalize(final String s) {
        if (!this.isLower(s.charAt(0))) {
            return s;
        }
        final StringBuffer sb = new StringBuffer(s.length());
        sb.append(Character.toUpperCase(s.charAt(0)));
        sb.append(s.substring(1).toLowerCase());
        return sb.toString();
    }
    
    protected int nextBreak(final String s, final int start) {
        for (int n = s.length(), i = start; i < n; ++i) {
            final char c0 = s.charAt(i);
            if (i < n - 1) {
                final char c2 = s.charAt(i + 1);
                if (this.isPunct(c2)) {
                    return i + 1;
                }
                if (this.isDigit(c0) && !this.isDigit(c2)) {
                    return i + 1;
                }
                if (!this.isDigit(c0) && this.isDigit(c2)) {
                    return i + 1;
                }
                if (this.isLower(c0) && !this.isLower(c2)) {
                    return i + 1;
                }
                if (this.isLetter(c0) && !this.isLetter(c2)) {
                    return i + 1;
                }
                if (!this.isLetter(c0) && this.isLetter(c2)) {
                    return i + 1;
                }
                if (i < n - 2) {
                    final char c3 = s.charAt(i + 2);
                    if (this.isUpper(c0) && this.isUpper(c2) && this.isLower(c3)) {
                        return i + 1;
                    }
                }
            }
        }
        return -1;
    }
    
    public String[] toWordList(final String s) {
        final ArrayList ss = new ArrayList();
        int b;
        for (int n = s.length(), i = 0; i < n; i = b) {
            while (i < n && this.isPunct(s.charAt(i))) {
                ++i;
            }
            if (i >= n) {
                break;
            }
            b = this.nextBreak(s, i);
            final String w = (b == -1) ? s.substring(i) : s.substring(i, b);
            ss.add(escape(this.capitalize(w)));
            if (b == -1) {
                break;
            }
        }
        return ss.toArray(new String[0]);
    }
    
    protected String toMixedCaseName(final String[] ss, final boolean startUpper) {
        final StringBuffer sb = new StringBuffer();
        if (ss.length > 0) {
            sb.append(startUpper ? ss[0] : ss[0].toLowerCase());
            for (int i = 1; i < ss.length; ++i) {
                sb.append(ss[i]);
            }
        }
        return sb.toString();
    }
    
    protected String toMixedCaseVariableName(final String[] ss, final boolean startUpper, final boolean cdrUpper) {
        if (cdrUpper) {
            for (int i = 1; i < ss.length; ++i) {
                ss[i] = this.capitalize(ss[i]);
            }
        }
        final StringBuffer sb = new StringBuffer();
        if (ss.length > 0) {
            sb.append(startUpper ? ss[0] : ss[0].toLowerCase());
            for (int j = 1; j < ss.length; ++j) {
                sb.append(ss[j]);
            }
        }
        return sb.toString();
    }
    
    public String toConstantName(final String s) {
        return this.toConstantName(this.toWordList(s));
    }
    
    public String toConstantName(final String[] ss) {
        final StringBuffer sb = new StringBuffer();
        if (ss.length > 0) {
            sb.append(ss[0].toUpperCase());
            for (int i = 1; i < ss.length; ++i) {
                sb.append('_');
                sb.append(ss[i].toUpperCase());
            }
        }
        return sb.toString();
    }
    
    public static void escape(final StringBuffer sb, final String s, final int start) {
        for (int n = s.length(), i = start; i < n; ++i) {
            final char c = s.charAt(i);
            if (Character.isJavaIdentifierPart(c)) {
                sb.append(c);
            }
            else {
                sb.append("_");
                if (c <= '\u000f') {
                    sb.append("000");
                }
                else if (c <= '\u00ff') {
                    sb.append("00");
                }
                else if (c <= '\u0fff') {
                    sb.append("0");
                }
                sb.append(Integer.toString(c, 16));
            }
        }
    }
    
    private static String escape(final String s) {
        for (int n = s.length(), i = 0; i < n; ++i) {
            if (!Character.isJavaIdentifierPart(s.charAt(i))) {
                final StringBuffer sb = new StringBuffer(s.substring(0, i));
                escape(sb, s, i);
                return sb.toString();
            }
        }
        return s;
    }
}
