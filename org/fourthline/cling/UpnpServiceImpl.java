// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling;

import java.util.logging.Level;
import org.seamless.util.Exceptions;
import org.fourthline.cling.controlpoint.ControlPointImpl;
import org.fourthline.cling.transport.RouterImpl;
import org.fourthline.cling.registry.RegistryImpl;
import org.fourthline.cling.protocol.ProtocolFactoryImpl;
import org.fourthline.cling.transport.RouterException;
import org.fourthline.cling.registry.RegistryListener;
import org.fourthline.cling.transport.Router;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.controlpoint.ControlPoint;
import java.util.logging.Logger;
import javax.enterprise.inject.Alternative;

@Alternative
public class UpnpServiceImpl implements UpnpService
{
    private static Logger log;
    protected final UpnpServiceConfiguration configuration;
    protected final ControlPoint controlPoint;
    protected final ProtocolFactory protocolFactory;
    protected final Registry registry;
    protected final Router router;
    
    public UpnpServiceImpl() {
        this(new DefaultUpnpServiceConfiguration(), new RegistryListener[0]);
    }
    
    public UpnpServiceImpl(final RegistryListener... registryListeners) {
        this(new DefaultUpnpServiceConfiguration(), registryListeners);
    }
    
    public UpnpServiceImpl(final UpnpServiceConfiguration configuration, final RegistryListener... registryListeners) {
        this.configuration = configuration;
        UpnpServiceImpl.log.info(">>> Starting UPnP service...");
        UpnpServiceImpl.log.info("Using configuration: " + this.getConfiguration().getClass().getName());
        this.protocolFactory = this.createProtocolFactory();
        this.registry = this.createRegistry(this.protocolFactory);
        for (final RegistryListener registryListener : registryListeners) {
            this.registry.addListener(registryListener);
        }
        this.router = this.createRouter(this.protocolFactory, this.registry);
        try {
            this.router.enable();
        }
        catch (RouterException ex) {
            throw new RuntimeException("Enabling network router failed: " + ex, ex);
        }
        this.controlPoint = this.createControlPoint(this.protocolFactory, this.registry);
        UpnpServiceImpl.log.info("<<< UPnP service started successfully");
    }
    
    protected ProtocolFactory createProtocolFactory() {
        return new ProtocolFactoryImpl(this);
    }
    
    protected Registry createRegistry(final ProtocolFactory protocolFactory) {
        return new RegistryImpl(this);
    }
    
    protected Router createRouter(final ProtocolFactory protocolFactory, final Registry registry) {
        return new RouterImpl(this.getConfiguration(), protocolFactory);
    }
    
    protected ControlPoint createControlPoint(final ProtocolFactory protocolFactory, final Registry registry) {
        return new ControlPointImpl(this.getConfiguration(), protocolFactory, registry);
    }
    
    @Override
    public UpnpServiceConfiguration getConfiguration() {
        return this.configuration;
    }
    
    @Override
    public ControlPoint getControlPoint() {
        return this.controlPoint;
    }
    
    @Override
    public ProtocolFactory getProtocolFactory() {
        return this.protocolFactory;
    }
    
    @Override
    public Registry getRegistry() {
        return this.registry;
    }
    
    @Override
    public Router getRouter() {
        return this.router;
    }
    
    @Override
    public synchronized void shutdown() {
        this.shutdown(false);
    }
    
    protected void shutdown(final boolean separateThread) {
        final Runnable shutdown = new Runnable() {
            @Override
            public void run() {
                UpnpServiceImpl.log.info(">>> Shutting down UPnP service...");
                UpnpServiceImpl.this.shutdownRegistry();
                UpnpServiceImpl.this.shutdownRouter();
                UpnpServiceImpl.this.shutdownConfiguration();
                UpnpServiceImpl.log.info("<<< UPnP service shutdown completed");
            }
        };
        if (separateThread) {
            new Thread(shutdown).start();
        }
        else {
            shutdown.run();
        }
    }
    
    protected void shutdownRegistry() {
        this.getRegistry().shutdown();
    }
    
    protected void shutdownRouter() {
        try {
            this.getRouter().shutdown();
        }
        catch (RouterException ex) {
            final Throwable cause = Exceptions.unwrap(ex);
            if (cause instanceof InterruptedException) {
                UpnpServiceImpl.log.log(Level.INFO, "Router shutdown was interrupted: " + ex, cause);
            }
            else {
                UpnpServiceImpl.log.log(Level.SEVERE, "Router error on shutdown: " + ex, cause);
            }
        }
    }
    
    protected void shutdownConfiguration() {
        this.getConfiguration().shutdown();
    }
    
    static {
        UpnpServiceImpl.log = Logger.getLogger(UpnpServiceImpl.class.getName());
    }
}
