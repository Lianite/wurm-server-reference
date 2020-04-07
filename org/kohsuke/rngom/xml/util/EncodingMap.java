// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.xml.util;

import java.io.UnsupportedEncodingException;

public abstract class EncodingMap
{
    private static final String[] aliases;
    
    public static String getJavaName(final String enc) {
        try {
            "x".getBytes(enc);
        }
        catch (UnsupportedEncodingException e) {
            for (int i = 0; i < EncodingMap.aliases.length; i += 2) {
                if (enc.equalsIgnoreCase(EncodingMap.aliases[i])) {
                    try {
                        "x".getBytes(EncodingMap.aliases[i + 1]);
                        return EncodingMap.aliases[i + 1];
                    }
                    catch (UnsupportedEncodingException ex) {}
                }
            }
        }
        return enc;
    }
    
    public static void main(final String[] args) {
        System.err.println(getJavaName(args[0]));
    }
    
    static {
        aliases = new String[] { "UTF-8", "UTF8", "UTF-16", "Unicode", "UTF-16BE", "UnicodeBigUnmarked", "UTF-16LE", "UnicodeLittleUnmarked", "US-ASCII", "ASCII", "TIS-620", "TIS620" };
    }
}
