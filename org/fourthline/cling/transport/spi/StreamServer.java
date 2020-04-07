// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.spi;

import org.fourthline.cling.transport.Router;
import java.net.InetAddress;

public interface StreamServer<C extends StreamServerConfiguration> extends Runnable
{
    void init(final InetAddress p0, final Router p1) throws InitializationException;
    
    int getPort();
    
    void stop();
    
    C getConfiguration();
}
