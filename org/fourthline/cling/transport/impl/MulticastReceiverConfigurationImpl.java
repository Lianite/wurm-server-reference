// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.impl;

import java.net.UnknownHostException;
import java.net.InetAddress;
import org.fourthline.cling.transport.spi.MulticastReceiverConfiguration;

public class MulticastReceiverConfigurationImpl implements MulticastReceiverConfiguration
{
    private InetAddress group;
    private int port;
    private int maxDatagramBytes;
    
    public MulticastReceiverConfigurationImpl(final InetAddress group, final int port, final int maxDatagramBytes) {
        this.group = group;
        this.port = port;
        this.maxDatagramBytes = maxDatagramBytes;
    }
    
    public MulticastReceiverConfigurationImpl(final InetAddress group, final int port) {
        this(group, port, 640);
    }
    
    public MulticastReceiverConfigurationImpl(final String group, final int port, final int maxDatagramBytes) throws UnknownHostException {
        this(InetAddress.getByName(group), port, maxDatagramBytes);
    }
    
    public MulticastReceiverConfigurationImpl(final String group, final int port) throws UnknownHostException {
        this(InetAddress.getByName(group), port, 640);
    }
    
    @Override
    public InetAddress getGroup() {
        return this.group;
    }
    
    public void setGroup(final InetAddress group) {
        this.group = group;
    }
    
    @Override
    public int getPort() {
        return this.port;
    }
    
    public void setPort(final int port) {
        this.port = port;
    }
    
    @Override
    public int getMaxDatagramBytes() {
        return this.maxDatagramBytes;
    }
    
    public void setMaxDatagramBytes(final int maxDatagramBytes) {
        this.maxDatagramBytes = maxDatagramBytes;
    }
}
