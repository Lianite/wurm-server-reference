// 
// Decompiled by Procyon v0.5.30
// 

package javax.xml.stream;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.File;

class FactoryFinder
{
    private static boolean debug;
    
    private static void debugPrintln(final String msg) {
        if (FactoryFinder.debug) {
            System.err.println("STREAM: " + msg);
        }
    }
    
    private static ClassLoader findClassLoader() throws FactoryConfigurationError {
        ClassLoader classLoader;
        try {
            final Class clazz = Class.forName(FactoryFinder.class.getName() + "$ClassLoaderFinderConcrete");
            final ClassLoaderFinder clf = clazz.newInstance();
            classLoader = clf.getContextClassLoader();
        }
        catch (LinkageError le) {
            classLoader = FactoryFinder.class.getClassLoader();
        }
        catch (ClassNotFoundException x2) {
            classLoader = FactoryFinder.class.getClassLoader();
        }
        catch (Exception x) {
            throw new FactoryConfigurationError(x.toString(), x);
        }
        return classLoader;
    }
    
    private static Object newInstance(final String className, final ClassLoader classLoader) throws FactoryConfigurationError {
        try {
            Class spiClass;
            if (classLoader == null) {
                spiClass = Class.forName(className);
            }
            else {
                spiClass = classLoader.loadClass(className);
            }
            return spiClass.newInstance();
        }
        catch (ClassNotFoundException x) {
            throw new FactoryConfigurationError("Provider " + className + " not found", x);
        }
        catch (Exception x2) {
            throw new FactoryConfigurationError("Provider " + className + " could not be instantiated: " + x2, x2);
        }
    }
    
    static Object find(final String factoryId) throws FactoryConfigurationError {
        return find(factoryId, null);
    }
    
    static Object find(final String factoryId, final String fallbackClassName) throws FactoryConfigurationError {
        final ClassLoader classLoader = findClassLoader();
        return find(factoryId, fallbackClassName, classLoader);
    }
    
    static Object find(final String factoryId, final String fallbackClassName, final ClassLoader classLoader) throws FactoryConfigurationError {
        try {
            final String systemProp = System.getProperty(factoryId);
            if (systemProp != null) {
                debugPrintln("found system property" + systemProp);
                return newInstance(systemProp, classLoader);
            }
        }
        catch (SecurityException ex3) {}
        try {
            final String javah = System.getProperty("java.home");
            final String configFile = javah + File.separator + "lib" + File.separator + "jaxp.properties";
            final File f = new File(configFile);
            if (f.exists()) {
                final Properties props = new Properties();
                props.load(new FileInputStream(f));
                final String factoryClassName = props.getProperty(factoryId);
                debugPrintln("found java.home property " + factoryClassName);
                return newInstance(factoryClassName, classLoader);
            }
        }
        catch (Exception ex) {
            if (FactoryFinder.debug) {
                ex.printStackTrace();
            }
        }
        final String serviceId = "META-INF/services/" + factoryId;
        try {
            InputStream is = null;
            if (classLoader == null) {
                is = ClassLoader.getSystemResourceAsStream(serviceId);
            }
            else {
                is = classLoader.getResourceAsStream(serviceId);
            }
            if (is != null) {
                debugPrintln("found " + serviceId);
                final BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                final String factoryClassName2 = rd.readLine();
                rd.close();
                if (factoryClassName2 != null && !"".equals(factoryClassName2)) {
                    debugPrintln("loaded from services: " + factoryClassName2);
                    return newInstance(factoryClassName2, classLoader);
                }
            }
        }
        catch (Exception ex2) {
            if (FactoryFinder.debug) {
                ex2.printStackTrace();
            }
        }
        if (fallbackClassName == null) {
            throw new FactoryConfigurationError("Provider for " + factoryId + " cannot be found", null);
        }
        debugPrintln("loaded from fallback value: " + fallbackClassName);
        return newInstance(fallbackClassName, classLoader);
    }
    
    static {
        FactoryFinder.debug = false;
        try {
            FactoryFinder.debug = (System.getProperty("xml.stream.debug") != null);
        }
        catch (Exception ex) {}
    }
    
    private abstract static class ClassLoaderFinder
    {
        abstract ClassLoader getContextClassLoader();
    }
    
    static class ClassLoaderFinderConcrete extends ClassLoaderFinder
    {
        ClassLoader getContextClassLoader() {
            return Thread.currentThread().getContextClassLoader();
        }
    }
}
