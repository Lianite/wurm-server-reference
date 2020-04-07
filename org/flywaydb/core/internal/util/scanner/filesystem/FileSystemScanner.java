// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util.scanner.filesystem;

import org.flywaydb.core.internal.util.logging.LogFactory;
import java.util.Collection;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.io.File;
import org.flywaydb.core.internal.util.scanner.Resource;
import org.flywaydb.core.internal.util.Location;
import org.flywaydb.core.internal.util.logging.Log;

public class FileSystemScanner
{
    private static final Log LOG;
    
    public Resource[] scanForResources(final Location location, final String prefix, final String suffix) throws IOException {
        final String path = location.getPath();
        FileSystemScanner.LOG.debug("Scanning for filesystem resources at '" + path + "' (Prefix: '" + prefix + "', Suffix: '" + suffix + "')");
        final File dir = new File(path);
        if (!dir.isDirectory() || !dir.canRead()) {
            FileSystemScanner.LOG.warn("Unable to resolve location filesystem:" + path);
            return new Resource[0];
        }
        final Set<Resource> resources = new TreeSet<Resource>();
        final Set<String> resourceNames = this.findResourceNames(path, prefix, suffix);
        for (final String resourceName : resourceNames) {
            resources.add(new FileSystemResource(resourceName));
            FileSystemScanner.LOG.debug("Found filesystem resource: " + resourceName);
        }
        return resources.toArray(new Resource[resources.size()]);
    }
    
    private Set<String> findResourceNames(final String path, final String prefix, final String suffix) throws IOException {
        final Set<String> resourceNames = this.findResourceNamesFromFileSystem(path, new File(path));
        return this.filterResourceNames(resourceNames, prefix, suffix);
    }
    
    private Set<String> findResourceNamesFromFileSystem(final String scanRootLocation, final File folder) throws IOException {
        FileSystemScanner.LOG.debug("Scanning for resources in path: " + folder.getPath() + " (" + scanRootLocation + ")");
        final Set<String> resourceNames = new TreeSet<String>();
        final File[] listFiles;
        final File[] files = listFiles = folder.listFiles();
        for (final File file : listFiles) {
            if (file.canRead()) {
                if (file.isDirectory()) {
                    resourceNames.addAll(this.findResourceNamesFromFileSystem(scanRootLocation, file));
                }
                else {
                    resourceNames.add(file.getPath());
                }
            }
        }
        return resourceNames;
    }
    
    private Set<String> filterResourceNames(final Set<String> resourceNames, final String prefix, final String suffix) {
        final Set<String> filteredResourceNames = new TreeSet<String>();
        for (final String resourceName : resourceNames) {
            final String fileName = resourceName.substring(resourceName.lastIndexOf(File.separator) + 1);
            if (fileName.startsWith(prefix) && fileName.endsWith(suffix) && fileName.length() > (prefix + suffix).length()) {
                filteredResourceNames.add(resourceName);
            }
            else {
                FileSystemScanner.LOG.debug("Filtering out resource: " + resourceName + " (filename: " + fileName + ")");
            }
        }
        return filteredResourceNames;
    }
    
    static {
        LOG = LogFactory.getLog(FileSystemScanner.class);
    }
}
