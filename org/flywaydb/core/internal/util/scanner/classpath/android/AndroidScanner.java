// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util.scanner.classpath.android;

import org.flywaydb.core.internal.util.logging.LogFactory;
import java.util.Enumeration;
import org.flywaydb.core.internal.util.ClassUtils;
import java.lang.reflect.Modifier;
import dalvik.system.DexFile;
import java.util.List;
import java.util.ArrayList;
import org.flywaydb.core.internal.util.scanner.Resource;
import org.flywaydb.core.internal.util.Location;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.android.ContextHolder;
import dalvik.system.PathClassLoader;
import android.content.Context;
import org.flywaydb.core.internal.util.logging.Log;
import org.flywaydb.core.internal.util.scanner.classpath.ResourceAndClassScanner;

public class AndroidScanner implements ResourceAndClassScanner
{
    private static final Log LOG;
    private final Context context;
    private final PathClassLoader classLoader;
    
    public AndroidScanner(final ClassLoader classLoader) {
        this.classLoader = (PathClassLoader)classLoader;
        this.context = ContextHolder.getContext();
        if (this.context == null) {
            throw new FlywayException("Unable to scan for Migrations! Context not set. Within an activity you can fix this with org.flywaydb.core.api.android.ContextHolder.setContext(this);");
        }
    }
    
    @Override
    public Resource[] scanForResources(final Location location, final String prefix, final String suffix) throws Exception {
        final List<Resource> resources = new ArrayList<Resource>();
        final String path = location.getPath();
        for (final String asset : this.context.getAssets().list(path)) {
            if (asset.startsWith(prefix) && asset.endsWith(suffix) && asset.length() > (prefix + suffix).length()) {
                resources.add(new AndroidResource(this.context.getAssets(), path, asset));
            }
            else {
                AndroidScanner.LOG.debug("Filtering out asset: " + asset);
            }
        }
        return resources.toArray(new Resource[resources.size()]);
    }
    
    @Override
    public Class<?>[] scanForClasses(final Location location, final Class<?> implementedInterface) throws Exception {
        final String pkg = location.getPath().replace("/", ".");
        final List<Class> classes = new ArrayList<Class>();
        final DexFile dex = new DexFile(this.context.getApplicationInfo().sourceDir);
        final Enumeration<String> entries = (Enumeration<String>)dex.entries();
        while (entries.hasMoreElements()) {
            final String className = entries.nextElement();
            if (className.startsWith(pkg)) {
                final Class<?> clazz = (Class<?>)this.classLoader.loadClass(className);
                if (Modifier.isAbstract(clazz.getModifiers())) {
                    AndroidScanner.LOG.debug("Skipping abstract class: " + className);
                }
                else {
                    if (!implementedInterface.isAssignableFrom(clazz)) {
                        continue;
                    }
                    try {
                        ClassUtils.instantiate(className, (ClassLoader)this.classLoader);
                    }
                    catch (Exception e) {
                        throw new FlywayException("Unable to instantiate class: " + className);
                    }
                    classes.add(clazz);
                    AndroidScanner.LOG.debug("Found class: " + className);
                }
            }
        }
        return classes.toArray(new Class[classes.size()]);
    }
    
    static {
        LOG = LogFactory.getLog(AndroidScanner.class);
    }
}
