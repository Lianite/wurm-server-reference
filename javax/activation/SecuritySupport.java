// 
// Decompiled by Procyon v0.5.30
// 

package javax.activation;

import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;
import java.net.URL;
import java.security.PrivilegedActionException;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;

class SecuritySupport
{
    public static ClassLoader getContextClassLoader() {
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction() {
            public Object run() {
                ClassLoader cl = null;
                try {
                    cl = Thread.currentThread().getContextClassLoader();
                }
                catch (SecurityException ex) {}
                return cl;
            }
        });
    }
    
    public static InputStream getResourceAsStream(final Class c, final String name) throws IOException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<InputStream>)new PrivilegedExceptionAction() {
                public Object run() throws IOException {
                    return c.getResourceAsStream(name);
                }
            });
        }
        catch (PrivilegedActionException e) {
            throw (IOException)e.getException();
        }
    }
    
    public static URL[] getResources(final ClassLoader cl, final String name) {
        return AccessController.doPrivileged((PrivilegedAction<URL[]>)new PrivilegedAction() {
            public Object run() {
                URL[] ret = null;
                try {
                    final List v = new ArrayList();
                    final Enumeration e = cl.getResources(name);
                    while (e != null && e.hasMoreElements()) {
                        final URL url = e.nextElement();
                        if (url != null) {
                            v.add(url);
                        }
                    }
                    if (v.size() > 0) {
                        ret = new URL[v.size()];
                        ret = v.toArray(ret);
                    }
                }
                catch (IOException ioex) {}
                catch (SecurityException ex) {}
                return ret;
            }
        });
    }
    
    public static URL[] getSystemResources(final String name) {
        return AccessController.doPrivileged((PrivilegedAction<URL[]>)new PrivilegedAction() {
            public Object run() {
                URL[] ret = null;
                try {
                    final List v = new ArrayList();
                    final Enumeration e = ClassLoader.getSystemResources(name);
                    while (e != null && e.hasMoreElements()) {
                        final URL url = e.nextElement();
                        if (url != null) {
                            v.add(url);
                        }
                    }
                    if (v.size() > 0) {
                        ret = new URL[v.size()];
                        ret = v.toArray(ret);
                    }
                }
                catch (IOException ioex) {}
                catch (SecurityException ex) {}
                return ret;
            }
        });
    }
    
    public static InputStream openStream(final URL url) throws IOException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<InputStream>)new PrivilegedExceptionAction() {
                public Object run() throws IOException {
                    return url.openStream();
                }
            });
        }
        catch (PrivilegedActionException e) {
            throw (IOException)e.getException();
        }
    }
}
