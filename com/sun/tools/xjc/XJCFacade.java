// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

public class XJCFacade
{
    static /* synthetic */ Class class$com$sun$tools$xjc$XJCFacade;
    static /* synthetic */ Class array$Ljava$lang$String;
    
    public static void main(final String[] args) throws Throwable {
        String v = "2.0";
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals("-source") && i + 1 < args.length) {
                v = parseVersion(args[i + 1]);
            }
        }
        try {
            final ClassLoader cl = ClassLoaderBuilder.createProtectiveClassLoader(((XJCFacade.class$com$sun$tools$xjc$XJCFacade == null) ? (XJCFacade.class$com$sun$tools$xjc$XJCFacade = class$("com.sun.tools.xjc.XJCFacade")) : XJCFacade.class$com$sun$tools$xjc$XJCFacade).getClassLoader(), v);
            final Class driver = cl.loadClass("com.sun.tools.xjc.Driver");
            final Method mainMethod = driver.getDeclaredMethod("main", (XJCFacade.array$Ljava$lang$String == null) ? (XJCFacade.array$Ljava$lang$String = class$("[Ljava.lang.String;")) : XJCFacade.array$Ljava$lang$String);
            try {
                mainMethod.invoke(null, args);
            }
            catch (IllegalAccessException e) {
                throw e;
            }
            catch (InvocationTargetException e2) {
                if (e2.getTargetException() != null) {
                    throw e2.getTargetException();
                }
            }
        }
        catch (UnsupportedClassVersionError e3) {
            System.err.println("XJC requires JDK 5.0 or later. Please download it from http://java.sun.com/j2se/1.5/");
        }
    }
    
    private static String parseVersion(final String version) {
        if (version.equals("1.0")) {
            return version;
        }
        return "2.0";
    }
    
    static /* synthetic */ Class class$(final String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x) {
            throw new NoClassDefFoundError(x.getMessage());
        }
    }
}
