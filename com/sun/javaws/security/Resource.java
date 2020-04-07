// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.security;

import java.security.CodeSigner;
import java.security.cert.Certificate;
import java.util.jar.Manifest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public abstract class Resource
{
    public abstract String getName();
    
    public abstract URL getURL();
    
    public abstract URL getCodeSourceURL();
    
    public abstract InputStream getInputStream() throws IOException;
    
    public abstract int getContentLength() throws IOException;
    
    public byte[] getBytes() throws IOException {
        final InputStream inputStream = this.getInputStream();
        int i = this.getContentLength();
        byte[] array;
        try {
            if (i != -1) {
                array = new byte[i];
                while (i > 0) {
                    final int read = inputStream.read(array, array.length - i, i);
                    if (read == -1) {
                        throw new IOException("unexpected EOF");
                    }
                    i -= read;
                }
            }
            else {
                int n;
                int read2;
                byte[] array2 = null;
                for (array = new byte[1024], n = 0; (read2 = inputStream.read(array, n, array.length - n)) != -1; array = array2) {
                    n += read2;
                    if (n >= array.length) {
                        array2 = new byte[n * 2];
                        System.arraycopy(array, 0, array2, 0, n);
                    }
                }
                if (n != array.length) {
                    final byte[] array3 = new byte[n];
                    System.arraycopy(array, 0, array3, 0, n);
                    array = array3;
                }
            }
        }
        finally {
            inputStream.close();
        }
        return array;
    }
    
    public Manifest getManifest() throws IOException {
        return null;
    }
    
    public Certificate[] getCertificates() {
        return null;
    }
    
    public CodeSigner[] getCodeSigners() {
        return null;
    }
}
