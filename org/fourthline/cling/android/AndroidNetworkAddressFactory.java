// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.android;

import org.fourthline.cling.transport.spi.InitializationException;
import java.util.Iterator;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.logging.Logger;
import org.fourthline.cling.transport.impl.NetworkAddressFactoryImpl;

public class AndroidNetworkAddressFactory extends NetworkAddressFactoryImpl
{
    private static final Logger log;
    
    public AndroidNetworkAddressFactory(final int streamListenPort) {
        super(streamListenPort);
    }
    
    @Override
    protected boolean requiresNetworkInterface() {
        return false;
    }
    
    @Override
    protected boolean isUsableAddress(final NetworkInterface networkInterface, final InetAddress address) {
        final boolean result = super.isUsableAddress(networkInterface, address);
        if (result) {
            final String hostName = address.getHostAddress();
            Field field0 = null;
            Object target = null;
            try {
                try {
                    field0 = InetAddress.class.getDeclaredField("holder");
                    field0.setAccessible(true);
                    target = field0.get(address);
                    field0 = target.getClass().getDeclaredField("hostName");
                }
                catch (NoSuchFieldException e) {
                    field0 = InetAddress.class.getDeclaredField("hostName");
                    target = address;
                }
                if (field0 == null || target == null || hostName == null) {
                    return false;
                }
                field0.setAccessible(true);
                field0.set(target, hostName);
            }
            catch (Exception ex) {
                AndroidNetworkAddressFactory.log.log(Level.SEVERE, "Failed injecting hostName to work around Android InetAddress DNS bug: " + address, ex);
                return false;
            }
        }
        return result;
    }
    
    @Override
    public InetAddress getLocalAddress(final NetworkInterface networkInterface, final boolean isIPv6, final InetAddress remoteAddress) {
        for (final InetAddress localAddress : this.getInetAddresses(networkInterface)) {
            if (isIPv6 && localAddress instanceof Inet6Address) {
                return localAddress;
            }
            if (!isIPv6 && localAddress instanceof Inet4Address) {
                return localAddress;
            }
        }
        throw new IllegalStateException("Can't find any IPv4 or IPv6 address on interface: " + networkInterface.getDisplayName());
    }
    
    @Override
    protected void discoverNetworkInterfaces() throws InitializationException {
        try {
            super.discoverNetworkInterfaces();
        }
        catch (Exception ex) {
            AndroidNetworkAddressFactory.log.warning("Exception while enumerating network interfaces, trying once more: " + ex);
            super.discoverNetworkInterfaces();
        }
    }
    
    static {
        log = Logger.getLogger(AndroidUpnpServiceConfiguration.class.getName());
    }
}
