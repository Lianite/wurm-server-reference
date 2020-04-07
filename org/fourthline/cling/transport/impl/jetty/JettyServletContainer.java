// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.impl.jetty;

import org.eclipse.jetty.server.AbstractHttpConnection;
import java.util.logging.Level;
import java.net.Socket;
import org.eclipse.jetty.server.Request;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import javax.servlet.Servlet;
import java.io.IOException;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import java.util.concurrent.ExecutorService;
import org.eclipse.jetty.server.Server;
import java.util.logging.Logger;
import org.fourthline.cling.transport.spi.ServletContainerAdapter;

public class JettyServletContainer implements ServletContainerAdapter
{
    private static final Logger log;
    public static final JettyServletContainer INSTANCE;
    protected Server server;
    
    private JettyServletContainer() {
        this.resetServer();
    }
    
    @Override
    public synchronized void setExecutorService(final ExecutorService executorService) {
        if (JettyServletContainer.INSTANCE.server.getThreadPool() == null) {
            JettyServletContainer.INSTANCE.server.setThreadPool((ThreadPool)new ExecutorThreadPool(executorService) {
                protected void doStop() throws Exception {
                }
            });
        }
    }
    
    @Override
    public synchronized int addConnector(final String host, final int port) throws IOException {
        final SocketConnector connector = new SocketConnector();
        connector.setHost(host);
        connector.setPort(port);
        connector.open();
        this.server.addConnector((Connector)connector);
        if (this.server.isStarted()) {
            try {
                connector.start();
            }
            catch (Exception ex) {
                JettyServletContainer.log.severe("Couldn't start connector: " + connector + " " + ex);
                throw new RuntimeException(ex);
            }
        }
        return connector.getLocalPort();
    }
    
    @Override
    public synchronized void removeConnector(final String host, final int port) {
        final Connector[] connectors2;
        final Connector[] connectors = connectors2 = this.server.getConnectors();
        final int length = connectors2.length;
        int i = 0;
        while (i < length) {
            final Connector connector = connectors2[i];
            if (connector.getHost().equals(host) && connector.getLocalPort() == port) {
                Label_0136: {
                    if (!connector.isStarted()) {
                        if (!connector.isStarting()) {
                            break Label_0136;
                        }
                    }
                    try {
                        connector.stop();
                    }
                    catch (Exception ex) {
                        JettyServletContainer.log.severe("Couldn't stop connector: " + connector + " " + ex);
                        throw new RuntimeException(ex);
                    }
                }
                this.server.removeConnector(connector);
                if (connectors.length == 1) {
                    JettyServletContainer.log.info("No more connectors, stopping Jetty server");
                    this.stopIfRunning();
                    break;
                }
                break;
            }
            else {
                ++i;
            }
        }
    }
    
    @Override
    public synchronized void registerServlet(final String contextPath, final Servlet servlet) {
        if (this.server.getHandler() != null) {
            return;
        }
        JettyServletContainer.log.info("Registering UPnP servlet under context path: " + contextPath);
        final ServletContextHandler servletHandler = new ServletContextHandler(0);
        if (contextPath != null && contextPath.length() > 0) {
            servletHandler.setContextPath(contextPath);
        }
        final ServletHolder s = new ServletHolder(servlet);
        servletHandler.addServlet(s, "/*");
        this.server.setHandler((Handler)servletHandler);
    }
    
    @Override
    public synchronized void startIfNotRunning() {
        if (!this.server.isStarted() && !this.server.isStarting()) {
            JettyServletContainer.log.info("Starting Jetty server... ");
            try {
                this.server.start();
            }
            catch (Exception ex) {
                JettyServletContainer.log.severe("Couldn't start Jetty server: " + ex);
                throw new RuntimeException(ex);
            }
        }
    }
    
    @Override
    public synchronized void stopIfRunning() {
        if (!this.server.isStopped() && !this.server.isStopping()) {
            JettyServletContainer.log.info("Stopping Jetty server...");
            try {
                this.server.stop();
            }
            catch (Exception ex) {
                JettyServletContainer.log.severe("Couldn't stop Jetty server: " + ex);
                throw new RuntimeException(ex);
            }
            finally {
                this.resetServer();
            }
        }
    }
    
    protected void resetServer() {
        (this.server = new Server()).setGracefulShutdown(1000);
    }
    
    public static boolean isConnectionOpen(final HttpServletRequest request) {
        return isConnectionOpen(request, " ".getBytes());
    }
    
    public static boolean isConnectionOpen(final HttpServletRequest request, final byte[] heartbeat) {
        final Request jettyRequest = (Request)request;
        final AbstractHttpConnection connection = jettyRequest.getConnection();
        final Socket socket = (Socket)connection.getEndPoint().getTransport();
        if (JettyServletContainer.log.isLoggable(Level.FINE)) {
            JettyServletContainer.log.fine("Checking if client connection is still open: " + socket.getRemoteSocketAddress());
        }
        try {
            socket.getOutputStream().write(heartbeat);
            socket.getOutputStream().flush();
            return true;
        }
        catch (IOException ex) {
            if (JettyServletContainer.log.isLoggable(Level.FINE)) {
                JettyServletContainer.log.fine("Client connection has been closed: " + socket.getRemoteSocketAddress());
            }
            return false;
        }
    }
    
    static {
        log = Logger.getLogger(JettyServletContainer.class.getName());
        INSTANCE = new JettyServletContainer();
    }
}
