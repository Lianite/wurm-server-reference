// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.spi;

import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.StreamRequestMessage;

public interface StreamClient<C extends StreamClientConfiguration>
{
    StreamResponseMessage sendRequest(final StreamRequestMessage p0) throws InterruptedException;
    
    void stop();
    
    C getConfiguration();
}
