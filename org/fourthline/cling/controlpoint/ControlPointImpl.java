// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.controlpoint;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.fourthline.cling.controlpoint.event.ExecuteAction;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.message.header.MXHeader;
import org.fourthline.cling.model.message.header.STAllHeader;
import javax.enterprise.event.Observes;
import org.fourthline.cling.controlpoint.event.Search;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.UpnpServiceConfiguration;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ControlPointImpl implements ControlPoint
{
    private static Logger log;
    protected UpnpServiceConfiguration configuration;
    protected ProtocolFactory protocolFactory;
    protected Registry registry;
    
    protected ControlPointImpl() {
    }
    
    public ControlPointImpl(final UpnpServiceConfiguration configuration, final ProtocolFactory protocolFactory, final Registry registry) {
        ControlPointImpl.log.fine("Creating ControlPoint: " + this.getClass().getName());
        this.configuration = configuration;
        this.protocolFactory = protocolFactory;
        this.registry = registry;
    }
    
    @Override
    public UpnpServiceConfiguration getConfiguration() {
        return this.configuration;
    }
    
    @Override
    public ProtocolFactory getProtocolFactory() {
        return this.protocolFactory;
    }
    
    @Override
    public Registry getRegistry() {
        return this.registry;
    }
    
    public void search(@Observes final Search search) {
        this.search(search.getSearchType(), search.getMxSeconds());
    }
    
    @Override
    public void search() {
        this.search(new STAllHeader(), MXHeader.DEFAULT_VALUE);
    }
    
    @Override
    public void search(final UpnpHeader searchType) {
        this.search(searchType, MXHeader.DEFAULT_VALUE);
    }
    
    @Override
    public void search(final int mxSeconds) {
        this.search(new STAllHeader(), mxSeconds);
    }
    
    @Override
    public void search(final UpnpHeader searchType, final int mxSeconds) {
        ControlPointImpl.log.fine("Sending asynchronous search for: " + searchType.getString());
        this.getConfiguration().getAsyncProtocolExecutor().execute(this.getProtocolFactory().createSendingSearch(searchType, mxSeconds));
    }
    
    public void execute(final ExecuteAction executeAction) {
        this.execute(executeAction.getCallback());
    }
    
    @Override
    public Future execute(final ActionCallback callback) {
        ControlPointImpl.log.fine("Invoking action in background: " + callback);
        callback.setControlPoint(this);
        final ExecutorService executor = this.getConfiguration().getSyncProtocolExecutorService();
        return executor.submit(callback);
    }
    
    @Override
    public void execute(final SubscriptionCallback callback) {
        ControlPointImpl.log.fine("Invoking subscription in background: " + callback);
        callback.setControlPoint(this);
        this.getConfiguration().getSyncProtocolExecutorService().execute(callback);
    }
    
    static {
        ControlPointImpl.log = Logger.getLogger(ControlPointImpl.class.getName());
    }
}
