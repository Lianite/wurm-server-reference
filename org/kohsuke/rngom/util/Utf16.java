// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.util;

public abstract class Utf16
{
    public static boolean isSurrogate(final char c) {
        return (c & '\uf800') == '\ud800';
    }
    
    public static boolean isSurrogate1(final char c) {
        return (c & '\ufc00') == '\ud800';
    }
    
    public static boolean isSurrogate2(final char c) {
        return (c & '\ufc00') == '\udc00';
    }
    
    public static int scalarValue(final char c1, final char c2) {
        return ((c1 & '\u03ff') << 10 | (c2 & '\u03ff')) + 65536;
    }
    
    public static char surrogate1(final int c) {
        return (char)(c - 65536 >> 10 | 0xD800);
    }
    
    public static char surrogate2(final int c) {
        return (char)((c - 65536 & 0x3FF) | 0xDC00);
    }
}
