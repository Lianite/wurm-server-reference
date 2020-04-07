// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util.scanner.classpath;

import java.io.IOException;
import java.net.URL;

public class DefaultUrlResolver implements UrlResolver
{
    @Override
    public URL toStandardJavaUrl(final URL url) throws IOException {
        return url;
    }
}
