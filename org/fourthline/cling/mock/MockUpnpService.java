// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.mock;

import org.fourthline.cling.protocol.async.SendingSearch;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.transport.RouterException;
import org.fourthline.cling.protocol.async.SendingNotificationAlive;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.protocol.ProtocolFactoryImpl;
import org.fourthline.cling.transport.Router;
import org.fourthline.cling.controlpoint.ControlPointImpl;
import org.fourthline.cling.registry.RegistryMaintainer;
import org.fourthline.cling.registry.RegistryImpl;
import org.fourthline.cling.transport.spi.NetworkAddressFactory;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.UpnpServiceConfiguration;
import javax.enterprise.inject.Alternative;
import org.fourthline.cling.UpnpService;

@Alternative
public class MockUpnpService implements UpnpService
{
    protected final UpnpServiceConfiguration configuration;
    protected final ControlPoint controlPoint;
    protected final ProtocolFactory protocolFactory;
    protected final Registry registry;
    protected final MockRouter router;
    protected final NetworkAddressFactory networkAddressFactory;
    
    public MockUpnpService() {
        this(false, new MockUpnpServiceConfiguration(false, false));
    }
    
    public MockUpnpService(final MockUpnpServiceConfiguration configuration) {
        this(false, configuration);
    }
    
    public MockUpnpService(final boolean sendsAlive, final boolean maintainsRegistry) {
        this(sendsAlive, new MockUpnpServiceConfiguration(maintainsRegistry, false));
    }
    
    public MockUpnpService(final boolean sendsAlive, final boolean maintainsRegistry, final boolean multiThreaded) {
        this(sendsAlive, new MockUpnpServiceConfiguration(maintainsRegistry, multiThreaded));
    }
    
    public MockUpnpService(final boolean sendsAlive, final MockUpnpServiceConfiguration configuration) {
        this.configuration = configuration;
        this.protocolFactory = this.createProtocolFactory(this, sendsAlive);
        this.registry = new RegistryImpl(this) {
            @Override
            protected RegistryMaintainer createRegistryMaintainer() {
                return configuration.isMaintainsRegistry() ? super.createRegistryMaintainer() : null;
            }
        };
        this.networkAddressFactory = this.configuration.createNetworkAddressFactory();
        this.router = this.createRouter();
        this.controlPoint = new ControlPointImpl(configuration, this.protocolFactory, this.registry);
    }
    
    protected ProtocolFactory createProtocolFactory(final UpnpService service, final boolean sendsAlive) {
        return new MockProtocolFactory(service, sendsAlive);
    }
    
    protected MockRouter createRouter() {
        return new MockRouter(this.getConfiguration(), this.getProtocolFactory());
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
    public MockRouter getRouter() {
        return this.router;
    }
    
    @Override
    public void shutdown() {
        this.getRegistry().shutdown();
        this.getConfiguration().shutdown();
    }
    
    public static class MockProtocolFactory extends ProtocolFactoryImpl
    {
        private boolean sendsAlive;
        
        public MockProtocolFactory(final UpnpService upnpService, final boolean sendsAlive) {
            super(upnpService);
            this.sendsAlive = sendsAlive;
        }
        
        @Override
        public SendingNotificationAlive createSendingNotificationAlive(final LocalDevice localDevice) {
            return new SendingNotificationAlive(this.getUpnpService(), localDevice) {
                @Override
                protected void execute() throws RouterException {
                    if (MockProtocolFactory.this.sendsAlive) {
                        super.execute();
                    }
                }
            };
        }
        
        @Override
        public SendingSearch createSendingSearch(final UpnpHeader searchTarget, final int mxSeconds) {
            return new SendingSearch(this.getUpnpService(), searchTarget, mxSeconds) {
                @Override
                public int getBulkIntervalMilliseconds() {
                    return 0;
                }
            };
        }
    }
}
