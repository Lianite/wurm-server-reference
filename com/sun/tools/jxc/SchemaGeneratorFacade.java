// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.jxc;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

public class SchemaGeneratorFacade
{
    static /* synthetic */ Class class$com$sun$tools$jxc$SchemaGeneratorFacade;
    static /* synthetic */ Class array$Ljava$lang$String;
    
    public static void main(final String[] args) throws Throwable {
        try {
            ClassLoader cl = ((SchemaGeneratorFacade.class$com$sun$tools$jxc$SchemaGeneratorFacade == null) ? (SchemaGeneratorFacade.class$com$sun$tools$jxc$SchemaGeneratorFacade = class$("com.sun.tools.jxc.SchemaGeneratorFacade")) : SchemaGeneratorFacade.class$com$sun$tools$jxc$SchemaGeneratorFacade).getClassLoader();
            if (cl == null) {
                cl = ClassLoader.getSystemClassLoader();
            }
            final Class driver = cl.loadClass("com.sun.tools.jxc.SchemaGenerator");
            final Method mainMethod = driver.getDeclaredMethod("main", (SchemaGeneratorFacade.array$Ljava$lang$String == null) ? (SchemaGeneratorFacade.array$Ljava$lang$String = class$("[Ljava.lang.String;")) : SchemaGeneratorFacade.array$Ljava$lang$String);
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
            System.err.println("schemagen requires JDK 5.0 or later. Please download it from http://java.sun.com/j2se/1.5/");
        }
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
