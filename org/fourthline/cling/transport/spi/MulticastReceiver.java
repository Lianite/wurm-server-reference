// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.spi;

import org.fourthline.cling.transport.Router;
import java.net.NetworkInterface;

public interface MulticastReceiver<C extends MulticastReceiverConfiguration> extends Runnable
{
    void init(final NetworkInterface p0, final Router p1, final NetworkAddressFactory p2, final DatagramProcessor p3) throws InitializationException;
    
    void stop();
    
    C getConfiguration();
}
