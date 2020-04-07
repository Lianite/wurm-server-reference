// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.net;

import java.net.URL;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarOutputStream;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.jar.Pack200;
import com.sun.javaws.Globals;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import com.sun.deploy.util.Trace;
import com.sun.deploy.util.TraceLevel;
import java.io.File;

public class BasicDownloadLayer implements HttpDownload
{
    private static final int BUF_SIZE = 32768;
    private HttpRequest _httpRequest;
    
    public BasicDownloadLayer(final HttpRequest httpRequest) {
        this._httpRequest = httpRequest;
    }
    
    public void download(final HttpResponse httpResponse, final File file, final HttpDownloadListener httpDownloadListener) throws CanceledDownloadException, IOException {
        final int contentLength = httpResponse.getContentLength();
        if (httpDownloadListener != null) {
            httpDownloadListener.downloadProgress(0, contentLength);
        }
        Trace.println("Doing download", TraceLevel.NETWORK);
        BufferedInputStream inputStream = httpResponse.getInputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
        final String contentEncoding = httpResponse.getContentEncoding();
        try {
            if (contentEncoding != null && contentEncoding.compareTo("pack200-gzip") == 0 && Globals.havePack200()) {
                Trace.println("download:encoding Pack200: = " + contentEncoding, TraceLevel.NETWORK);
                final Pack200.Unpacker unpacker = Pack200.newUnpacker();
                unpacker.addPropertyChangeListener(new PropertyChangeListener() {
                    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
                        if (httpDownloadListener != null && propertyChangeEvent.getPropertyName().compareTo("unpack.progress") == 0) {
                            final String s = (String)propertyChangeEvent.getNewValue();
                            httpDownloadListener.downloadProgress(((s != null) ? Integer.parseInt(s) : 0) * contentLength / 100, contentLength);
                        }
                    }
                });
                final JarOutputStream jarOutputStream = new JarOutputStream(bufferedOutputStream);
                unpacker.unpack(inputStream, jarOutputStream);
                jarOutputStream.close();
            }
            else {
                Trace.println("download:encoding GZIP/Plain = " + contentEncoding, TraceLevel.NETWORK);
                int n = 0;
                final byte[] array = new byte[32768];
                int read;
                while ((read = inputStream.read(array)) != -1) {
                    bufferedOutputStream.write(array, 0, read);
                    n += read;
                    if (n > contentLength && contentLength != 0) {
                        n = contentLength;
                    }
                    if (httpDownloadListener != null) {
                        httpDownloadListener.downloadProgress(n, contentLength);
                    }
                }
            }
            Trace.println("Wrote URL " + httpResponse.getRequest() + " to file " + file, TraceLevel.NETWORK);
            inputStream.close();
            inputStream = null;
            bufferedOutputStream.close();
            bufferedOutputStream = null;
        }
        catch (IOException ex) {
            Trace.println("Got exception while downloading resource: " + ex, TraceLevel.NETWORK);
            if (inputStream != null) {
                inputStream.close();
            }
            if (bufferedOutputStream != null) {
                bufferedOutputStream.close();
            }
            if (file != null) {
                file.delete();
            }
            throw ex;
        }
        if (httpDownloadListener != null) {
            httpDownloadListener.downloadProgress(contentLength, contentLength);
        }
    }
    
    public void download(final URL url, final File file, final HttpDownloadListener httpDownloadListener) throws CanceledDownloadException, IOException {
        final HttpResponse doGetRequest = this._httpRequest.doGetRequest(url);
        this.download(doGetRequest, file, httpDownloadListener);
        doGetRequest.disconnect();
    }
    
    class PropertyChangeListenerTask implements PropertyChangeListener
    {
        HttpDownloadListener _dl;
        
        PropertyChangeListenerTask(final HttpDownloadListener dl) {
            this._dl = null;
            this._dl = dl;
        }
        
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            if (propertyChangeEvent.getPropertyName().compareTo("unpack.progress") == 0) {
                final String s = (String)propertyChangeEvent.getNewValue();
                if (this._dl != null && s != null) {
                    this._dl.downloadProgress(Integer.parseInt(s), 100);
                }
            }
        }
    }
}
