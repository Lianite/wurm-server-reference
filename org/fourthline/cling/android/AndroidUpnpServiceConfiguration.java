// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.android;

import org.fourthline.cling.transport.impl.RecoveringGENAEventProcessorImpl;
import org.fourthline.cling.transport.spi.GENAEventProcessor;
import org.fourthline.cling.transport.impl.RecoveringSOAPActionProcessorImpl;
import org.fourthline.cling.transport.spi.SOAPActionProcessor;
import org.fourthline.cling.binding.xml.UDA10ServiceDescriptorBinderSAXImpl;
import org.fourthline.cling.binding.xml.ServiceDescriptorBinder;
import org.fourthline.cling.binding.xml.RecoveringUDA10DeviceDescriptorBinderImpl;
import org.fourthline.cling.binding.xml.DeviceDescriptorBinder;
import org.fourthline.cling.transport.impl.AsyncServletStreamServerImpl;
import org.fourthline.cling.transport.spi.ServletContainerAdapter;
import org.fourthline.cling.transport.impl.AsyncServletStreamServerConfigurationImpl;
import org.fourthline.cling.transport.impl.jetty.JettyServletContainer;
import org.fourthline.cling.transport.spi.StreamServer;
import org.fourthline.cling.transport.impl.jetty.StreamClientImpl;
import android.os.Build;
import org.fourthline.cling.model.ServerClientTokens;
import java.util.concurrent.ExecutorService;
import org.fourthline.cling.transport.impl.jetty.StreamClientConfigurationImpl;
import org.fourthline.cling.transport.spi.StreamClient;
import org.fourthline.cling.model.Namespace;
import org.fourthline.cling.transport.spi.NetworkAddressFactory;
import org.fourthline.cling.DefaultUpnpServiceConfiguration;

public class AndroidUpnpServiceConfiguration extends DefaultUpnpServiceConfiguration
{
    public AndroidUpnpServiceConfiguration() {
        this(0);
    }
    
    public AndroidUpnpServiceConfiguration(final int streamListenPort) {
        super(streamListenPort, false);
        System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");
    }
    
    @Override
    protected NetworkAddressFactory createNetworkAddressFactory(final int streamListenPort) {
        return new AndroidNetworkAddressFactory(streamListenPort);
    }
    
    @Override
    protected Namespace createNamespace() {
        return new Namespace("/upnp");
    }
    
    @Override
    public StreamClient createStreamClient() {
        return new StreamClientImpl(new StreamClientConfigurationImpl(this.getSyncProtocolExecutorService()) {
            @Override
            public String getUserAgentValue(final int majorVersion, final int minorVersion) {
                final ServerClientTokens tokens = new ServerClientTokens(majorVersion, minorVersion);
                tokens.setOsName("Android");
                tokens.setOsVersion(Build.VERSION.RELEASE);
                return tokens.toString();
            }
        });
    }
    
    @Override
    public StreamServer createStreamServer(final NetworkAddressFactory networkAddressFactory) {
        return new AsyncServletStreamServerImpl(new AsyncServletStreamServerConfigurationImpl(JettyServletContainer.INSTANCE, networkAddressFactory.getStreamListenPort()));
    }
    
    @Override
    protected DeviceDescriptorBinder createDeviceDescriptorBinderUDA10() {
        return new RecoveringUDA10DeviceDescriptorBinderImpl();
    }
    
    @Override
    protected ServiceDescriptorBinder createServiceDescriptorBinderUDA10() {
        return new UDA10ServiceDescriptorBinderSAXImpl();
    }
    
    @Override
    protected SOAPActionProcessor createSOAPActionProcessor() {
        return new RecoveringSOAPActionProcessorImpl();
    }
    
    @Override
    protected GENAEventProcessor createGENAEventProcessor() {
        return new RecoveringGENAEventProcessorImpl();
    }
    
    @Override
    public int getRegistryMaintenanceIntervalMillis() {
        return 3000;
    }
}
