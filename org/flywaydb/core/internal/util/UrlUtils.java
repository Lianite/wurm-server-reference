// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util;

import java.io.UnsupportedEncodingException;
import java.io.File;
import java.net.URLDecoder;
import java.net.URL;

public class UrlUtils
{
    public static String toFilePath(final URL url) {
        try {
            final String filePath = new File(URLDecoder.decode(url.getPath().replace("+", "%2b"), "UTF-8")).getAbsolutePath();
            if (filePath.endsWith("/")) {
                return filePath.substring(0, filePath.length() - 1);
            }
            return filePath;
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Can never happen", e);
        }
    }
}
