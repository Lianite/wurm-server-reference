// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.impl;

import org.fourthline.cling.transport.spi.DatagramIOConfiguration;

public class DatagramIOConfigurationImpl implements DatagramIOConfiguration
{
    private int timeToLive;
    private int maxDatagramBytes;
    
    public DatagramIOConfigurationImpl() {
        this.timeToLive = 4;
        this.maxDatagramBytes = 640;
    }
    
    public DatagramIOConfigurationImpl(final int timeToLive, final int maxDatagramBytes) {
        this.timeToLive = 4;
        this.maxDatagramBytes = 640;
        this.timeToLive = timeToLive;
        this.maxDatagramBytes = maxDatagramBytes;
    }
    
    @Override
    public int getTimeToLive() {
        return this.timeToLive;
    }
    
    public void setTimeToLive(final int timeToLive) {
        this.timeToLive = timeToLive;
    }
    
    @Override
    public int getMaxDatagramBytes() {
        return this.maxDatagramBytes;
    }
    
    public void setMaxDatagramBytes(final int maxDatagramBytes) {
        this.maxDatagramBytes = maxDatagramBytes;
    }
}
