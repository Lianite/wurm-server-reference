// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types;

public class HostPort
{
    private String host;
    private int port;
    
    public HostPort() {
    }
    
    public HostPort(final String host, final int port) {
        this.host = host;
        this.port = port;
    }
    
    public String getHost() {
        return this.host;
    }
    
    public void setHost(final String host) {
        this.host = host;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public void setPort(final int port) {
        this.port = port;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final HostPort hostPort = (HostPort)o;
        return this.port == hostPort.port && this.host.equals(hostPort.host);
    }
    
    @Override
    public int hashCode() {
        int result = this.host.hashCode();
        result = 31 * result + this.port;
        return result;
    }
    
    @Override
    public String toString() {
        return this.host + ":" + this.port;
    }
}
