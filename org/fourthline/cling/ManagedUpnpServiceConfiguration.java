// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling;

import org.fourthline.cling.binding.xml.UDA10ServiceDescriptorBinderImpl;
import org.fourthline.cling.binding.xml.UDA10DeviceDescriptorBinderImpl;
import org.fourthline.cling.transport.impl.GENAEventProcessorImpl;
import org.fourthline.cling.transport.impl.SOAPActionProcessorImpl;
import org.fourthline.cling.transport.impl.NetworkAddressFactoryImpl;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.message.UpnpHeaders;
import org.fourthline.cling.model.meta.RemoteDeviceIdentity;
import org.fourthline.cling.model.types.ServiceType;
import java.util.concurrent.Executor;
import org.fourthline.cling.transport.impl.StreamServerImpl;
import org.fourthline.cling.transport.impl.StreamServerConfigurationImpl;
import org.fourthline.cling.transport.spi.StreamServer;
import org.fourthline.cling.transport.impl.DatagramIOImpl;
import org.fourthline.cling.transport.impl.DatagramIOConfigurationImpl;
import org.fourthline.cling.transport.spi.DatagramIO;
import org.fourthline.cling.transport.impl.MulticastReceiverImpl;
import org.fourthline.cling.transport.impl.MulticastReceiverConfigurationImpl;
import org.fourthline.cling.transport.spi.MulticastReceiver;
import org.fourthline.cling.transport.spi.NetworkAddressFactory;
import org.fourthline.cling.transport.impl.StreamClientImpl;
import org.fourthline.cling.transport.impl.StreamClientConfigurationImpl;
import org.fourthline.cling.transport.spi.StreamClient;
import javax.annotation.PostConstruct;
import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.model.Namespace;
import org.fourthline.cling.binding.xml.ServiceDescriptorBinder;
import org.fourthline.cling.binding.xml.DeviceDescriptorBinder;
import org.fourthline.cling.transport.spi.GENAEventProcessor;
import org.fourthline.cling.transport.spi.SOAPActionProcessor;
import javax.inject.Inject;
import org.fourthline.cling.transport.spi.DatagramProcessor;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ManagedUpnpServiceConfiguration implements UpnpServiceConfiguration
{
    private static Logger log;
    private int streamListenPort;
    private ExecutorService defaultExecutorService;
    @Inject
    protected DatagramProcessor datagramProcessor;
    private SOAPActionProcessor soapActionProcessor;
    private GENAEventProcessor genaEventProcessor;
    private DeviceDescriptorBinder deviceDescriptorBinderUDA10;
    private ServiceDescriptorBinder serviceDescriptorBinderUDA10;
    private Namespace namespace;
    
    @PostConstruct
    public void init() {
        if (ModelUtil.ANDROID_RUNTIME) {
            throw new Error("Unsupported runtime environment, use org.fourthline.cling.android.AndroidUpnpServiceConfiguration");
        }
        this.streamListenPort = 0;
        this.defaultExecutorService = this.createDefaultExecutorService();
        this.soapActionProcessor = this.createSOAPActionProcessor();
        this.genaEventProcessor = this.createGENAEventProcessor();
        this.deviceDescriptorBinderUDA10 = this.createDeviceDescriptorBinderUDA10();
        this.serviceDescriptorBinderUDA10 = this.createServiceDescriptorBinderUDA10();
        this.namespace = this.createNamespace();
    }
    
    @Override
    public DatagramProcessor getDatagramProcessor() {
        return this.datagramProcessor;
    }
    
    @Override
    public SOAPActionProcessor getSoapActionProcessor() {
        return this.soapActionProcessor;
    }
    
    @Override
    public GENAEventProcessor getGenaEventProcessor() {
        return this.genaEventProcessor;
    }
    
    @Override
    public StreamClient createStreamClient() {
        return new StreamClientImpl(new StreamClientConfigurationImpl(this.getSyncProtocolExecutorService()));
    }
    
    @Override
    public MulticastReceiver createMulticastReceiver(final NetworkAddressFactory networkAddressFactory) {
        return new MulticastReceiverImpl(new MulticastReceiverConfigurationImpl(networkAddressFactory.getMulticastGroup(), networkAddressFactory.getMulticastPort()));
    }
    
    @Override
    public DatagramIO createDatagramIO(final NetworkAddressFactory networkAddressFactory) {
        return new DatagramIOImpl(new DatagramIOConfigurationImpl());
    }
    
    @Override
    public StreamServer createStreamServer(final NetworkAddressFactory networkAddressFactory) {
        return new StreamServerImpl(new StreamServerConfigurationImpl(networkAddressFactory.getStreamListenPort()));
    }
    
    @Override
    public Executor getMulticastReceiverExecutor() {
        return this.getDefaultExecutorService();
    }
    
    @Override
    public Executor getDatagramIOExecutor() {
        return this.getDefaultExecutorService();
    }
    
    @Override
    public ExecutorService getStreamServerExecutorService() {
        return this.getDefaultExecutorService();
    }
    
    @Override
    public DeviceDescriptorBinder getDeviceDescriptorBinderUDA10() {
        return this.deviceDescriptorBinderUDA10;
    }
    
    @Override
    public ServiceDescriptorBinder getServiceDescriptorBinderUDA10() {
        return this.serviceDescriptorBinderUDA10;
    }
    
    @Override
    public ServiceType[] getExclusiveServiceTypes() {
        return new ServiceType[0];
    }
    
    @Override
    public boolean isReceivedSubscriptionTimeoutIgnored() {
        return false;
    }
    
    @Override
    public UpnpHeaders getDescriptorRetrievalHeaders(final RemoteDeviceIdentity identity) {
        return null;
    }
    
    @Override
    public UpnpHeaders getEventSubscriptionHeaders(final RemoteService service) {
        return null;
    }
    
    @Override
    public int getRegistryMaintenanceIntervalMillis() {
        return 1000;
    }
    
    @Override
    public int getAliveIntervalMillis() {
        return 0;
    }
    
    @Override
    public Integer getRemoteDeviceMaxAgeSeconds() {
        return null;
    }
    
    @Override
    public Executor getAsyncProtocolExecutor() {
        return this.getDefaultExecutorService();
    }
    
    @Override
    public ExecutorService getSyncProtocolExecutorService() {
        return this.getDefaultExecutorService();
    }
    
    @Override
    public Namespace getNamespace() {
        return this.namespace;
    }
    
    @Override
    public Executor getRegistryMaintainerExecutor() {
        return this.getDefaultExecutorService();
    }
    
    @Override
    public Executor getRegistryListenerExecutor() {
        return this.getDefaultExecutorService();
    }
    
    @Override
    public NetworkAddressFactory createNetworkAddressFactory() {
        return this.createNetworkAddressFactory(this.streamListenPort);
    }
    
    @Override
    public void shutdown() {
        ManagedUpnpServiceConfiguration.log.fine("Shutting down default executor service");
        this.getDefaultExecutorService().shutdownNow();
    }
    
    protected NetworkAddressFactory createNetworkAddressFactory(final int streamListenPort) {
        return new NetworkAddressFactoryImpl(streamListenPort);
    }
    
    protected SOAPActionProcessor createSOAPActionProcessor() {
        return new SOAPActionProcessorImpl();
    }
    
    protected GENAEventProcessor createGENAEventProcessor() {
        return new GENAEventProcessorImpl();
    }
    
    protected DeviceDescriptorBinder createDeviceDescriptorBinderUDA10() {
        return new UDA10DeviceDescriptorBinderImpl();
    }
    
    protected ServiceDescriptorBinder createServiceDescriptorBinderUDA10() {
        return new UDA10ServiceDescriptorBinderImpl();
    }
    
    protected Namespace createNamespace() {
        return new Namespace();
    }
    
    protected ExecutorService getDefaultExecutorService() {
        return this.defaultExecutorService;
    }
    
    protected ExecutorService createDefaultExecutorService() {
        return new DefaultUpnpServiceConfiguration.ClingExecutor();
    }
    
    static {
        ManagedUpnpServiceConfiguration.log = Logger.getLogger(DefaultUpnpServiceConfiguration.class.getName());
    }
}
