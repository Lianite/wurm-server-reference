// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util.scanner.classpath;

import java.io.IOException;
import java.net.URL;

public interface UrlResolver
{
    URL toStandardJavaUrl(final URL p0) throws IOException;
}
