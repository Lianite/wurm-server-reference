// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util.scanner.classpath;

import java.util.regex.Matcher;
import java.io.IOException;
import java.util.Enumeration;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import java.util.TreeSet;
import java.util.Set;
import java.net.URL;
import java.util.regex.Pattern;

public class OsgiClassPathLocationScanner implements ClassPathLocationScanner
{
    private static final Pattern bundleIdPattern;
    
    @Override
    public Set<String> findResourceNames(final String location, final URL locationUrl) throws IOException {
        final Set<String> resourceNames = new TreeSet<String>();
        final Bundle bundle = this.getTargetBundleOrCurrent(FrameworkUtil.getBundle((Class)this.getClass()), locationUrl);
        final Enumeration<URL> entries = (Enumeration<URL>)bundle.findEntries(locationUrl.getPath(), "*", true);
        if (entries != null) {
            while (entries.hasMoreElements()) {
                final URL entry = entries.nextElement();
                final String resourceName = this.getPathWithoutLeadingSlash(entry);
                resourceNames.add(resourceName);
            }
        }
        return resourceNames;
    }
    
    private Bundle getTargetBundleOrCurrent(final Bundle currentBundle, final URL locationUrl) {
        try {
            final Bundle targetBundle = currentBundle.getBundleContext().getBundle(this.getBundleId(locationUrl.getHost()));
            return (targetBundle != null) ? targetBundle : currentBundle;
        }
        catch (Exception e) {
            return currentBundle;
        }
    }
    
    private long getBundleId(final String host) {
        final Matcher matcher = OsgiClassPathLocationScanner.bundleIdPattern.matcher(host);
        if (matcher.find()) {
            return (long)(Object)Double.valueOf(matcher.group());
        }
        throw new IllegalArgumentException("There's no bundleId in passed URL");
    }
    
    private String getPathWithoutLeadingSlash(final URL entry) {
        final String path = entry.getPath();
        return path.startsWith("/") ? path.substring(1) : path;
    }
    
    static {
        bundleIdPattern = Pattern.compile("^\\d+");
    }
}
