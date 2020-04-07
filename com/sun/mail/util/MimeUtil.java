// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.mail.util;

import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.mail.internet.MimePart;
import java.lang.reflect.Method;

public class MimeUtil
{
    private static final Method cleanContentType;
    static /* synthetic */ Class class$javax$mail$internet$MimePart;
    static /* synthetic */ Class class$java$lang$String;
    
    public static String cleanContentType(final MimePart mp, final String contentType) {
        if (MimeUtil.cleanContentType != null) {
            try {
                return (String)MimeUtil.cleanContentType.invoke(null, mp, contentType);
            }
            catch (Exception ex) {
                return contentType;
            }
        }
        return contentType;
    }
    
    private static ClassLoader getContextClassLoader() {
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
    
    static /* synthetic */ Class class$(final String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x) {
            throw new NoClassDefFoundError().initCause(x);
        }
    }
    
    static {
        Method meth = null;
        try {
            final String cth = System.getProperty("mail.mime.contenttypehandler");
            if (cth != null) {
                final ClassLoader cl = getContextClassLoader();
                Class clsHandler = null;
                if (cl != null) {
                    try {
                        clsHandler = Class.forName(cth, false, cl);
                    }
                    catch (ClassNotFoundException ex4) {}
                }
                if (clsHandler == null) {
                    clsHandler = Class.forName(cth);
                }
                meth = clsHandler.getMethod("cleanContentType", (MimeUtil.class$javax$mail$internet$MimePart == null) ? (MimeUtil.class$javax$mail$internet$MimePart = class$("javax.mail.internet.MimePart")) : MimeUtil.class$javax$mail$internet$MimePart, (MimeUtil.class$java$lang$String == null) ? (MimeUtil.class$java$lang$String = class$("java.lang.String")) : MimeUtil.class$java$lang$String);
            }
        }
        catch (ClassNotFoundException ex) {}
        catch (NoSuchMethodException ex2) {}
        catch (RuntimeException ex3) {}
        finally {
            cleanContentType = meth;
        }
    }
}
