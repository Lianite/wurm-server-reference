// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling;

import org.fourthline.cling.model.Namespace;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.message.UpnpHeaders;
import org.fourthline.cling.model.meta.RemoteDeviceIdentity;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.binding.xml.ServiceDescriptorBinder;
import org.fourthline.cling.binding.xml.DeviceDescriptorBinder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executor;
import org.fourthline.cling.transport.spi.StreamServer;
import org.fourthline.cling.transport.spi.DatagramIO;
import org.fourthline.cling.transport.spi.MulticastReceiver;
import org.fourthline.cling.transport.spi.StreamClient;
import org.fourthline.cling.transport.spi.GENAEventProcessor;
import org.fourthline.cling.transport.spi.SOAPActionProcessor;
import org.fourthline.cling.transport.spi.DatagramProcessor;
import org.fourthline.cling.transport.spi.NetworkAddressFactory;

public interface UpnpServiceConfiguration
{
    NetworkAddressFactory createNetworkAddressFactory();
    
    DatagramProcessor getDatagramProcessor();
    
    SOAPActionProcessor getSoapActionProcessor();
    
    GENAEventProcessor getGenaEventProcessor();
    
    StreamClient createStreamClient();
    
    MulticastReceiver createMulticastReceiver(final NetworkAddressFactory p0);
    
    DatagramIO createDatagramIO(final NetworkAddressFactory p0);
    
    StreamServer createStreamServer(final NetworkAddressFactory p0);
    
    Executor getMulticastReceiverExecutor();
    
    Executor getDatagramIOExecutor();
    
    ExecutorService getStreamServerExecutorService();
    
    DeviceDescriptorBinder getDeviceDescriptorBinderUDA10();
    
    ServiceDescriptorBinder getServiceDescriptorBinderUDA10();
    
    ServiceType[] getExclusiveServiceTypes();
    
    int getRegistryMaintenanceIntervalMillis();
    
    int getAliveIntervalMillis();
    
    boolean isReceivedSubscriptionTimeoutIgnored();
    
    Integer getRemoteDeviceMaxAgeSeconds();
    
    UpnpHeaders getDescriptorRetrievalHeaders(final RemoteDeviceIdentity p0);
    
    UpnpHeaders getEventSubscriptionHeaders(final RemoteService p0);
    
    Executor getAsyncProtocolExecutor();
    
    ExecutorService getSyncProtocolExecutorService();
    
    Namespace getNamespace();
    
    Executor getRegistryMaintainerExecutor();
    
    Executor getRegistryListenerExecutor();
    
    void shutdown();
}
