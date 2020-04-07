// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.impl;

import java.net.ProtocolException;
import java.io.OutputStream;
import sun.net.www.protocol.http.HttpURLConnection;
import java.io.IOException;
import java.net.Proxy;
import java.net.URLConnection;
import java.net.URL;
import sun.net.www.protocol.http.Handler;
import java.net.URLStreamHandler;
import java.util.logging.Logger;
import java.net.URLStreamHandlerFactory;

public class FixedSunURLStreamHandler implements URLStreamHandlerFactory
{
    private static final Logger log;
    
    @Override
    public URLStreamHandler createURLStreamHandler(final String protocol) {
        FixedSunURLStreamHandler.log.fine("Creating new URLStreamHandler for protocol: " + protocol);
        if ("http".equals(protocol)) {
            return new Handler() {
                @Override
                protected URLConnection openConnection(final URL u) throws IOException {
                    return this.openConnection(u, null);
                }
                
                @Override
                protected URLConnection openConnection(final URL u, final Proxy p) throws IOException {
                    return new UpnpURLConnection(u, this);
                }
            };
        }
        return null;
    }
    
    static {
        log = Logger.getLogger(FixedSunURLStreamHandler.class.getName());
    }
    
    static class UpnpURLConnection extends HttpURLConnection
    {
        private static final String[] methods;
        
        protected UpnpURLConnection(final URL u, final Handler handler) throws IOException {
            super(u, handler);
        }
        
        public UpnpURLConnection(final URL u, final String host, final int port) throws IOException {
            super(u, host, port);
        }
        
        @Override
        public synchronized OutputStream getOutputStream() throws IOException {
            final String savedMethod = this.method;
            if (this.method.equals("PUT") || this.method.equals("POST") || this.method.equals("NOTIFY")) {
                this.method = "PUT";
            }
            else {
                this.method = "GET";
            }
            final OutputStream os = super.getOutputStream();
            this.method = savedMethod;
            return os;
        }
        
        @Override
        public void setRequestMethod(final String method) throws ProtocolException {
            if (this.connected) {
                throw new ProtocolException("Cannot reset method once connected");
            }
            for (final String m : UpnpURLConnection.methods) {
                if (m.equals(method)) {
                    this.method = method;
                    return;
                }
            }
            throw new ProtocolException("Invalid UPnP HTTP method: " + method);
        }
        
        static {
            methods = new String[] { "GET", "POST", "HEAD", "OPTIONS", "PUT", "DELETE", "SUBSCRIBE", "UNSUBSCRIBE", "NOTIFY" };
        }
    }
}
