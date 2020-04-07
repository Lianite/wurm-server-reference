// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util.scanner.classpath;

import org.flywaydb.core.internal.util.logging.LogFactory;
import org.flywaydb.core.internal.util.scanner.classpath.jboss.JBossVFSv3ClassPathLocationScanner;
import org.flywaydb.core.internal.util.scanner.classpath.jboss.JBossVFSv2UrlResolver;
import org.flywaydb.core.internal.util.FeatureDetector;
import java.util.Enumeration;
import java.net.URLDecoder;
import java.util.Collection;
import org.flywaydb.core.internal.util.UrlUtils;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.internal.util.ClassUtils;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Iterator;
import java.util.TreeSet;
import org.flywaydb.core.internal.util.scanner.Resource;
import java.util.HashMap;
import java.util.Set;
import java.net.URL;
import java.util.List;
import org.flywaydb.core.internal.util.Location;
import java.util.Map;
import org.flywaydb.core.internal.util.logging.Log;

public class ClassPathScanner implements ResourceAndClassScanner
{
    private static final Log LOG;
    private final ClassLoader classLoader;
    private final Map<Location, List<URL>> locationUrlCache;
    private final Map<String, ClassPathLocationScanner> locationScannerCache;
    private final Map<ClassPathLocationScanner, Map<URL, Set<String>>> resourceNameCache;
    
    public ClassPathScanner(final ClassLoader classLoader) {
        this.locationUrlCache = new HashMap<Location, List<URL>>();
        this.locationScannerCache = new HashMap<String, ClassPathLocationScanner>();
        this.resourceNameCache = new HashMap<ClassPathLocationScanner, Map<URL, Set<String>>>();
        this.classLoader = classLoader;
    }
    
    @Override
    public Resource[] scanForResources(final Location path, final String prefix, final String suffix) throws IOException {
        ClassPathScanner.LOG.debug("Scanning for classpath resources at '" + path + "' (Prefix: '" + prefix + "', Suffix: '" + suffix + "')");
        final Set<Resource> resources = new TreeSet<Resource>();
        final Set<String> resourceNames = this.findResourceNames(path, prefix, suffix);
        for (final String resourceName : resourceNames) {
            resources.add(new ClassPathResource(resourceName, this.classLoader));
            ClassPathScanner.LOG.debug("Found resource: " + resourceName);
        }
        return resources.toArray(new Resource[resources.size()]);
    }
    
    @Override
    public Class<?>[] scanForClasses(final Location location, final Class<?> implementedInterface) throws Exception {
        ClassPathScanner.LOG.debug("Scanning for classes at '" + location + "' (Implementing: '" + implementedInterface.getName() + "')");
        final List<Class<?>> classes = new ArrayList<Class<?>>();
        final Set<String> resourceNames = this.findResourceNames(location, "", ".class");
        for (final String resourceName : resourceNames) {
            final String className = this.toClassName(resourceName);
            final Class<?> clazz = this.classLoader.loadClass(className);
            if (Modifier.isAbstract(clazz.getModifiers()) || clazz.isEnum() || clazz.isAnonymousClass()) {
                ClassPathScanner.LOG.debug("Skipping non-instantiable class: " + className);
            }
            else {
                if (!implementedInterface.isAssignableFrom(clazz)) {
                    continue;
                }
                try {
                    ClassUtils.instantiate(className, this.classLoader);
                }
                catch (Exception e) {
                    throw new FlywayException("Unable to instantiate class: " + className, e);
                }
                classes.add(clazz);
                ClassPathScanner.LOG.debug("Found class: " + className);
            }
        }
        return classes.toArray(new Class[classes.size()]);
    }
    
    private String toClassName(final String resourceName) {
        final String nameWithDots = resourceName.replace("/", ".");
        return nameWithDots.substring(0, nameWithDots.length() - ".class".length());
    }
    
    private Set<String> findResourceNames(final Location location, final String prefix, final String suffix) throws IOException {
        final Set<String> resourceNames = new TreeSet<String>();
        final List<URL> locationsUrls = this.getLocationUrlsForPath(location);
        for (final URL locationUrl : locationsUrls) {
            ClassPathScanner.LOG.debug("Scanning URL: " + locationUrl.toExternalForm());
            final UrlResolver urlResolver = this.createUrlResolver(locationUrl.getProtocol());
            final URL resolvedUrl = urlResolver.toStandardJavaUrl(locationUrl);
            final String protocol = resolvedUrl.getProtocol();
            final ClassPathLocationScanner classPathLocationScanner = this.createLocationScanner(protocol);
            if (classPathLocationScanner == null) {
                final String scanRoot = UrlUtils.toFilePath(resolvedUrl);
                ClassPathScanner.LOG.warn("Unable to scan location: " + scanRoot + " (unsupported protocol: " + protocol + ")");
            }
            else {
                Set<String> names = this.resourceNameCache.get(classPathLocationScanner).get(resolvedUrl);
                if (names == null) {
                    names = classPathLocationScanner.findResourceNames(location.getPath(), resolvedUrl);
                    this.resourceNameCache.get(classPathLocationScanner).put(resolvedUrl, names);
                }
                resourceNames.addAll(names);
            }
        }
        return this.filterResourceNames(resourceNames, prefix, suffix);
    }
    
    private List<URL> getLocationUrlsForPath(final Location location) throws IOException {
        if (this.locationUrlCache.containsKey(location)) {
            return this.locationUrlCache.get(location);
        }
        ClassPathScanner.LOG.debug("Determining location urls for " + location + " using ClassLoader " + this.classLoader + " ...");
        final List<URL> locationUrls = new ArrayList<URL>();
        if (this.classLoader.getClass().getName().startsWith("com.ibm")) {
            final Enumeration<URL> urls = this.classLoader.getResources(location.getPath() + "/flyway.location");
            if (!urls.hasMoreElements()) {
                ClassPathScanner.LOG.warn("Unable to resolve location " + location + " (ClassLoader: " + this.classLoader + ")" + " On WebSphere an empty file named flyway.location must be present on the classpath location for WebSphere to find it!");
            }
            while (urls.hasMoreElements()) {
                final URL url = urls.nextElement();
                locationUrls.add(new URL(URLDecoder.decode(url.toExternalForm(), "UTF-8").replace("/flyway.location", "")));
            }
        }
        else {
            final Enumeration<URL> urls = this.classLoader.getResources(location.getPath());
            if (!urls.hasMoreElements()) {
                ClassPathScanner.LOG.warn("Unable to resolve location " + location);
            }
            while (urls.hasMoreElements()) {
                locationUrls.add(urls.nextElement());
            }
        }
        this.locationUrlCache.put(location, locationUrls);
        return locationUrls;
    }
    
    private UrlResolver createUrlResolver(final String protocol) {
        if (new FeatureDetector(this.classLoader).isJBossVFSv2Available() && protocol.startsWith("vfs")) {
            return new JBossVFSv2UrlResolver();
        }
        return new DefaultUrlResolver();
    }
    
    private ClassPathLocationScanner createLocationScanner(final String protocol) {
        if (this.locationScannerCache.containsKey(protocol)) {
            return this.locationScannerCache.get(protocol);
        }
        if ("file".equals(protocol)) {
            final FileSystemClassPathLocationScanner locationScanner = new FileSystemClassPathLocationScanner();
            this.locationScannerCache.put(protocol, locationScanner);
            this.resourceNameCache.put(locationScanner, new HashMap<URL, Set<String>>());
            return locationScanner;
        }
        if ("jar".equals(protocol) || "zip".equals(protocol) || "wsjar".equals(protocol)) {
            final JarFileClassPathLocationScanner locationScanner2 = new JarFileClassPathLocationScanner();
            this.locationScannerCache.put(protocol, locationScanner2);
            this.resourceNameCache.put(locationScanner2, new HashMap<URL, Set<String>>());
            return locationScanner2;
        }
        final FeatureDetector featureDetector = new FeatureDetector(this.classLoader);
        if (featureDetector.isJBossVFSv3Available() && "vfs".equals(protocol)) {
            final JBossVFSv3ClassPathLocationScanner locationScanner3 = new JBossVFSv3ClassPathLocationScanner();
            this.locationScannerCache.put(protocol, locationScanner3);
            this.resourceNameCache.put(locationScanner3, new HashMap<URL, Set<String>>());
            return locationScanner3;
        }
        if (featureDetector.isOsgiFrameworkAvailable() && ("bundle".equals(protocol) || "bundleresource".equals(protocol))) {
            final OsgiClassPathLocationScanner locationScanner4 = new OsgiClassPathLocationScanner();
            this.locationScannerCache.put(protocol, locationScanner4);
            this.resourceNameCache.put(locationScanner4, new HashMap<URL, Set<String>>());
            return locationScanner4;
        }
        return null;
    }
    
    private Set<String> filterResourceNames(final Set<String> resourceNames, final String prefix, final String suffix) {
        final Set<String> filteredResourceNames = new TreeSet<String>();
        for (final String resourceName : resourceNames) {
            final String fileName = resourceName.substring(resourceName.lastIndexOf("/") + 1);
            if (fileName.startsWith(prefix) && fileName.endsWith(suffix) && fileName.length() > (prefix + suffix).length()) {
                filteredResourceNames.add(resourceName);
            }
            else {
                ClassPathScanner.LOG.debug("Filtering out resource: " + resourceName + " (filename: " + fileName + ")");
            }
        }
        return filteredResourceNames;
    }
    
    static {
        LOG = LogFactory.getLog(ClassPathScanner.class);
    }
}
