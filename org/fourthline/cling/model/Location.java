// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model;

import java.net.InetAddress;
import java.net.URL;

public class Location
{
    protected final NetworkAddress networkAddress;
    protected final String path;
    protected final URL url;
    
    public Location(final NetworkAddress networkAddress, final String path) {
        this.networkAddress = networkAddress;
        this.path = path;
        this.url = createAbsoluteURL(networkAddress.getAddress(), networkAddress.getPort(), path);
    }
    
    public NetworkAddress getNetworkAddress() {
        return this.networkAddress;
    }
    
    public String getPath() {
        return this.path;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Location location = (Location)o;
        return this.networkAddress.equals(location.networkAddress) && this.path.equals(location.path);
    }
    
    @Override
    public int hashCode() {
        int result = this.networkAddress.hashCode();
        result = 31 * result + this.path.hashCode();
        return result;
    }
    
    public URL getURL() {
        return this.url;
    }
    
    private static URL createAbsoluteURL(final InetAddress address, final int localStreamPort, final String path) {
        try {
            return new URL("http", address.getHostAddress(), localStreamPort, path);
        }
        catch (Exception ex) {
            throw new IllegalArgumentException("Address, port, and URI can not be converted to URL", ex);
        }
    }
}
