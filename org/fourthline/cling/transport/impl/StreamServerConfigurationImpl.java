// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.impl;

import org.fourthline.cling.transport.spi.StreamServerConfiguration;

public class StreamServerConfigurationImpl implements StreamServerConfiguration
{
    private int listenPort;
    private int tcpConnectionBacklog;
    
    public StreamServerConfigurationImpl() {
    }
    
    public StreamServerConfigurationImpl(final int listenPort) {
        this.listenPort = listenPort;
    }
    
    @Override
    public int getListenPort() {
        return this.listenPort;
    }
    
    public void setListenPort(final int listenPort) {
        this.listenPort = listenPort;
    }
    
    public int getTcpConnectionBacklog() {
        return this.tcpConnectionBacklog;
    }
    
    public void setTcpConnectionBacklog(final int tcpConnectionBacklog) {
        this.tcpConnectionBacklog = tcpConnectionBacklog;
    }
}
