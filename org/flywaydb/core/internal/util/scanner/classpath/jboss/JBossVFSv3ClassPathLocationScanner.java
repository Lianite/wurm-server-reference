// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util.scanner.classpath.jboss;

import org.flywaydb.core.internal.util.logging.LogFactory;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.jboss.vfs.VirtualFile;
import org.jboss.vfs.VirtualFileFilter;
import org.jboss.vfs.VFS;
import java.util.TreeSet;
import org.flywaydb.core.internal.util.UrlUtils;
import java.util.Set;
import java.net.URL;
import org.flywaydb.core.internal.util.logging.Log;
import org.flywaydb.core.internal.util.scanner.classpath.ClassPathLocationScanner;

public class JBossVFSv3ClassPathLocationScanner implements ClassPathLocationScanner
{
    private static final Log LOG;
    
    @Override
    public Set<String> findResourceNames(final String location, final URL locationUrl) throws IOException {
        final String filePath = UrlUtils.toFilePath(locationUrl);
        String classPathRootOnDisk = filePath.substring(0, filePath.length() - location.length());
        if (!classPathRootOnDisk.endsWith("/")) {
            classPathRootOnDisk += "/";
        }
        JBossVFSv3ClassPathLocationScanner.LOG.debug("Scanning starting at classpath root on JBoss VFS: " + classPathRootOnDisk);
        final Set<String> resourceNames = new TreeSet<String>();
        final List<VirtualFile> files = (List<VirtualFile>)VFS.getChild(filePath).getChildrenRecursively((VirtualFileFilter)new VirtualFileFilter() {
            public boolean accepts(final VirtualFile file) {
                return file.isFile();
            }
        });
        for (final VirtualFile file : files) {
            resourceNames.add(file.getPathName().substring(classPathRootOnDisk.length()));
        }
        return resourceNames;
    }
    
    static {
        LOG = LogFactory.getLog(JBossVFSv3ClassPathLocationScanner.class);
    }
}
