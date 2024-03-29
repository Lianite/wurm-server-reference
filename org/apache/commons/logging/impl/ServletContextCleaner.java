// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.commons.logging.impl;

import java.lang.reflect.Method;
import org.apache.commons.logging.LogFactory;
import java.lang.reflect.InvocationTargetException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ServletContextCleaner implements ServletContextListener
{
    private static final Class[] RELEASE_SIGNATURE;
    static /* synthetic */ Class class$java$lang$ClassLoader;
    
    public void contextDestroyed(final ServletContextEvent sce) {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        final Object[] params = { tccl };
        ClassLoader loader = tccl;
        while (loader != null) {
            try {
                final Class logFactoryClass = loader.loadClass("org.apache.commons.logging.LogFactory");
                final Method releaseMethod = logFactoryClass.getMethod("release", (Class[])ServletContextCleaner.RELEASE_SIGNATURE);
                releaseMethod.invoke(null, params);
                loader = logFactoryClass.getClassLoader().getParent();
            }
            catch (ClassNotFoundException ex) {
                loader = null;
            }
            catch (NoSuchMethodException ex2) {
                System.err.println("LogFactory instance found which does not support release method!");
                loader = null;
            }
            catch (IllegalAccessException ex3) {
                System.err.println("LogFactory instance found which is not accessable!");
                loader = null;
            }
            catch (InvocationTargetException ex4) {
                System.err.println("LogFactory instance release method failed!");
                loader = null;
            }
        }
        LogFactory.release(tccl);
    }
    
    public void contextInitialized(final ServletContextEvent sce) {
    }
    
    static /* synthetic */ Class class$(final String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x) {
            throw new NoClassDefFoundError(x.getMessage());
        }
    }
    
    static {
        RELEASE_SIGNATURE = new Class[] { (ServletContextCleaner.class$java$lang$ClassLoader == null) ? (ServletContextCleaner.class$java$lang$ClassLoader = class$("java.lang.ClassLoader")) : ServletContextCleaner.class$java$lang$ClassLoader };
    }
}
