// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.shared.util;

import java.net.HttpURLConnection;
import java.io.IOException;
import java.io.Closeable;

public final class IoUtilities
{
    public static void closeClosable(final Closeable closableObject) {
        if (closableObject != null) {
            try {
                closableObject.close();
            }
            catch (IOException ex) {}
        }
    }
    
    public static void closeHttpURLConnection(final HttpURLConnection httpURLConnection) {
        if (httpURLConnection != null) {
            try {
                httpURLConnection.disconnect();
            }
            catch (Exception ex) {}
        }
    }
}
