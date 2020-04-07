// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util;

import java.security.ProtectionDomain;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import org.flywaydb.core.api.FlywayException;
import java.util.ArrayList;
import java.util.List;

public class ClassUtils
{
    public static synchronized <T> T instantiate(final String className, final ClassLoader classLoader) throws Exception {
        return (T)Class.forName(className, true, classLoader).newInstance();
    }
    
    public static <T> List<T> instantiateAll(final String[] classes, final ClassLoader classLoader) {
        final List<T> clazzes = new ArrayList<T>();
        for (final String clazz : classes) {
            if (StringUtils.hasLength(clazz)) {
                try {
                    clazzes.add(instantiate(clazz, classLoader));
                }
                catch (Exception e) {
                    throw new FlywayException("Unable to instantiate class: " + clazz, e);
                }
            }
        }
        return clazzes;
    }
    
    public static boolean isPresent(final String className, final ClassLoader classLoader) {
        try {
            classLoader.loadClass(className);
            return true;
        }
        catch (Throwable ex) {
            return false;
        }
    }
    
    public static String getShortName(final Class<?> aClass) {
        final String name = aClass.getName();
        return name.substring(name.lastIndexOf(".") + 1);
    }
    
    public static String getLocationOnDisk(final Class<?> aClass) {
        try {
            final ProtectionDomain protectionDomain = aClass.getProtectionDomain();
            if (protectionDomain == null) {
                return null;
            }
            final String url = protectionDomain.getCodeSource().getLocation().getPath();
            return URLDecoder.decode(url, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}
