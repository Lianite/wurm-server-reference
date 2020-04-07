// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling;

import org.fourthline.cling.registry.event.After;
import org.fourthline.cling.registry.event.Before;
import javax.enterprise.util.AnnotationLiteral;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.registry.event.Phase;
import java.lang.annotation.Annotation;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.registry.event.RegistryShutdown;
import org.fourthline.cling.registry.event.LocalDeviceDiscovery;
import org.fourthline.cling.registry.event.FailedRemoteDeviceDiscovery;
import javax.enterprise.inject.Any;
import org.fourthline.cling.registry.event.RemoteDeviceDiscovery;
import org.fourthline.cling.registry.RegistryListener;
import javax.enterprise.event.Observes;
import org.fourthline.cling.transport.DisableRouter;
import org.fourthline.cling.transport.EnableRouter;
import javax.enterprise.event.Event;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.transport.Router;
import org.fourthline.cling.registry.Registry;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ManagedUpnpService implements UpnpService
{
    private static final Logger log;
    @Inject
    RegistryListenerAdapter registryListenerAdapter;
    @Inject
    Instance<UpnpServiceConfiguration> configuration;
    @Inject
    Instance<Registry> registryInstance;
    @Inject
    Instance<Router> routerInstance;
    @Inject
    Instance<ProtocolFactory> protocolFactoryInstance;
    @Inject
    Instance<ControlPoint> controlPointInstance;
    @Inject
    Event<EnableRouter> enableRouterEvent;
    @Inject
    Event<DisableRouter> disableRouterEvent;
    
    @Override
    public UpnpServiceConfiguration getConfiguration() {
        return (UpnpServiceConfiguration)this.configuration.get();
    }
    
    @Override
    public ControlPoint getControlPoint() {
        return (ControlPoint)this.controlPointInstance.get();
    }
    
    @Override
    public ProtocolFactory getProtocolFactory() {
        return (ProtocolFactory)this.protocolFactoryInstance.get();
    }
    
    @Override
    public Registry getRegistry() {
        return (Registry)this.registryInstance.get();
    }
    
    @Override
    public Router getRouter() {
        return (Router)this.routerInstance.get();
    }
    
    public void start(@Observes final Start start) {
        ManagedUpnpService.log.info(">>> Starting managed UPnP service...");
        this.getRegistry().addListener(this.registryListenerAdapter);
        this.enableRouterEvent.fire((Object)new EnableRouter());
        ManagedUpnpService.log.info("<<< Managed UPnP service started successfully");
    }
    
    @Override
    public void shutdown() {
        this.shutdown(null);
    }
    
    public void shutdown(@Observes final Shutdown shutdown) {
        ManagedUpnpService.log.info(">>> Shutting down managed UPnP service...");
        this.getRegistry().shutdown();
        this.disableRouterEvent.fire((Object)new DisableRouter());
        this.getConfiguration().shutdown();
        ManagedUpnpService.log.info("<<< Managed UPnP service shutdown completed");
    }
    
    static {
        log = Logger.getLogger(ManagedUpnpService.class.getName());
    }
    
    @ApplicationScoped
    static class RegistryListenerAdapter implements RegistryListener
    {
        @Inject
        @Any
        Event<RemoteDeviceDiscovery> remoteDeviceDiscoveryEvent;
        @Inject
        @Any
        Event<FailedRemoteDeviceDiscovery> failedRemoteDeviceDiscoveryEvent;
        @Inject
        @Any
        Event<LocalDeviceDiscovery> localDeviceDiscoveryEvent;
        @Inject
        @Any
        Event<RegistryShutdown> registryShutdownEvent;
        
        @Override
        public void remoteDeviceDiscoveryStarted(final Registry registry, final RemoteDevice device) {
            this.remoteDeviceDiscoveryEvent.select(new Annotation[] { Phase.ALIVE }).fire((Object)new RemoteDeviceDiscovery(device));
        }
        
        @Override
        public void remoteDeviceDiscoveryFailed(final Registry registry, final RemoteDevice device, final Exception ex) {
            this.failedRemoteDeviceDiscoveryEvent.fire((Object)new FailedRemoteDeviceDiscovery(device, ex));
        }
        
        @Override
        public void remoteDeviceAdded(final Registry registry, final RemoteDevice device) {
            this.remoteDeviceDiscoveryEvent.select(new Annotation[] { Phase.COMPLETE }).fire((Object)new RemoteDeviceDiscovery(device));
        }
        
        @Override
        public void remoteDeviceUpdated(final Registry registry, final RemoteDevice device) {
            this.remoteDeviceDiscoveryEvent.select(new Annotation[] { Phase.UPDATED }).fire((Object)new RemoteDeviceDiscovery(device));
        }
        
        @Override
        public void remoteDeviceRemoved(final Registry registry, final RemoteDevice device) {
            this.remoteDeviceDiscoveryEvent.select(new Annotation[] { Phase.BYEBYE }).fire((Object)new RemoteDeviceDiscovery(device));
        }
        
        @Override
        public void localDeviceAdded(final Registry registry, final LocalDevice device) {
            this.localDeviceDiscoveryEvent.select(new Annotation[] { Phase.COMPLETE }).fire((Object)new LocalDeviceDiscovery(device));
        }
        
        @Override
        public void localDeviceRemoved(final Registry registry, final LocalDevice device) {
            this.localDeviceDiscoveryEvent.select(new Annotation[] { Phase.BYEBYE }).fire((Object)new LocalDeviceDiscovery(device));
        }
        
        @Override
        public void beforeShutdown(final Registry registry) {
            this.registryShutdownEvent.select(new Annotation[] { new AnnotationLiteral<Before>() {} }).fire((Object)new RegistryShutdown());
        }
        
        @Override
        public void afterShutdown() {
            this.registryShutdownEvent.select(new Annotation[] { new AnnotationLiteral<After>() {} }).fire((Object)new RegistryShutdown());
        }
    }
}
