// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.jnl;

import java.net.MalformedURLException;
import com.sun.deploy.resources.ResourceManager;
import java.io.BufferedInputStream;
import com.sun.javaws.net.HttpResponse;
import com.sun.javaws.JavawsFactory;
import com.sun.javaws.cache.Cache;
import java.net.URL;
import java.io.FileInputStream;
import java.io.File;
import java.io.InputStream;
import com.sun.javaws.exceptions.JNLParseException;
import com.sun.javaws.exceptions.MissingFieldException;
import com.sun.javaws.exceptions.BadFieldException;
import java.io.IOException;

public class LaunchDescFactory
{
    public static LaunchDesc buildDescriptor(final byte[] array) throws IOException, BadFieldException, MissingFieldException, JNLParseException {
        return XMLFormat.parse(array);
    }
    
    public static LaunchDesc buildDescriptor(final InputStream inputStream) throws IOException, BadFieldException, MissingFieldException, JNLParseException {
        return buildDescriptor(readBytes(inputStream, -1L));
    }
    
    public static LaunchDesc buildDescriptor(final InputStream inputStream, final long n) throws IOException, BadFieldException, MissingFieldException, JNLParseException {
        return buildDescriptor(readBytes(inputStream, n));
    }
    
    public static LaunchDesc buildDescriptor(final File file) throws IOException, BadFieldException, MissingFieldException, JNLParseException {
        return buildDescriptor(new FileInputStream(file), file.length());
    }
    
    public static LaunchDesc buildDescriptor(final URL url) throws IOException, BadFieldException, MissingFieldException, JNLParseException {
        final File cachedLaunchedFile = Cache.getCachedLaunchedFile(url);
        if (cachedLaunchedFile != null) {
            return buildDescriptor(cachedLaunchedFile);
        }
        final HttpResponse doGetRequest = JavawsFactory.getHttpRequestImpl().doGetRequest(url);
        final BufferedInputStream inputStream = doGetRequest.getInputStream();
        final LaunchDesc buildDescriptor = buildDescriptor(inputStream, doGetRequest.getContentLength());
        inputStream.close();
        return buildDescriptor;
    }
    
    public static LaunchDesc buildDescriptor(final String s) throws IOException, BadFieldException, MissingFieldException, JNLParseException {
        try {
            return buildDescriptor(new URL(s));
        }
        catch (MalformedURLException ex) {
            if (ex.getMessage().indexOf("https") != -1) {
                throw new BadFieldException(ResourceManager.getString("launch.error.badfield.download.https"), "<jnlp>", "https");
            }
            final FileInputStream fileInputStream = new FileInputStream(s);
            final long length = new File(s).length();
            if (length > 1048576L) {
                throw new IOException("File too large");
            }
            return buildDescriptor(fileInputStream, (int)length);
        }
    }
    
    public static LaunchDesc buildInternalLaunchDesc(final String s, final String s2, final String s3) {
        return new LaunchDesc("0.1", null, null, null, null, 1, null, 5, null, null, null, null, (s3 == null) ? s : s3, s2, null);
    }
    
    public static byte[] readBytes(final InputStream inputStream, long n) throws IOException {
        if (n > 1048576L) {
            throw new IOException("File too large");
        }
        BufferedInputStream bufferedInputStream;
        if (inputStream instanceof BufferedInputStream) {
            bufferedInputStream = (BufferedInputStream)inputStream;
        }
        else {
            bufferedInputStream = new BufferedInputStream(inputStream);
        }
        if (n <= 0L) {
            n = 10240L;
        }
        byte[] array = new byte[(int)n];
        int n2 = 0;
        for (int i = bufferedInputStream.read(array, n2, array.length - n2); i != -1; i = bufferedInputStream.read(array, n2, array.length - n2)) {
            n2 += i;
            if (array.length == n2) {
                final byte[] array2 = new byte[array.length * 2];
                System.arraycopy(array, 0, array2, 0, array.length);
                array = array2;
            }
        }
        bufferedInputStream.close();
        inputStream.close();
        if (n2 != array.length) {
            final byte[] array3 = new byte[n2];
            System.arraycopy(array, 0, array3, 0, n2);
            array = array3;
        }
        return array;
    }
}
