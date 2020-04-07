// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util.scanner.classpath;

import org.flywaydb.core.internal.util.logging.LogFactory;
import java.util.Collection;
import java.io.IOException;
import java.util.TreeSet;
import java.io.File;
import org.flywaydb.core.internal.util.UrlUtils;
import java.util.Set;
import java.net.URL;
import org.flywaydb.core.internal.util.logging.Log;

public class FileSystemClassPathLocationScanner implements ClassPathLocationScanner
{
    private static final Log LOG;
    
    @Override
    public Set<String> findResourceNames(final String location, final URL locationUrl) throws IOException {
        final String filePath = UrlUtils.toFilePath(locationUrl);
        final File folder = new File(filePath);
        if (!folder.isDirectory()) {
            FileSystemClassPathLocationScanner.LOG.debug("Skipping path as it is not a directory: " + filePath);
            return new TreeSet<String>();
        }
        String classPathRootOnDisk = filePath.substring(0, filePath.length() - location.length());
        if (!classPathRootOnDisk.endsWith(File.separator)) {
            classPathRootOnDisk += File.separator;
        }
        FileSystemClassPathLocationScanner.LOG.debug("Scanning starting at classpath root in filesystem: " + classPathRootOnDisk);
        return this.findResourceNamesFromFileSystem(classPathRootOnDisk, location, folder);
    }
    
    Set<String> findResourceNamesFromFileSystem(final String classPathRootOnDisk, final String scanRootLocation, final File folder) throws IOException {
        FileSystemClassPathLocationScanner.LOG.debug("Scanning for resources in path: " + folder.getPath() + " (" + scanRootLocation + ")");
        final Set<String> resourceNames = new TreeSet<String>();
        final File[] listFiles;
        final File[] files = listFiles = folder.listFiles();
        for (final File file : listFiles) {
            if (file.canRead()) {
                if (file.isDirectory()) {
                    resourceNames.addAll(this.findResourceNamesFromFileSystem(classPathRootOnDisk, scanRootLocation, file));
                }
                else {
                    resourceNames.add(this.toResourceNameOnClasspath(classPathRootOnDisk, file));
                }
            }
        }
        return resourceNames;
    }
    
    private String toResourceNameOnClasspath(final String classPathRootOnDisk, final File file) throws IOException {
        final String fileName = file.getAbsolutePath().replace("\\", "/");
        return fileName.substring(classPathRootOnDisk.length());
    }
    
    static {
        LOG = LogFactory.getLog(FileSystemClassPathLocationScanner.class);
    }
}
