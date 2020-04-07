// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.shared.util;

import java.io.OutputStream;
import java.io.InputStream;

public final class StreamUtilities
{
    public static void closeInputStreamIgnoreExceptions(final InputStream aInputStream) {
        if (aInputStream != null) {
            try {
                aInputStream.close();
            }
            catch (Exception ex) {}
        }
    }
    
    public static void closeOutputStreamIgnoreExceptions(final OutputStream aOutputStream) {
        if (aOutputStream != null) {
            try {
                aOutputStream.close();
            }
            catch (Exception ex) {}
        }
    }
}
