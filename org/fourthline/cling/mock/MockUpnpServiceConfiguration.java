// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.mock;

import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executor;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import org.fourthline.cling.transport.impl.NetworkAddressFactoryImpl;
import org.fourthline.cling.transport.spi.NetworkAddressFactory;
import javax.enterprise.inject.Alternative;
import org.fourthline.cling.DefaultUpnpServiceConfiguration;

@Alternative
public class MockUpnpServiceConfiguration extends DefaultUpnpServiceConfiguration
{
    protected final boolean maintainsRegistry;
    protected final boolean multiThreaded;
    
    public MockUpnpServiceConfiguration() {
        this(false, false);
    }
    
    public MockUpnpServiceConfiguration(final boolean maintainsRegistry) {
        this(maintainsRegistry, false);
    }
    
    public MockUpnpServiceConfiguration(final boolean maintainsRegistry, final boolean multiThreaded) {
        super(false);
        this.maintainsRegistry = maintainsRegistry;
        this.multiThreaded = multiThreaded;
    }
    
    public boolean isMaintainsRegistry() {
        return this.maintainsRegistry;
    }
    
    public boolean isMultiThreaded() {
        return this.multiThreaded;
    }
    
    @Override
    protected NetworkAddressFactory createNetworkAddressFactory(final int streamListenPort) {
        return new NetworkAddressFactoryImpl(streamListenPort) {
            @Override
            protected boolean isUsableNetworkInterface(final NetworkInterface iface) throws Exception {
                return iface.isLoopback();
            }
            
            @Override
            protected boolean isUsableAddress(final NetworkInterface networkInterface, final InetAddress address) {
                return address.isLoopbackAddress() && address instanceof Inet4Address;
            }
        };
    }
    
    @Override
    public Executor getRegistryMaintainerExecutor() {
        if (this.isMaintainsRegistry()) {
            return new Executor() {
                @Override
                public void execute(final Runnable runnable) {
                    new Thread(runnable).start();
                }
            };
        }
        return this.getDefaultExecutorService();
    }
    
    @Override
    protected ExecutorService getDefaultExecutorService() {
        if (this.isMultiThreaded()) {
            return super.getDefaultExecutorService();
        }
        return new AbstractExecutorService() {
            boolean terminated;
            
            @Override
            public void shutdown() {
                this.terminated = true;
            }
            
            @Override
            public List<Runnable> shutdownNow() {
                this.shutdown();
                return null;
            }
            
            @Override
            public boolean isShutdown() {
                return this.terminated;
            }
            
            @Override
            public boolean isTerminated() {
                return this.terminated;
            }
            
            @Override
            public boolean awaitTermination(final long l, final TimeUnit timeUnit) throws InterruptedException {
                this.shutdown();
                return this.terminated;
            }
            
            @Override
            public void execute(final Runnable runnable) {
                runnable.run();
            }
        };
    }
}
