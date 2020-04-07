// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.impl;

import org.fourthline.cling.transport.spi.ServletContainerAdapter;
import org.fourthline.cling.transport.spi.StreamServerConfiguration;

public class AsyncServletStreamServerConfigurationImpl implements StreamServerConfiguration
{
    protected ServletContainerAdapter servletContainerAdapter;
    protected int listenPort;
    protected int asyncTimeoutSeconds;
    
    public AsyncServletStreamServerConfigurationImpl(final ServletContainerAdapter servletContainerAdapter) {
        this.listenPort = 0;
        this.asyncTimeoutSeconds = 60;
        this.servletContainerAdapter = servletContainerAdapter;
    }
    
    public AsyncServletStreamServerConfigurationImpl(final ServletContainerAdapter servletContainerAdapter, final int listenPort) {
        this.listenPort = 0;
        this.asyncTimeoutSeconds = 60;
        this.servletContainerAdapter = servletContainerAdapter;
        this.listenPort = listenPort;
    }
    
    public AsyncServletStreamServerConfigurationImpl(final ServletContainerAdapter servletContainerAdapter, final int listenPort, final int asyncTimeoutSeconds) {
        this.listenPort = 0;
        this.asyncTimeoutSeconds = 60;
        this.servletContainerAdapter = servletContainerAdapter;
        this.listenPort = listenPort;
        this.asyncTimeoutSeconds = asyncTimeoutSeconds;
    }
    
    @Override
    public int getListenPort() {
        return this.listenPort;
    }
    
    public void setListenPort(final int listenPort) {
        this.listenPort = listenPort;
    }
    
    public int getAsyncTimeoutSeconds() {
        return this.asyncTimeoutSeconds;
    }
    
    public void setAsyncTimeoutSeconds(final int asyncTimeoutSeconds) {
        this.asyncTimeoutSeconds = asyncTimeoutSeconds;
    }
    
    public ServletContainerAdapter getServletContainerAdapter() {
        return this.servletContainerAdapter;
    }
    
    public void setServletContainerAdapter(final ServletContainerAdapter servletContainerAdapter) {
        this.servletContainerAdapter = servletContainerAdapter;
    }
}
