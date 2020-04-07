// 
// Decompiled by Procyon v0.5.30
// 

package javax.xml.bind;

import java.util.logging.Handler;
import java.util.logging.ConsoleHandler;
import java.io.InputStream;
import java.util.Properties;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.PrivilegedAction;
import java.security.AccessController;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.lang.reflect.Method;
import java.util.Map;
import java.net.URL;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

class ContextFinder
{
    private static final Logger logger;
    private static final String PLATFORM_DEFAULT_FACTORY_CLASS = "com.sun.xml.bind.v2.ContextFactory";
    
    private static void handleInvocationTargetException(final InvocationTargetException x) throws JAXBException {
        final Throwable t = x.getTargetException();
        if (t != null) {
            if (t instanceof JAXBException) {
                throw (JAXBException)t;
            }
            if (t instanceof RuntimeException) {
                throw (RuntimeException)t;
            }
            if (t instanceof Error) {
                throw (Error)t;
            }
        }
    }
    
    private static JAXBException handleClassCastException(final Class originalType, final Class targetType) {
        final URL targetTypeURL = which(targetType);
        return new JAXBException(Messages.format("JAXBContext.IllegalCast", originalType.getClassLoader().getResource("javax/xml/bind/JAXBContext.class"), targetTypeURL));
    }
    
    static JAXBContext newInstance(final String contextPath, final String className, final ClassLoader classLoader, final Map properties) throws JAXBException {
        try {
            Class spiClass;
            if (classLoader == null) {
                spiClass = Class.forName(className);
            }
            else {
                spiClass = classLoader.loadClass(className);
            }
            Object context = null;
            try {
                final Method m = spiClass.getMethod("createContext", String.class, ClassLoader.class, Map.class);
                context = m.invoke(null, contextPath, classLoader, properties);
            }
            catch (NoSuchMethodException ex) {}
            if (context == null) {
                final Method m = spiClass.getMethod("createContext", String.class, ClassLoader.class);
                context = m.invoke(null, contextPath, classLoader);
            }
            if (!(context instanceof JAXBContext)) {
                handleClassCastException(context.getClass(), JAXBContext.class);
            }
            return (JAXBContext)context;
        }
        catch (ClassNotFoundException x) {
            throw new JAXBException(Messages.format("ContextFinder.ProviderNotFound", className), x);
        }
        catch (InvocationTargetException x2) {
            handleInvocationTargetException(x2);
            Throwable e = x2;
            if (x2.getTargetException() != null) {
                e = x2.getTargetException();
            }
            throw new JAXBException(Messages.format("ContextFinder.CouldNotInstantiate", className, e), e);
        }
        catch (RuntimeException x3) {
            throw x3;
        }
        catch (Exception x4) {
            throw new JAXBException(Messages.format("ContextFinder.CouldNotInstantiate", className, x4), x4);
        }
    }
    
    static JAXBContext newInstance(final Class[] classes, final Map properties, final String className) throws JAXBException {
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Class spi;
        try {
            ContextFinder.logger.fine("Trying to load " + className);
            if (cl != null) {
                spi = cl.loadClass(className);
            }
            else {
                spi = Class.forName(className);
            }
        }
        catch (ClassNotFoundException e) {
            throw new JAXBException(e);
        }
        if (ContextFinder.logger.isLoggable(Level.FINE)) {
            ContextFinder.logger.fine("loaded " + className + " from " + which(spi));
        }
        Method m;
        try {
            m = spi.getMethod("createContext", Class[].class, Map.class);
        }
        catch (NoSuchMethodException e2) {
            throw new JAXBException(e2);
        }
        try {
            final Object context = m.invoke(null, classes, properties);
            if (!(context instanceof JAXBContext)) {
                throw handleClassCastException(context.getClass(), JAXBContext.class);
            }
            return (JAXBContext)context;
        }
        catch (IllegalAccessException e3) {
            throw new JAXBException(e3);
        }
        catch (InvocationTargetException e4) {
            handleInvocationTargetException(e4);
            Throwable x = e4;
            if (e4.getTargetException() != null) {
                x = e4.getTargetException();
            }
            throw new JAXBException(x);
        }
    }
    
    static JAXBContext find(final String factoryId, final String contextPath, final ClassLoader classLoader, final Map properties) throws JAXBException {
        final String jaxbContextFQCN = JAXBContext.class.getName();
        final StringTokenizer packages = new StringTokenizer(contextPath, ":");
        if (!packages.hasMoreTokens()) {
            throw new JAXBException(Messages.format("ContextFinder.NoPackageInContextPath"));
        }
        ContextFinder.logger.fine("Searching jaxb.properties");
        while (packages.hasMoreTokens()) {
            final String packageName = packages.nextToken(":").replace('.', '/');
            final StringBuilder propFileName = new StringBuilder().append(packageName).append("/jaxb.properties");
            final Properties props = loadJAXBProperties(classLoader, propFileName.toString());
            if (props != null) {
                if (props.containsKey(factoryId)) {
                    final String factoryClassName = props.getProperty(factoryId);
                    return newInstance(contextPath, factoryClassName, classLoader, properties);
                }
                throw new JAXBException(Messages.format("ContextFinder.MissingProperty", packageName, factoryId));
            }
        }
        ContextFinder.logger.fine("Searching the system property");
        String factoryClassName = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction(jaxbContextFQCN));
        if (factoryClassName != null) {
            return newInstance(contextPath, factoryClassName, classLoader, properties);
        }
        ContextFinder.logger.fine("Searching META-INF/services");
        try {
            final StringBuilder resource = new StringBuilder().append("META-INF/services/").append(jaxbContextFQCN);
            final InputStream resourceStream = classLoader.getResourceAsStream(resource.toString());
            if (resourceStream != null) {
                final BufferedReader r = new BufferedReader(new InputStreamReader(resourceStream, "UTF-8"));
                factoryClassName = r.readLine().trim();
                r.close();
                return newInstance(contextPath, factoryClassName, classLoader, properties);
            }
            ContextFinder.logger.fine("Unable to load:" + resource.toString());
        }
        catch (UnsupportedEncodingException e) {
            throw new JAXBException(e);
        }
        catch (IOException e2) {
            throw new JAXBException(e2);
        }
        ContextFinder.logger.fine("Trying to create the platform default provider");
        return newInstance(contextPath, "com.sun.xml.bind.v2.ContextFactory", classLoader, properties);
    }
    
    static JAXBContext find(final Class[] classes, final Map properties) throws JAXBException {
        final String jaxbContextFQCN = JAXBContext.class.getName();
        for (final Class c : classes) {
            final ClassLoader classLoader = AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
                public ClassLoader run() {
                    return c.getClassLoader();
                }
            });
            final Package pkg = c.getPackage();
            if (pkg != null) {
                final String packageName = pkg.getName().replace('.', '/');
                final String resourceName = packageName + "/jaxb.properties";
                ContextFinder.logger.fine("Trying to locate " + resourceName);
                final Properties props = loadJAXBProperties(classLoader, resourceName);
                if (props == null) {
                    ContextFinder.logger.fine("  not found");
                }
                else {
                    ContextFinder.logger.fine("  found");
                    if (props.containsKey("javax.xml.bind.context.factory")) {
                        final String factoryClassName = props.getProperty("javax.xml.bind.context.factory").trim();
                        return newInstance(classes, properties, factoryClassName);
                    }
                    throw new JAXBException(Messages.format("ContextFinder.MissingProperty", packageName, "javax.xml.bind.context.factory"));
                }
            }
        }
        ContextFinder.logger.fine("Checking system property " + jaxbContextFQCN);
        String factoryClassName = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction(jaxbContextFQCN));
        if (factoryClassName != null) {
            ContextFinder.logger.fine("  found " + factoryClassName);
            return newInstance(classes, properties, factoryClassName);
        }
        ContextFinder.logger.fine("  not found");
        ContextFinder.logger.fine("Checking META-INF/services");
        try {
            final String resource = "META-INF/services/" + jaxbContextFQCN;
            final ClassLoader classLoader2 = Thread.currentThread().getContextClassLoader();
            URL resourceURL;
            if (classLoader2 == null) {
                resourceURL = ClassLoader.getSystemResource(resource);
            }
            else {
                resourceURL = classLoader2.getResource(resource);
            }
            if (resourceURL != null) {
                ContextFinder.logger.fine("Reading " + resourceURL);
                final BufferedReader r = new BufferedReader(new InputStreamReader(resourceURL.openStream(), "UTF-8"));
                factoryClassName = r.readLine().trim();
                return newInstance(classes, properties, factoryClassName);
            }
            ContextFinder.logger.fine("Unable to find: " + resource);
        }
        catch (UnsupportedEncodingException e) {
            throw new JAXBException(e);
        }
        catch (IOException e2) {
            throw new JAXBException(e2);
        }
        ContextFinder.logger.fine("Trying to create the platform default provider");
        return newInstance(classes, properties, "com.sun.xml.bind.v2.ContextFactory");
    }
    
    private static Properties loadJAXBProperties(final ClassLoader classLoader, final String propFileName) throws JAXBException {
        Properties props = null;
        try {
            URL url;
            if (classLoader == null) {
                url = ClassLoader.getSystemResource(propFileName);
            }
            else {
                url = classLoader.getResource(propFileName);
            }
            if (url != null) {
                ContextFinder.logger.fine("loading props from " + url);
                props = new Properties();
                final InputStream is = url.openStream();
                props.load(is);
                is.close();
            }
        }
        catch (IOException ioe) {
            ContextFinder.logger.log(Level.FINE, "Unable to load " + propFileName, ioe);
            throw new JAXBException(ioe.toString(), ioe);
        }
        return props;
    }
    
    static URL which(final Class clazz, ClassLoader loader) {
        final String classnameAsResource = clazz.getName().replace('.', '/') + ".class";
        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }
        return loader.getResource(classnameAsResource);
    }
    
    static URL which(final Class clazz) {
        return which(clazz, clazz.getClassLoader());
    }
    
    static {
        logger = Logger.getLogger("javax.xml.bind");
        try {
            if (AccessController.doPrivileged((PrivilegedAction<Object>)new GetPropertyAction("jaxb.debug")) != null) {
                ContextFinder.logger.setUseParentHandlers(false);
                ContextFinder.logger.setLevel(Level.ALL);
                final ConsoleHandler handler = new ConsoleHandler();
                handler.setLevel(Level.ALL);
                ContextFinder.logger.addHandler(handler);
            }
        }
        catch (Throwable t) {}
    }
}
