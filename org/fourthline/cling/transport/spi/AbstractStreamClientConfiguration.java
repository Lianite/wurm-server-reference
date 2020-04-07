// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.spi;

import org.fourthline.cling.model.ServerClientTokens;
import java.util.concurrent.ExecutorService;

public abstract class AbstractStreamClientConfiguration implements StreamClientConfiguration
{
    protected ExecutorService requestExecutorService;
    protected int timeoutSeconds;
    protected int logWarningSeconds;
    
    protected AbstractStreamClientConfiguration(final ExecutorService requestExecutorService) {
        this.timeoutSeconds = 60;
        this.logWarningSeconds = 5;
        this.requestExecutorService = requestExecutorService;
    }
    
    protected AbstractStreamClientConfiguration(final ExecutorService requestExecutorService, final int timeoutSeconds) {
        this.timeoutSeconds = 60;
        this.logWarningSeconds = 5;
        this.requestExecutorService = requestExecutorService;
        this.timeoutSeconds = timeoutSeconds;
    }
    
    protected AbstractStreamClientConfiguration(final ExecutorService requestExecutorService, final int timeoutSeconds, final int logWarningSeconds) {
        this.timeoutSeconds = 60;
        this.logWarningSeconds = 5;
        this.requestExecutorService = requestExecutorService;
        this.timeoutSeconds = timeoutSeconds;
        this.logWarningSeconds = logWarningSeconds;
    }
    
    @Override
    public ExecutorService getRequestExecutorService() {
        return this.requestExecutorService;
    }
    
    public void setRequestExecutorService(final ExecutorService requestExecutorService) {
        this.requestExecutorService = requestExecutorService;
    }
    
    @Override
    public int getTimeoutSeconds() {
        return this.timeoutSeconds;
    }
    
    public void setTimeoutSeconds(final int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }
    
    @Override
    public int getLogWarningSeconds() {
        return this.logWarningSeconds;
    }
    
    public void setLogWarningSeconds(final int logWarningSeconds) {
        this.logWarningSeconds = logWarningSeconds;
    }
    
    @Override
    public String getUserAgentValue(final int majorVersion, final int minorVersion) {
        return new ServerClientTokens(majorVersion, minorVersion).toString();
    }
}
