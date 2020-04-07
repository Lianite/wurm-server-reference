// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.impl;

import java.net.UnknownHostException;
import org.fourthline.cling.transport.spi.StreamServerConfiguration;
import javax.servlet.ServletException;
import org.fourthline.cling.transport.spi.UpnpStream;
import org.fourthline.cling.model.message.Connection;
import javax.servlet.AsyncContext;
import org.fourthline.cling.protocol.ProtocolFactory;
import java.io.IOException;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.Servlet;
import org.fourthline.cling.transport.spi.InitializationException;
import java.util.logging.Level;
import org.fourthline.cling.transport.Router;
import java.net.InetAddress;
import java.util.logging.Logger;
import org.fourthline.cling.transport.spi.StreamServer;

public class AsyncServletStreamServerImpl implements StreamServer<AsyncServletStreamServerConfigurationImpl>
{
    private static final Logger log;
    protected final AsyncServletStreamServerConfigurationImpl configuration;
    protected int localPort;
    protected String hostAddress;
    private int mCounter;
    
    public AsyncServletStreamServerImpl(final AsyncServletStreamServerConfigurationImpl configuration) {
        this.mCounter = 0;
        this.configuration = configuration;
    }
    
    @Override
    public AsyncServletStreamServerConfigurationImpl getConfiguration() {
        return this.configuration;
    }
    
    @Override
    public synchronized void init(final InetAddress bindAddress, final Router router) throws InitializationException {
        try {
            if (AsyncServletStreamServerImpl.log.isLoggable(Level.FINE)) {
                AsyncServletStreamServerImpl.log.fine("Setting executor service on servlet container adapter");
            }
            this.getConfiguration().getServletContainerAdapter().setExecutorService(router.getConfiguration().getStreamServerExecutorService());
            if (AsyncServletStreamServerImpl.log.isLoggable(Level.FINE)) {
                AsyncServletStreamServerImpl.log.fine("Adding connector: " + bindAddress + ":" + this.getConfiguration().getListenPort());
            }
            this.hostAddress = bindAddress.getHostAddress();
            this.localPort = this.getConfiguration().getServletContainerAdapter().addConnector(this.hostAddress, this.getConfiguration().getListenPort());
            final String contextPath = router.getConfiguration().getNamespace().getBasePath().getPath();
            this.getConfiguration().getServletContainerAdapter().registerServlet(contextPath, this.createServlet(router));
        }
        catch (Exception ex) {
            throw new InitializationException("Could not initialize " + this.getClass().getSimpleName() + ": " + ex.toString(), ex);
        }
    }
    
    @Override
    public synchronized int getPort() {
        return this.localPort;
    }
    
    @Override
    public synchronized void stop() {
        this.getConfiguration().getServletContainerAdapter().removeConnector(this.hostAddress, this.localPort);
    }
    
    @Override
    public void run() {
        this.getConfiguration().getServletContainerAdapter().startIfNotRunning();
    }
    
    protected Servlet createServlet(final Router router) {
        return new HttpServlet() {
            @Override
            protected void service(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
                final long startTime = System.currentTimeMillis();
                final int counter = AsyncServletStreamServerImpl.this.mCounter++;
                if (AsyncServletStreamServerImpl.log.isLoggable(Level.FINE)) {
                    AsyncServletStreamServerImpl.log.fine(String.format("HttpServlet.service(): id: %3d, request URI: %s", counter, req.getRequestURI()));
                }
                final AsyncContext async = req.startAsync();
                async.setTimeout(AsyncServletStreamServerImpl.this.getConfiguration().getAsyncTimeoutSeconds() * 1000);
                async.addListener(new AsyncListener() {
                    @Override
                    public void onTimeout(final AsyncEvent arg0) throws IOException {
                        final long duration = System.currentTimeMillis() - startTime;
                        if (AsyncServletStreamServerImpl.log.isLoggable(Level.FINE)) {
                            AsyncServletStreamServerImpl.log.fine(String.format("AsyncListener.onTimeout(): id: %3d, duration: %,4d, request: %s", counter, duration, arg0.getSuppliedRequest()));
                        }
                    }
                    
                    @Override
                    public void onStartAsync(final AsyncEvent arg0) throws IOException {
                        if (AsyncServletStreamServerImpl.log.isLoggable(Level.FINE)) {
                            AsyncServletStreamServerImpl.log.fine(String.format("AsyncListener.onStartAsync(): id: %3d, request: %s", counter, arg0.getSuppliedRequest()));
                        }
                    }
                    
                    @Override
                    public void onError(final AsyncEvent arg0) throws IOException {
                        final long duration = System.currentTimeMillis() - startTime;
                        if (AsyncServletStreamServerImpl.log.isLoggable(Level.FINE)) {
                            AsyncServletStreamServerImpl.log.fine(String.format("AsyncListener.onError(): id: %3d, duration: %,4d, response: %s", counter, duration, arg0.getSuppliedResponse()));
                        }
                    }
                    
                    @Override
                    public void onComplete(final AsyncEvent arg0) throws IOException {
                        final long duration = System.currentTimeMillis() - startTime;
                        if (AsyncServletStreamServerImpl.log.isLoggable(Level.FINE)) {
                            AsyncServletStreamServerImpl.log.fine(String.format("AsyncListener.onComplete(): id: %3d, duration: %,4d, response: %s", counter, duration, arg0.getSuppliedResponse()));
                        }
                    }
                });
                final AsyncServletUpnpStream stream = new AsyncServletUpnpStream(router.getProtocolFactory(), async, req) {
                    @Override
                    protected Connection createConnection() {
                        return new AsyncServletConnection(this.getRequest());
                    }
                };
                router.received(stream);
            }
        };
    }
    
    protected boolean isConnectionOpen(final HttpServletRequest request) {
        return true;
    }
    
    static {
        log = Logger.getLogger(StreamServer.class.getName());
    }
    
    protected class AsyncServletConnection implements Connection
    {
        protected HttpServletRequest request;
        
        public AsyncServletConnection(final HttpServletRequest request) {
            this.request = request;
        }
        
        public HttpServletRequest getRequest() {
            return this.request;
        }
        
        @Override
        public boolean isOpen() {
            return AsyncServletStreamServerImpl.this.isConnectionOpen(this.getRequest());
        }
        
        @Override
        public InetAddress getRemoteAddress() {
            try {
                return InetAddress.getByName(this.getRequest().getRemoteAddr());
            }
            catch (UnknownHostException ex) {
                throw new RuntimeException(ex);
            }
        }
        
        @Override
        public InetAddress getLocalAddress() {
            try {
                return InetAddress.getByName(this.getRequest().getLocalAddr());
            }
            catch (UnknownHostException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
