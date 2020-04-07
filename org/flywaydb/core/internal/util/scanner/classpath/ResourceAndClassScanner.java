// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util.scanner.classpath;

import org.flywaydb.core.internal.util.scanner.Resource;
import org.flywaydb.core.internal.util.Location;

public interface ResourceAndClassScanner
{
    Resource[] scanForResources(final Location p0, final String p1, final String p2) throws Exception;
    
    Class<?>[] scanForClasses(final Location p0, final Class<?> p1) throws Exception;
}
