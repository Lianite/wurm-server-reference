// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.istack.tools;

import java.net.MalformedURLException;
import java.util.Enumeration;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.io.ByteArrayOutputStream;

public class ParallelWorldClassLoader extends ClassLoader
{
    private final String prefix;
    
    public ParallelWorldClassLoader(final ClassLoader parent, final String prefix) {
        super(parent);
        this.prefix = prefix;
    }
    
    protected Class findClass(final String name) throws ClassNotFoundException {
        final StringBuffer sb = new StringBuffer(name.length() + this.prefix.length() + 6);
        sb.append(this.prefix).append(name.replace('.', '/')).append(".class");
        final InputStream is = this.getParent().getResourceAsStream(sb.toString());
        if (is == null) {
            throw new ClassNotFoundException(name);
        }
        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int len;
            while ((len = is.read(buf)) >= 0) {
                baos.write(buf, 0, len);
            }
            buf = baos.toByteArray();
            final int packIndex = name.lastIndexOf(46);
            if (packIndex != -1) {
                final String pkgname = name.substring(0, packIndex);
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
    
    protected URL findResource(final String name) {
        return this.getParent().getResource(this.prefix + name);
    }
    
    protected Enumeration<URL> findResources(final String name) throws IOException {
        return this.getParent().getResources(this.prefix + name);
    }
    
    public static URL toJarUrl(final URL res) throws ClassNotFoundException, MalformedURLException {
        String url = res.toExternalForm();
        if (!url.startsWith("jar:")) {
            throw new ClassNotFoundException("Loaded outside a jar " + url);
        }
        url = url.substring(4);
        url = url.substring(0, url.lastIndexOf(33));
        return new URL(url);
    }
}
