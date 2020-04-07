// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model;

import java.util.Arrays;
import java.net.InetAddress;

public class NetworkAddress
{
    protected InetAddress address;
    protected int port;
    protected byte[] hardwareAddress;
    
    public NetworkAddress(final InetAddress address, final int port) {
        this(address, port, null);
    }
    
    public NetworkAddress(final InetAddress address, final int port, final byte[] hardwareAddress) {
        this.address = address;
        this.port = port;
        this.hardwareAddress = hardwareAddress;
    }
    
    public InetAddress getAddress() {
        return this.address;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public byte[] getHardwareAddress() {
        return this.hardwareAddress;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final NetworkAddress that = (NetworkAddress)o;
        return this.port == that.port && this.address.equals(that.address) && Arrays.equals(this.hardwareAddress, that.hardwareAddress);
    }
    
    @Override
    public int hashCode() {
        int result = this.address.hashCode();
        result = 31 * result + this.port;
        result = 31 * result + ((this.hardwareAddress != null) ? Arrays.hashCode(this.hardwareAddress) : 0);
        return result;
    }
}
