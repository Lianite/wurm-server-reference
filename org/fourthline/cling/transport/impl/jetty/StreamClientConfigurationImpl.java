// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.impl.jetty;

import java.util.concurrent.ExecutorService;
import org.fourthline.cling.transport.spi.AbstractStreamClientConfiguration;

public class StreamClientConfigurationImpl extends AbstractStreamClientConfiguration
{
    public StreamClientConfigurationImpl(final ExecutorService timeoutExecutorService) {
        super(timeoutExecutorService);
    }
    
    public StreamClientConfigurationImpl(final ExecutorService timeoutExecutorService, final int timeoutSeconds) {
        super(timeoutExecutorService, timeoutSeconds);
    }
    
    public int getRequestRetryCount() {
        return 0;
    }
}
