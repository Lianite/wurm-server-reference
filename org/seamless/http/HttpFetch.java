// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.http;

import java.net.HttpURLConnection;
import org.seamless.util.io.IO;
import java.io.InputStream;
import java.net.URLConnection;
import java.io.IOException;
import java.net.URL;

public class HttpFetch
{
    public static Representation<byte[]> fetchBinary(final URL url) throws IOException {
        return fetchBinary(url, 500, 500);
    }
    
    public static Representation<byte[]> fetchBinary(final URL url, final int connectTimeoutMillis, final int readTimeoutMillis) throws IOException {
        return fetch(url, connectTimeoutMillis, readTimeoutMillis, (RepresentationFactory<byte[]>)new RepresentationFactory<byte[]>() {
            public Representation<byte[]> createRepresentation(final URLConnection urlConnection, final InputStream is) throws IOException {
                return new Representation<byte[]>(urlConnection, IO.readBytes(is));
            }
        });
    }
    
    public static Representation<String> fetchString(final URL url, final int connectTimeoutMillis, final int readTimeoutMillis) throws IOException {
        return fetch(url, connectTimeoutMillis, readTimeoutMillis, (RepresentationFactory<String>)new RepresentationFactory<String>() {
            public Representation<String> createRepresentation(final URLConnection urlConnection, final InputStream is) throws IOException {
                return new Representation<String>(urlConnection, IO.readLines(is));
            }
        });
    }
    
    public static <E> Representation<E> fetch(final URL url, final int connectTimeoutMillis, final int readTimeoutMillis, final RepresentationFactory<E> factory) throws IOException {
        return fetch(url, "GET", connectTimeoutMillis, readTimeoutMillis, factory);
    }
    
    public static <E> Representation<E> fetch(final URL url, final String method, final int connectTimeoutMillis, final int readTimeoutMillis, final RepresentationFactory<E> factory) throws IOException {
        HttpURLConnection urlConnection = null;
        InputStream is = null;
        try {
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod(method);
            urlConnection.setConnectTimeout(connectTimeoutMillis);
            urlConnection.setReadTimeout(readTimeoutMillis);
            is = urlConnection.getInputStream();
            return factory.createRepresentation(urlConnection, is);
        }
        catch (IOException ex) {
            if (urlConnection != null) {
                final int responseCode = urlConnection.getResponseCode();
                throw new IOException("Fetching resource failed, returned status code: " + responseCode);
            }
            throw ex;
        }
        finally {
            if (is != null) {
                is.close();
            }
        }
    }
    
    public static void validate(final URL url) throws IOException {
        fetch(url, "HEAD", 500, 500, (RepresentationFactory<Object>)new RepresentationFactory() {
            public Representation createRepresentation(final URLConnection urlConnection, final InputStream is) throws IOException {
                return new Representation(urlConnection, null);
            }
        });
    }
    
    public interface RepresentationFactory<E>
    {
        Representation<E> createRepresentation(final URLConnection p0, final InputStream p1) throws IOException;
    }
}
