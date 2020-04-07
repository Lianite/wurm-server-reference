// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.security;

import com.sun.jnlp.PrintServiceImpl;
import com.sun.jnlp.JNLPClassLoader;

public class JavaWebStartSecurity extends SecurityManager
{
    private JNLPClassLoader currentJNLPClassLoader() {
        final Class[] classContext = this.getClassContext();
        for (int i = 0; i < classContext.length; ++i) {
            final ClassLoader classLoader = classContext[i].getClassLoader();
            if (classLoader instanceof JNLPClassLoader) {
                return (JNLPClassLoader)classLoader;
            }
        }
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader instanceof JNLPClassLoader) {
            return (JNLPClassLoader)contextClassLoader;
        }
        return null;
    }
    
    public void checkAwtEventQueueAccess() {
        if (!AppContextUtil.isApplicationAppContext() && this.currentJNLPClassLoader() != null) {
            super.checkAwtEventQueueAccess();
        }
    }
    
    public Class[] getExecutionStackContext() {
        return super.getClassContext();
    }
    
    public void checkPrintJobAccess() {
        try {
            super.checkPrintJobAccess();
        }
        catch (SecurityException ex) {
            if (PrintServiceImpl.requestPrintPermission()) {
                return;
            }
            throw ex;
        }
    }
}
