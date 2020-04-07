// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util.scanner;

import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.internal.util.Location;
import org.flywaydb.core.internal.util.scanner.classpath.ClassPathScanner;
import org.flywaydb.core.internal.util.scanner.classpath.android.AndroidScanner;
import org.flywaydb.core.internal.util.FeatureDetector;
import org.flywaydb.core.internal.util.scanner.filesystem.FileSystemScanner;
import org.flywaydb.core.internal.util.scanner.classpath.ResourceAndClassScanner;

public class Scanner
{
    private final ResourceAndClassScanner resourceAndClassScanner;
    private final ClassLoader classLoader;
    private final FileSystemScanner fileSystemScanner;
    
    public Scanner(final ClassLoader classLoader) {
        this.fileSystemScanner = new FileSystemScanner();
        this.classLoader = classLoader;
        if (new FeatureDetector(classLoader).isAndroidAvailable()) {
            this.resourceAndClassScanner = new AndroidScanner(classLoader);
        }
        else {
            this.resourceAndClassScanner = new ClassPathScanner(classLoader);
        }
    }
    
    public Resource[] scanForResources(final Location location, final String prefix, final String suffix) {
        try {
            if (location.isFileSystem()) {
                return this.fileSystemScanner.scanForResources(location, prefix, suffix);
            }
            return this.resourceAndClassScanner.scanForResources(location, prefix, suffix);
        }
        catch (Exception e) {
            throw new FlywayException("Unable to scan for SQL migrations in location: " + location, e);
        }
    }
    
    public Class<?>[] scanForClasses(final Location location, final Class<?> implementedInterface) throws Exception {
        return this.resourceAndClassScanner.scanForClasses(location, implementedInterface);
    }
    
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }
}
