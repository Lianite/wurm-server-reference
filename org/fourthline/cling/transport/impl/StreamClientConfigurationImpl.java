// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.impl;

import java.util.concurrent.ExecutorService;
import org.fourthline.cling.transport.spi.AbstractStreamClientConfiguration;

public class StreamClientConfigurationImpl extends AbstractStreamClientConfiguration
{
    private boolean usePersistentConnections;
    
    public StreamClientConfigurationImpl(final ExecutorService timeoutExecutorService) {
        super(timeoutExecutorService);
        this.usePersistentConnections = false;
    }
    
    public StreamClientConfigurationImpl(final ExecutorService timeoutExecutorService, final int timeoutSeconds) {
        super(timeoutExecutorService, timeoutSeconds);
        this.usePersistentConnections = false;
    }
    
    public boolean isUsePersistentConnections() {
        return this.usePersistentConnections;
    }
    
    public void setUsePersistentConnections(final boolean usePersistentConnections) {
        this.usePersistentConnections = usePersistentConnections;
    }
}
