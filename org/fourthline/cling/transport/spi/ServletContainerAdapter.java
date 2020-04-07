// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.spi;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

public interface ServletContainerAdapter
{
    void setExecutorService(final ExecutorService p0);
    
    int addConnector(final String p0, final int p1) throws IOException;
    
    void removeConnector(final String p0, final int p1);
    
    void registerServlet(final String p0, final Servlet p1);
    
    void startIfNotRunning();
    
    void stopIfRunning();
}
