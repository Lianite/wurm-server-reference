// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util.scanner.classpath;

import java.io.IOException;
import java.util.Set;
import java.net.URL;

public interface ClassPathLocationScanner
{
    Set<String> findResourceNames(final String p0, final URL p1) throws IOException;
}
