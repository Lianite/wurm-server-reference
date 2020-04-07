// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.net;

import java.net.MalformedURLException;
import com.sun.javaws.Globals;
import java.net.URLConnection;
import java.util.Map;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.io.BufferedInputStream;
import java.util.HashMap;
import java.net.HttpURLConnection;
import com.sun.deploy.util.Trace;
import com.sun.deploy.util.TraceLevel;
import java.util.Date;
import java.io.File;
import com.sun.deploy.util.URLUtil;
import java.io.IOException;
import java.net.URL;

public class BasicNetworkLayer implements HttpRequest
{
    private static final String USER_AGENT_JAVA_VERSION = "UA-Java-Version";
    private static final String USER_AGENT = "User-Agent";
    
    public HttpResponse doGetRequest(final URL url) throws IOException {
        return this.doRequest(url, false, null, null, true);
    }
    
    public HttpResponse doGetRequest(final URL url, final boolean b) throws IOException {
        return this.doRequest(url, false, null, null, b);
    }
    
    public HttpResponse doHeadRequest(final URL url) throws IOException {
        return this.doRequest(url, true, null, null, true);
    }
    
    public HttpResponse doHeadRequest(final URL url, final boolean b) throws IOException {
        return this.doRequest(url, true, null, null, b);
    }
    
    public HttpResponse doGetRequest(final URL url, final String[] array, final String[] array2) throws IOException {
        return this.doRequest(url, false, array, array2, true);
    }
    
    public HttpResponse doGetRequest(final URL url, final String[] array, final String[] array2, final boolean b) throws IOException {
        return this.doRequest(url, false, array, array2, b);
    }
    
    public HttpResponse doHeadRequest(final URL url, final String[] array, final String[] array2) throws IOException {
        return this.doRequest(url, true, array, array2, true);
    }
    
    public HttpResponse doHeadRequest(final URL url, final String[] array, final String[] array2, final boolean b) throws IOException {
        return this.doRequest(url, true, array, array2, b);
    }
    
    private HttpResponse doRequest(final URL url, final boolean b, final String[] array, final String[] array2, final boolean b2) throws IOException {
        long lastModified = 0L;
        String s = null;
        if ("file".equals(url.getProtocol()) && url.getFile() != null) {
            try {
                final String pathFromURL = URLUtil.getPathFromURL(url);
                lastModified = new File(pathFromURL).lastModified();
                Trace.println("File URL discovered. Real timestamp: " + new Date(lastModified), TraceLevel.NETWORK);
                if (pathFromURL.endsWith(".jnlp")) {
                    s = "application/x-java-jnlp-file";
                }
                else if (pathFromURL.endsWith(".jardiff")) {
                    s = "application/x-java-archive-diff";
                }
            }
            catch (Exception ex) {}
        }
        URLConnection hostHeader;
        if (url.getProtocol().equals("file")) {
            hostHeader = this.createUrlConnection(new URL(url.getProtocol(), url.getHost(), url.getPath()), b, array, array2, b2);
        }
        else {
            hostHeader = this.createUrlConnection(url, b, array, array2, b2);
        }
        HttpURLConnection httpURLConnection = null;
        if (hostHeader instanceof HttpURLConnection) {
            httpURLConnection = (HttpURLConnection)hostHeader;
        }
        URLUtil.setHostHeader(hostHeader);
        hostHeader.connect();
        int responseCode = 200;
        if (httpURLConnection != null) {
            responseCode = httpURLConnection.getResponseCode();
        }
        final int contentLength = hostHeader.getContentLength();
        final long n = (lastModified != 0L) ? lastModified : hostHeader.getLastModified();
        String trim = (s != null) ? s : hostHeader.getContentType();
        if (trim != null && trim.indexOf(59) != -1) {
            trim = trim.substring(0, trim.indexOf(59)).trim();
        }
        final HashMap<String, String> hashMap = new HashMap<String, String>();
        int n2 = 1;
        for (String s2 = hostHeader.getHeaderFieldKey(n2); s2 != null; s2 = hostHeader.getHeaderFieldKey(n2)) {
            hashMap.put(s2.toLowerCase(), hostHeader.getHeaderField(n2));
            ++n2;
        }
        String lowerCase = hashMap.get("content-encoding");
        if (lowerCase != null) {
            lowerCase = lowerCase.toLowerCase();
        }
        Trace.println("encoding = " + lowerCase + " for " + url.toString(), TraceLevel.NETWORK);
        BufferedInputStream bufferedInputStream;
        if (b) {
            bufferedInputStream = null;
        }
        else {
            final BufferedInputStream bufferedInputStream2 = new BufferedInputStream(hostHeader.getInputStream());
            if (lowerCase != null && (lowerCase.compareTo("pack200-gzip") == 0 || lowerCase.compareTo("gzip") == 0)) {
                bufferedInputStream = new BufferedInputStream(new GZIPInputStream(bufferedInputStream2));
            }
            else {
                bufferedInputStream = new BufferedInputStream(bufferedInputStream2);
            }
        }
        return new BasicHttpResponse(url, responseCode, contentLength, n, trim, hashMap, bufferedInputStream, httpURLConnection, lowerCase);
    }
    
    private URLConnection createUrlConnection(final URL url, final boolean b, final String[] array, final String[] array2, final boolean b2) throws MalformedURLException, IOException {
        final URLConnection openConnection = url.openConnection();
        this.addToRequestProperty(openConnection, "pragma", "no-cache");
        if (b2 && url.getPath().endsWith(".jar")) {
            final String s = Globals.havePack200() ? "pack200-gzip,gzip" : "gzip";
            this.addToRequestProperty(openConnection, "accept-encoding", s);
            this.addToRequestProperty(openConnection, "content-type", "application/x-java-archive");
            Trace.println("Requesting file " + url.getFile() + " with Encoding = " + s, TraceLevel.NETWORK);
        }
        if (System.getProperty("http.agent") == null) {
            openConnection.setRequestProperty("User-Agent", Globals.getUserAgent());
            openConnection.setRequestProperty("UA-Java-Version", Globals.getJavaVersion());
        }
        if (array != null && array2 != null) {
            for (int i = 0; i < array.length; ++i) {
                openConnection.setRequestProperty(array[i], array2[i]);
            }
        }
        if (openConnection instanceof HttpURLConnection) {
            ((HttpURLConnection)openConnection).setRequestMethod(b ? "HEAD" : "GET");
        }
        return openConnection;
    }
    
    private void addToRequestProperty(final URLConnection urlConnection, final String s, final String s2) {
        final String requestProperty = urlConnection.getRequestProperty(s);
        String string;
        if (requestProperty == null || requestProperty.trim().length() == 0) {
            string = s2;
        }
        else {
            string = requestProperty + "," + s2;
        }
        urlConnection.setRequestProperty(s, string);
    }
    
    static class BasicHttpResponse implements HttpResponse
    {
        private URL _request;
        private int _status;
        private int _length;
        private long _lastModified;
        private String _mimeType;
        private Map _headers;
        private BufferedInputStream _bis;
        private HttpURLConnection _httpURLConnection;
        private String _contentEncoding;
        
        BasicHttpResponse(final URL request, final int status, final int length, final long lastModified, final String mimeType, final Map headers, final BufferedInputStream bis, final HttpURLConnection httpURLConnection, final String contentEncoding) {
            this._request = request;
            this._status = status;
            this._length = length;
            this._lastModified = lastModified;
            this._mimeType = mimeType;
            this._headers = headers;
            this._bis = bis;
            this._httpURLConnection = httpURLConnection;
            this._contentEncoding = contentEncoding;
        }
        
        public void disconnect() {
            if (this._httpURLConnection != null) {
                this._httpURLConnection.disconnect();
                Trace.println("Disconnect connection to " + this._request, TraceLevel.NETWORK);
            }
        }
        
        public URL getRequest() {
            return this._request;
        }
        
        public int getStatusCode() {
            return this._status;
        }
        
        public int getContentLength() {
            return this._length;
        }
        
        public long getLastModified() {
            return this._lastModified;
        }
        
        public String getContentType() {
            return this._mimeType;
        }
        
        public String getContentEncoding() {
            return this._contentEncoding;
        }
        
        public String getResponseHeader(final String s) {
            return this._headers.get(s.toLowerCase());
        }
        
        public BufferedInputStream getInputStream() {
            return this._bis;
        }
    }
}
