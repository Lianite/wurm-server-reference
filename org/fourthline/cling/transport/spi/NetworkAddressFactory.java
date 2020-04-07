// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.spi;

import java.net.NetworkInterface;
import java.util.Iterator;
import java.net.InetAddress;

public interface NetworkAddressFactory
{
    public static final String SYSTEM_PROPERTY_NET_IFACES = "org.fourthline.cling.network.useInterfaces";
    public static final String SYSTEM_PROPERTY_NET_ADDRESSES = "org.fourthline.cling.network.useAddresses";
    
    InetAddress getMulticastGroup();
    
    int getMulticastPort();
    
    int getStreamListenPort();
    
    Iterator<NetworkInterface> getNetworkInterfaces();
    
    Iterator<InetAddress> getBindAddresses();
    
    boolean hasUsableNetwork();
    
    Short getAddressNetworkPrefixLength(final InetAddress p0);
    
    byte[] getHardwareAddress(final InetAddress p0);
    
    InetAddress getBroadcastAddress(final InetAddress p0);
    
    InetAddress getLocalAddress(final NetworkInterface p0, final boolean p1, final InetAddress p2) throws IllegalStateException;
    
    void logInterfaceInformation();
}
