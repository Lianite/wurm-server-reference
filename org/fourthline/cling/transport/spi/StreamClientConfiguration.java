// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.spi;

import java.util.concurrent.ExecutorService;

public interface StreamClientConfiguration
{
    ExecutorService getRequestExecutorService();
    
    int getTimeoutSeconds();
    
    int getLogWarningSeconds();
    
    String getUserAgentValue(final int p0, final int p1);
}
