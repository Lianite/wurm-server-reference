// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.spi;

public interface DatagramIOConfiguration
{
    int getTimeToLive();
    
    int getMaxDatagramBytes();
}
