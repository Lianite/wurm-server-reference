// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.security;

import java.util.Locale;
import com.sun.javaws.Globals;
import com.sun.deploy.util.TraceLevel;
import java.security.CodeSource;
import java.util.Iterator;
import java.util.jar.Manifest;
import java.util.Enumeration;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.Map;
import com.sun.deploy.util.Trace;
import java.io.IOException;
import com.sun.javaws.exceptions.JARSigningException;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.jar.JarEntry;
import java.security.cert.Certificate;
import java.io.File;
import com.sun.javaws.cache.DownloadProtocol;
import java.util.jar.JarFile;
import java.net.URL;

public class SigningInfo
{
    public static Certificate[] checkSigning(final URL url, final String s, final JarFile jarFile, final DownloadProtocol.DownloadDelegate downloadDelegate, final File file) throws JARSigningException {
        Certificate[] array = null;
        boolean b = false;
        boolean b2 = false;
        final int size = jarFile.size();
        int n = 0;
        if (downloadDelegate != null) {
            downloadDelegate.validating(url, 0, size);
        }
        BufferedOutputStream bufferedOutputStream = null;
        InputStream inputStream = null;
        try {
            final byte[] array2 = new byte[32768];
            final Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                final JarEntry jarEntry = entries.nextElement();
                final String name = jarEntry.getName();
                if (!name.startsWith("META-INF/") && !name.endsWith("/") && jarEntry.getSize() != 0L) {
                    inputStream = jarFile.getInputStream(jarEntry);
                    if (file != null && name.indexOf("/") == -1) {
                        bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File(file, jarEntry.getName())));
                    }
                    int read;
                    while ((read = inputStream.read(array2, 0, array2.length)) != -1) {
                        if (bufferedOutputStream != null) {
                            bufferedOutputStream.write(array2, 0, read);
                        }
                    }
                    if (bufferedOutputStream != null) {
                        bufferedOutputStream.close();
                        bufferedOutputStream = null;
                    }
                    inputStream.close();
                    inputStream = null;
                    Certificate[] certificates = jarEntry.getCertificates();
                    if (certificates != null && certificates.length == 0) {
                        certificates = null;
                    }
                    boolean b3 = false;
                    if (certificates != null) {
                        b3 = true;
                        if (array == null) {
                            array = certificates;
                        }
                        else if (!equalChains(array, certificates)) {
                            throw new JARSigningException(url, s, 1);
                        }
                    }
                    b = (b || b3);
                    b2 = (b2 || !b3);
                }
                if (downloadDelegate != null) {
                    downloadDelegate.validating(url, ++n, size);
                }
            }
        }
        catch (SecurityException ex) {
            throw new JARSigningException(url, s, 2, ex);
        }
        catch (IOException ex2) {
            throw new JARSigningException(url, s, 2, ex2);
        }
        finally {
            try {
                if (bufferedOutputStream != null) {
                    bufferedOutputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            catch (IOException ex3) {
                Trace.ignoredException((Exception)ex3);
            }
        }
        if (b && b2) {
            throw new JARSigningException(url, s, 3);
        }
        if (array != null) {
            try {
                final Manifest manifest = jarFile.getManifest();
                final Iterator<Map.Entry<String, Attributes>> iterator = manifest.getEntries().entrySet().iterator();
                while (iterator.hasNext()) {
                    final String s2 = iterator.next().getKey();
                    if (isSignedManifestEntry(manifest, s2) && jarFile.getEntry(s2) == null) {
                        throw new JARSigningException(url, s, 4, s2);
                    }
                }
            }
            catch (IOException ex4) {
                throw new JARSigningException(url, s, 2, ex4);
            }
        }
        return array;
    }
    
    public static CodeSource getCodeSource(final URL url, final JarFile jarFile) {
        final Enumeration<JarEntry> entries = jarFile.entries();
        final byte[] array = new byte[32768];
        while (entries.hasMoreElements()) {
            final JarEntry jarEntry = entries.nextElement();
            final String name = jarEntry.getName();
            Trace.println(" ... name=" + name, TraceLevel.SECURITY);
            if (!name.startsWith("META-INF/") && !name.endsWith("/") && jarEntry.getSize() != 0L) {
                try {
                    final InputStream inputStream = jarFile.getInputStream(jarEntry);
                    while (inputStream.read(array, 0, array.length) != -1) {}
                    inputStream.close();
                }
                catch (IOException ex) {
                    Trace.ignoredException((Exception)ex);
                }
                if (Globals.isJavaVersionAtLeast15()) {
                    return new CodeSource(url, jarEntry.getCodeSigners());
                }
                return new CodeSource(url, jarEntry.getCertificates());
            }
        }
        return null;
    }
    
    public static boolean equalChains(final Certificate[] array, final Certificate[] array2) {
        if (array.length != array2.length) {
            return false;
        }
        for (int i = 0; i < array.length; ++i) {
            if (!array[i].equals(array2[i])) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean isSignedManifestEntry(final Manifest manifest, final String s) {
        final Attributes attributes = manifest.getAttributes(s);
        if (attributes != null) {
            final Iterator<Object> iterator = attributes.keySet().iterator();
            while (iterator.hasNext()) {
                final String upperCase = iterator.next().toString().toUpperCase(Locale.ENGLISH);
                if (upperCase.endsWith("-DIGEST") || upperCase.indexOf("-DIGEST-") != -1) {
                    return true;
                }
            }
        }
        return false;
    }
}
