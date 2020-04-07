// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.spi;

import java.net.InetAddress;

public interface MulticastReceiverConfiguration
{
    InetAddress getGroup();
    
    int getPort();
    
    int getMaxDatagramBytes();
}
