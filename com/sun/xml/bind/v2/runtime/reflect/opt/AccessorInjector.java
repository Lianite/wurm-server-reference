// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.reflect.opt;

import com.sun.xml.bind.Util;
import java.io.InputStream;
import com.sun.xml.bind.v2.bytecode.ClassTailor;
import java.util.logging.Level;
import java.util.logging.Logger;

class AccessorInjector
{
    private static final Logger logger;
    protected static final boolean noOptimize;
    private static final ClassLoader CLASS_LOADER;
    
    public static Class<?> prepare(final Class beanClass, final String templateClassName, final String newClassName, final String... replacements) {
        if (AccessorInjector.noOptimize) {
            return null;
        }
        try {
            final ClassLoader cl = beanClass.getClassLoader();
            if (cl == null) {
                return null;
            }
            Class c = Injector.find(cl, newClassName);
            if (c == null) {
                final byte[] image = tailor(templateClassName, newClassName, replacements);
                if (image == null) {
                    return null;
                }
                c = Injector.inject(cl, newClassName, image);
            }
            return (Class<?>)c;
        }
        catch (SecurityException e) {
            AccessorInjector.logger.log(Level.INFO, "Unable to create an optimized TransducedAccessor ", e);
            return null;
        }
    }
    
    private static byte[] tailor(final String templateClassName, final String newClassName, final String... replacements) {
        InputStream resource;
        if (AccessorInjector.CLASS_LOADER != null) {
            resource = AccessorInjector.CLASS_LOADER.getResourceAsStream(templateClassName + ".class");
        }
        else {
            resource = ClassLoader.getSystemResourceAsStream(templateClassName + ".class");
        }
        if (resource == null) {
            return null;
        }
        return ClassTailor.tailor(resource, templateClassName, newClassName, replacements);
    }
    
    static {
        logger = Util.getClassLogger();
        noOptimize = (Util.getSystemProperty(ClassTailor.class.getName() + ".noOptimize") != null);
        if (AccessorInjector.noOptimize) {
            AccessorInjector.logger.info("The optimized code generation is disabled");
        }
        CLASS_LOADER = AccessorInjector.class.getClassLoader();
    }
}
