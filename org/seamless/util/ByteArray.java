// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.util;

public class ByteArray
{
    public static byte[] toPrimitive(final Byte[] array) {
        final byte[] bytes = new byte[array.length];
        for (int i = 0; i < array.length; ++i) {
            bytes[i] = array[i];
        }
        return bytes;
    }
    
    public static Byte[] toWrapper(final byte[] array) {
        final Byte[] wrappers = new Byte[array.length];
        for (int i = 0; i < array.length; ++i) {
            wrappers[i] = array[i];
        }
        return wrappers;
    }
}
