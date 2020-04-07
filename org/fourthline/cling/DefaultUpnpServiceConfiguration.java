// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling;

import java.util.concurrent.atomic.AtomicInteger;
import org.seamless.util.Exceptions;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import org.fourthline.cling.binding.xml.UDA10ServiceDescriptorBinderImpl;
import org.fourthline.cling.binding.xml.UDA10DeviceDescriptorBinderImpl;
import org.fourthline.cling.transport.impl.GENAEventProcessorImpl;
import org.fourthline.cling.transport.impl.SOAPActionProcessorImpl;
import org.fourthline.cling.transport.impl.DatagramProcessorImpl;
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
import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.model.Namespace;
import org.fourthline.cling.binding.xml.ServiceDescriptorBinder;
import org.fourthline.cling.binding.xml.DeviceDescriptorBinder;
import org.fourthline.cling.transport.spi.GENAEventProcessor;
import org.fourthline.cling.transport.spi.SOAPActionProcessor;
import org.fourthline.cling.transport.spi.DatagramProcessor;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;
import javax.enterprise.inject.Alternative;

@Alternative
public class DefaultUpnpServiceConfiguration implements UpnpServiceConfiguration
{
    private static Logger log;
    private final int streamListenPort;
    private final ExecutorService defaultExecutorService;
    private final DatagramProcessor datagramProcessor;
    private final SOAPActionProcessor soapActionProcessor;
    private final GENAEventProcessor genaEventProcessor;
    private final DeviceDescriptorBinder deviceDescriptorBinderUDA10;
    private final ServiceDescriptorBinder serviceDescriptorBinderUDA10;
    private final Namespace namespace;
    
    public DefaultUpnpServiceConfiguration() {
        this(0);
    }
    
    public DefaultUpnpServiceConfiguration(final int streamListenPort) {
        this(streamListenPort, true);
    }
    
    protected DefaultUpnpServiceConfiguration(final boolean checkRuntime) {
        this(0, checkRuntime);
    }
    
    protected DefaultUpnpServiceConfiguration(final int streamListenPort, final boolean checkRuntime) {
        if (checkRuntime && ModelUtil.ANDROID_RUNTIME) {
            throw new Error("Unsupported runtime environment, use org.fourthline.cling.android.AndroidUpnpServiceConfiguration");
        }
        this.streamListenPort = streamListenPort;
        this.defaultExecutorService = this.createDefaultExecutorService();
        this.datagramProcessor = this.createDatagramProcessor();
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
        DefaultUpnpServiceConfiguration.log.fine("Shutting down default executor service");
        this.getDefaultExecutorService().shutdownNow();
    }
    
    protected NetworkAddressFactory createNetworkAddressFactory(final int streamListenPort) {
        return new NetworkAddressFactoryImpl(streamListenPort);
    }
    
    protected DatagramProcessor createDatagramProcessor() {
        return new DatagramProcessorImpl();
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
        return new ClingExecutor();
    }
    
    static {
        DefaultUpnpServiceConfiguration.log = Logger.getLogger(DefaultUpnpServiceConfiguration.class.getName());
    }
    
    public static class ClingExecutor extends ThreadPoolExecutor
    {
        public ClingExecutor() {
            this(new ClingThreadFactory(), new DiscardPolicy() {
                @Override
                public void rejectedExecution(final Runnable runnable, final ThreadPoolExecutor threadPoolExecutor) {
                    DefaultUpnpServiceConfiguration.log.info("Thread pool rejected execution of " + runnable.getClass());
                    super.rejectedExecution(runnable, threadPoolExecutor);
                }
            });
        }
        
        public ClingExecutor(final ThreadFactory threadFactory, final RejectedExecutionHandler rejectedHandler) {
            super(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), threadFactory, rejectedHandler);
        }
        
        @Override
        protected void afterExecute(final Runnable runnable, final Throwable throwable) {
            super.afterExecute(runnable, throwable);
            if (throwable != null) {
                final Throwable cause = Exceptions.unwrap(throwable);
                if (cause instanceof InterruptedException) {
                    return;
                }
                DefaultUpnpServiceConfiguration.log.warning("Thread terminated " + runnable + " abruptly with exception: " + throwable);
                DefaultUpnpServiceConfiguration.log.warning("Root cause: " + cause);
            }
        }
    }
    
    public static class ClingThreadFactory implements ThreadFactory
    {
        protected final ThreadGroup group;
        protected final AtomicInteger threadNumber;
        protected final String namePrefix = "cling-";
        
        public ClingThreadFactory() {
            this.threadNumber = new AtomicInteger(1);
            final SecurityManager s = System.getSecurityManager();
            this.group = ((s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup());
        }
        
        @Override
        public Thread newThread(final Runnable r) {
            final Thread t = new Thread(this.group, r, "cling-" + this.threadNumber.getAndIncrement(), 0L);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != 5) {
                t.setPriority(5);
            }
            return t;
        }
    }
}
