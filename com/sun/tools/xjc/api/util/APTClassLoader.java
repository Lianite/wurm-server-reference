// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.api.util;

import java.net.MalformedURLException;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.io.ByteArrayOutputStream;
import com.sun.istack.Nullable;
import java.net.URLClassLoader;

public final class APTClassLoader extends URLClassLoader
{
    private final String[] packagePrefixes;
    
    public APTClassLoader(@Nullable final ClassLoader parent, final String[] packagePrefixes) throws ToolsJarNotFoundException {
        super(getToolsJar(parent), parent);
        if (this.getURLs().length == 0) {
            this.packagePrefixes = new String[0];
        }
        else {
            this.packagePrefixes = packagePrefixes;
        }
    }
    
    public Class loadClass(final String className) throws ClassNotFoundException {
        for (final String prefix : this.packagePrefixes) {
            if (className.startsWith(prefix)) {
                return this.findClass(className);
            }
        }
        return super.loadClass(className);
    }
    
    protected Class findClass(final String name) throws ClassNotFoundException {
        final StringBuilder sb = new StringBuilder(name.length() + 6);
        sb.append(name.replace('.', '/')).append(".class");
        final InputStream is = this.getResourceAsStream(sb.toString());
        if (is == null) {
            throw new ClassNotFoundException("Class not found" + (Object)sb);
        }
        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int len;
            while ((len = is.read(buf)) >= 0) {
                baos.write(buf, 0, len);
            }
            buf = baos.toByteArray();
            final int i = name.lastIndexOf(46);
            if (i != -1) {
                final String pkgname = name.substring(0, i);
                final Package pkg = this.getPackage(pkgname);
                if (pkg == null) {
                    this.definePackage(pkgname, null, null, null, null, null, null, null);
                }
            }
            return this.defineClass(name, buf, 0, buf.length);
        }
        catch (IOException e) {
            throw new ClassNotFoundException(name, e);
        }
    }
    
    private static URL[] getToolsJar(@Nullable final ClassLoader parent) throws ToolsJarNotFoundException {
        try {
            Class.forName("com.sun.tools.javac.Main", false, parent);
            Class.forName("com.sun.tools.apt.Main", false, parent);
            return new URL[0];
        }
        catch (ClassNotFoundException e2) {
            final File jreHome = new File(System.getProperty("java.home"));
            final File toolsJar = new File(jreHome.getParent(), "lib/tools.jar");
            if (!toolsJar.exists()) {
                throw new ToolsJarNotFoundException(toolsJar);
            }
            try {
                return new URL[] { toolsJar.toURL() };
            }
            catch (MalformedURLException e) {
                throw new AssertionError((Object)e);
            }
        }
    }
}
