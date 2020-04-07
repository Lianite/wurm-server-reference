// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.util;

import java.net.URL;

public class Which
{
    public static String which(final Class clazz) {
        return which(clazz.getName(), clazz.getClassLoader());
    }
    
    public static String which(final String classname, ClassLoader loader) {
        final String classnameAsResource = classname.replace('.', '/') + ".class";
        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }
        final URL it = loader.getResource(classnameAsResource);
        if (it != null) {
            return it.toString();
        }
        return null;
    }
}
