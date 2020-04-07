// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.impl;

import java.io.IOException;
import org.fourthline.cling.transport.spi.UpnpStream;
import org.fourthline.cling.model.message.Connection;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.transport.spi.StreamServerConfiguration;
import com.sun.net.httpserver.HttpExchange;
import org.fourthline.cling.transport.spi.InitializationException;
import com.sun.net.httpserver.HttpHandler;
import java.net.InetSocketAddress;
import org.fourthline.cling.transport.Router;
import java.net.InetAddress;
import com.sun.net.httpserver.HttpServer;
import java.util.logging.Logger;
import org.fourthline.cling.transport.spi.StreamServer;

public class StreamServerImpl implements StreamServer<StreamServerConfigurationImpl>
{
    private static Logger log;
    protected final StreamServerConfigurationImpl configuration;
    protected HttpServer server;
    
    public StreamServerImpl(final StreamServerConfigurationImpl configuration) {
        this.configuration = configuration;
    }
    
    @Override
    public synchronized void init(final InetAddress bindAddress, final Router router) throws InitializationException {
        try {
            final InetSocketAddress socketAddress = new InetSocketAddress(bindAddress, this.configuration.getListenPort());
            (this.server = HttpServer.create(socketAddress, this.configuration.getTcpConnectionBacklog())).createContext("/", new RequestHttpHandler(router));
            StreamServerImpl.log.info("Created server (for receiving TCP streams) on: " + this.server.getAddress());
        }
        catch (Exception ex) {
            throw new InitializationException("Could not initialize " + this.getClass().getSimpleName() + ": " + ex.toString(), ex);
        }
    }
    
    @Override
    public synchronized int getPort() {
        return this.server.getAddress().getPort();
    }
    
    @Override
    public StreamServerConfigurationImpl getConfiguration() {
        return this.configuration;
    }
    
    @Override
    public synchronized void run() {
        StreamServerImpl.log.fine("Starting StreamServer...");
        this.server.start();
    }
    
    @Override
    public synchronized void stop() {
        StreamServerImpl.log.fine("Stopping StreamServer...");
        if (this.server != null) {
            this.server.stop(1);
        }
    }
    
    protected boolean isConnectionOpen(final HttpExchange exchange) {
        StreamServerImpl.log.warning("Can't check client connection, socket access impossible on JDK webserver!");
        return true;
    }
    
    static {
        StreamServerImpl.log = Logger.getLogger(StreamServer.class.getName());
    }
    
    protected class RequestHttpHandler implements HttpHandler
    {
        private final Router router;
        
        public RequestHttpHandler(final Router router) {
            this.router = router;
        }
        
        @Override
        public void handle(final HttpExchange httpExchange) throws IOException {
            StreamServerImpl.log.fine("Received HTTP exchange: " + httpExchange.getRequestMethod() + " " + httpExchange.getRequestURI());
            this.router.received(new HttpExchangeUpnpStream(this.router.getProtocolFactory(), httpExchange) {
                @Override
                protected Connection createConnection() {
                    return new HttpServerConnection(httpExchange);
                }
            });
        }
    }
    
    protected class HttpServerConnection implements Connection
    {
        protected HttpExchange exchange;
        
        public HttpServerConnection(final HttpExchange exchange) {
            this.exchange = exchange;
        }
        
        @Override
        public boolean isOpen() {
            return StreamServerImpl.this.isConnectionOpen(this.exchange);
        }
        
        @Override
        public InetAddress getRemoteAddress() {
            return (this.exchange.getRemoteAddress() != null) ? this.exchange.getRemoteAddress().getAddress() : null;
        }
        
        @Override
        public InetAddress getLocalAddress() {
            return (this.exchange.getLocalAddress() != null) ? this.exchange.getLocalAddress().getAddress() : null;
        }
    }
}
