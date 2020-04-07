// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util.scanner.classpath;

import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.TreeSet;
import java.net.URLConnection;
import java.net.URISyntaxException;
import java.net.JarURLConnection;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.Set;
import java.net.URL;

public class JarFileClassPathLocationScanner implements ClassPathLocationScanner
{
    @Override
    public Set<String> findResourceNames(final String location, final URL locationUrl) throws IOException {
        final JarFile jarFile = this.getJarFromUrl(locationUrl);
        try {
            final String prefix = jarFile.getName().toLowerCase().endsWith(".war") ? "WEB-INF/classes/" : "";
            return this.findResourceNamesFromJarFile(jarFile, prefix, location);
        }
        finally {
            jarFile.close();
        }
    }
    
    private JarFile getJarFromUrl(final URL locationUrl) throws IOException {
        final URLConnection con = locationUrl.openConnection();
        if (con instanceof JarURLConnection) {
            final JarURLConnection jarCon = (JarURLConnection)con;
            jarCon.setUseCaches(false);
            return jarCon.getJarFile();
        }
        final String urlFile = locationUrl.getFile();
        final int separatorIndex = urlFile.indexOf("!/");
        if (separatorIndex != -1) {
            final String jarFileUrl = urlFile.substring(0, separatorIndex);
            if (jarFileUrl.startsWith("file:")) {
                try {
                    return new JarFile(new URL(jarFileUrl).toURI().getSchemeSpecificPart());
                }
                catch (URISyntaxException ex) {
                    return new JarFile(jarFileUrl.substring("file:".length()));
                }
            }
            return new JarFile(jarFileUrl);
        }
        return new JarFile(urlFile);
    }
    
    private Set<String> findResourceNamesFromJarFile(final JarFile jarFile, final String prefix, final String location) throws IOException {
        final String toScan = prefix + location + (location.endsWith("/") ? "" : "/");
        final Set<String> resourceNames = new TreeSet<String>();
        final Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            final String entryName = entries.nextElement().getName();
            if (entryName.startsWith(toScan)) {
                resourceNames.add(entryName.substring(prefix.length()));
            }
        }
        return resourceNames;
    }
}
