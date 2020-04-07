// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.impl;

import java.util.Locale;
import java.util.Enumeration;
import java.util.Collections;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InterfaceAddress;
import org.seamless.util.Iterators;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.net.SocketException;
import java.util.logging.Level;
import org.fourthline.cling.transport.spi.NoNetworkException;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import org.fourthline.cling.transport.spi.InitializationException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import org.fourthline.cling.transport.spi.NetworkAddressFactory;

public class NetworkAddressFactoryImpl implements NetworkAddressFactory
{
    public static final int DEFAULT_TCP_HTTP_LISTEN_PORT = 0;
    private static Logger log;
    protected final Set<String> useInterfaces;
    protected final Set<String> useAddresses;
    protected final List<NetworkInterface> networkInterfaces;
    protected final List<InetAddress> bindAddresses;
    protected int streamListenPort;
    
    public NetworkAddressFactoryImpl() throws InitializationException {
        this(0);
    }
    
    public NetworkAddressFactoryImpl(final int streamListenPort) throws InitializationException {
        this.useInterfaces = new HashSet<String>();
        this.useAddresses = new HashSet<String>();
        this.networkInterfaces = new ArrayList<NetworkInterface>();
        this.bindAddresses = new ArrayList<InetAddress>();
        System.setProperty("java.net.preferIPv4Stack", "true");
        final String useInterfacesString = System.getProperty("org.fourthline.cling.network.useInterfaces");
        if (useInterfacesString != null) {
            final String[] userInterfacesStrings = useInterfacesString.split(",");
            this.useInterfaces.addAll(Arrays.asList(userInterfacesStrings));
        }
        final String useAddressesString = System.getProperty("org.fourthline.cling.network.useAddresses");
        if (useAddressesString != null) {
            final String[] useAddressesStrings = useAddressesString.split(",");
            this.useAddresses.addAll(Arrays.asList(useAddressesStrings));
        }
        this.discoverNetworkInterfaces();
        this.discoverBindAddresses();
        if (this.networkInterfaces.size() == 0 || this.bindAddresses.size() == 0) {
            NetworkAddressFactoryImpl.log.warning("No usable network interface or addresses found");
            if (this.requiresNetworkInterface()) {
                throw new NoNetworkException("Could not discover any usable network interfaces and/or addresses");
            }
        }
        this.streamListenPort = streamListenPort;
    }
    
    protected boolean requiresNetworkInterface() {
        return true;
    }
    
    @Override
    public void logInterfaceInformation() {
        synchronized (this.networkInterfaces) {
            if (this.networkInterfaces.isEmpty()) {
                NetworkAddressFactoryImpl.log.info("No network interface to display!");
                return;
            }
            for (final NetworkInterface networkInterface : this.networkInterfaces) {
                try {
                    this.logInterfaceInformation(networkInterface);
                }
                catch (SocketException ex) {
                    NetworkAddressFactoryImpl.log.log(Level.WARNING, "Exception while logging network interface information", ex);
                }
            }
        }
    }
    
    @Override
    public InetAddress getMulticastGroup() {
        try {
            return InetAddress.getByName("239.255.255.250");
        }
        catch (UnknownHostException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @Override
    public int getMulticastPort() {
        return 1900;
    }
    
    @Override
    public int getStreamListenPort() {
        return this.streamListenPort;
    }
    
    @Override
    public Iterator<NetworkInterface> getNetworkInterfaces() {
        return new Iterators.Synchronized<NetworkInterface>(this.networkInterfaces) {
            @Override
            protected void synchronizedRemove(final int index) {
                synchronized (NetworkAddressFactoryImpl.this.networkInterfaces) {
                    NetworkAddressFactoryImpl.this.networkInterfaces.remove(index);
                }
            }
        };
    }
    
    @Override
    public Iterator<InetAddress> getBindAddresses() {
        return new Iterators.Synchronized<InetAddress>(this.bindAddresses) {
            @Override
            protected void synchronizedRemove(final int index) {
                synchronized (NetworkAddressFactoryImpl.this.bindAddresses) {
                    NetworkAddressFactoryImpl.this.bindAddresses.remove(index);
                }
            }
        };
    }
    
    @Override
    public boolean hasUsableNetwork() {
        return this.networkInterfaces.size() > 0 && this.bindAddresses.size() > 0;
    }
    
    @Override
    public byte[] getHardwareAddress(final InetAddress inetAddress) {
        try {
            final NetworkInterface iface = NetworkInterface.getByInetAddress(inetAddress);
            return (byte[])((iface != null) ? iface.getHardwareAddress() : null);
        }
        catch (Throwable ex) {
            NetworkAddressFactoryImpl.log.log(Level.WARNING, "Cannot get hardware address for: " + inetAddress, ex);
            return null;
        }
    }
    
    @Override
    public InetAddress getBroadcastAddress(final InetAddress inetAddress) {
        synchronized (this.networkInterfaces) {
            for (final NetworkInterface iface : this.networkInterfaces) {
                for (final InterfaceAddress interfaceAddress : this.getInterfaceAddresses(iface)) {
                    if (interfaceAddress != null && interfaceAddress.getAddress().equals(inetAddress)) {
                        return interfaceAddress.getBroadcast();
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public Short getAddressNetworkPrefixLength(final InetAddress inetAddress) {
        synchronized (this.networkInterfaces) {
            for (final NetworkInterface iface : this.networkInterfaces) {
                for (final InterfaceAddress interfaceAddress : this.getInterfaceAddresses(iface)) {
                    if (interfaceAddress != null && interfaceAddress.getAddress().equals(inetAddress)) {
                        final short prefix = interfaceAddress.getNetworkPrefixLength();
                        if (prefix > 0 && prefix < 32) {
                            return prefix;
                        }
                        return null;
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public InetAddress getLocalAddress(final NetworkInterface networkInterface, final boolean isIPv6, final InetAddress remoteAddress) {
        final InetAddress localIPInSubnet = this.getBindAddressInSubnetOf(remoteAddress);
        if (localIPInSubnet != null) {
            return localIPInSubnet;
        }
        NetworkAddressFactoryImpl.log.finer("Could not find local bind address in same subnet as: " + remoteAddress.getHostAddress());
        for (final InetAddress interfaceAddress : this.getInetAddresses(networkInterface)) {
            if (isIPv6 && interfaceAddress instanceof Inet6Address) {
                return interfaceAddress;
            }
            if (!isIPv6 && interfaceAddress instanceof Inet4Address) {
                return interfaceAddress;
            }
        }
        throw new IllegalStateException("Can't find any IPv4 or IPv6 address on interface: " + networkInterface.getDisplayName());
    }
    
    protected List<InterfaceAddress> getInterfaceAddresses(final NetworkInterface networkInterface) {
        return networkInterface.getInterfaceAddresses();
    }
    
    protected List<InetAddress> getInetAddresses(final NetworkInterface networkInterface) {
        return Collections.list(networkInterface.getInetAddresses());
    }
    
    protected InetAddress getBindAddressInSubnetOf(final InetAddress inetAddress) {
        synchronized (this.networkInterfaces) {
            for (final NetworkInterface iface : this.networkInterfaces) {
                for (final InterfaceAddress ifaceAddress : this.getInterfaceAddresses(iface)) {
                    synchronized (this.bindAddresses) {
                        if (ifaceAddress == null || !this.bindAddresses.contains(ifaceAddress.getAddress())) {
                            continue;
                        }
                    }
                    if (this.isInSubnet(inetAddress.getAddress(), ifaceAddress.getAddress().getAddress(), ifaceAddress.getNetworkPrefixLength())) {
                        return ifaceAddress.getAddress();
                    }
                }
            }
        }
        return null;
    }
    
    protected boolean isInSubnet(final byte[] ip, final byte[] network, short prefix) {
        if (ip.length != network.length) {
            return false;
        }
        if (prefix / 8 > ip.length) {
            return false;
        }
        int i;
        for (i = 0; prefix >= 8 && i < ip.length; ++i, prefix -= 8) {
            if (ip[i] != network[i]) {
                return false;
            }
        }
        if (i == ip.length) {
            return true;
        }
        final byte mask = (byte)~((1 << 8 - prefix) - 1);
        return (ip[i] & mask) == (network[i] & mask);
    }
    
    protected void discoverNetworkInterfaces() throws InitializationException {
        try {
            final Enumeration<NetworkInterface> interfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            for (final NetworkInterface iface : Collections.list(interfaceEnumeration)) {
                NetworkAddressFactoryImpl.log.finer("Analyzing network interface: " + iface.getDisplayName());
                if (this.isUsableNetworkInterface(iface)) {
                    NetworkAddressFactoryImpl.log.fine("Discovered usable network interface: " + iface.getDisplayName());
                    synchronized (this.networkInterfaces) {
                        this.networkInterfaces.add(iface);
                    }
                }
                else {
                    NetworkAddressFactoryImpl.log.finer("Ignoring non-usable network interface: " + iface.getDisplayName());
                }
            }
        }
        catch (Exception ex) {
            throw new InitializationException("Could not not analyze local network interfaces: " + ex, ex);
        }
    }
    
    protected boolean isUsableNetworkInterface(final NetworkInterface iface) throws Exception {
        if (!iface.isUp()) {
            NetworkAddressFactoryImpl.log.finer("Skipping network interface (down): " + iface.getDisplayName());
            return false;
        }
        if (this.getInetAddresses(iface).size() == 0) {
            NetworkAddressFactoryImpl.log.finer("Skipping network interface without bound IP addresses: " + iface.getDisplayName());
            return false;
        }
        if (iface.getName().toLowerCase(Locale.ROOT).startsWith("vmnet") || (iface.getDisplayName() != null && iface.getDisplayName().toLowerCase(Locale.ROOT).contains("vmnet"))) {
            NetworkAddressFactoryImpl.log.finer("Skipping network interface (VMWare): " + iface.getDisplayName());
            return false;
        }
        if (iface.getName().toLowerCase(Locale.ROOT).startsWith("vnic")) {
            NetworkAddressFactoryImpl.log.finer("Skipping network interface (Parallels): " + iface.getDisplayName());
            return false;
        }
        if (iface.getName().toLowerCase(Locale.ROOT).startsWith("vboxnet")) {
            NetworkAddressFactoryImpl.log.finer("Skipping network interface (Virtual Box): " + iface.getDisplayName());
            return false;
        }
        if (iface.getName().toLowerCase(Locale.ROOT).contains("virtual")) {
            NetworkAddressFactoryImpl.log.finer("Skipping network interface (named '*virtual*'): " + iface.getDisplayName());
            return false;
        }
        if (iface.getName().toLowerCase(Locale.ROOT).startsWith("ppp")) {
            NetworkAddressFactoryImpl.log.finer("Skipping network interface (PPP): " + iface.getDisplayName());
            return false;
        }
        if (iface.isLoopback()) {
            NetworkAddressFactoryImpl.log.finer("Skipping network interface (ignoring loopback): " + iface.getDisplayName());
            return false;
        }
        if (this.useInterfaces.size() > 0 && !this.useInterfaces.contains(iface.getName())) {
            NetworkAddressFactoryImpl.log.finer("Skipping unwanted network interface (-Dorg.fourthline.cling.network.useInterfaces): " + iface.getName());
            return false;
        }
        if (!iface.supportsMulticast()) {
            NetworkAddressFactoryImpl.log.warning("Network interface may not be multicast capable: " + iface.getDisplayName());
        }
        return true;
    }
    
    protected void discoverBindAddresses() throws InitializationException {
        try {
            synchronized (this.networkInterfaces) {
                final Iterator<NetworkInterface> it = this.networkInterfaces.iterator();
                while (it.hasNext()) {
                    final NetworkInterface networkInterface = it.next();
                    NetworkAddressFactoryImpl.log.finer("Discovering addresses of interface: " + networkInterface.getDisplayName());
                    int usableAddresses = 0;
                    for (final InetAddress inetAddress : this.getInetAddresses(networkInterface)) {
                        if (inetAddress == null) {
                            NetworkAddressFactoryImpl.log.warning("Network has a null address: " + networkInterface.getDisplayName());
                        }
                        else if (this.isUsableAddress(networkInterface, inetAddress)) {
                            NetworkAddressFactoryImpl.log.fine("Discovered usable network interface address: " + inetAddress.getHostAddress());
                            ++usableAddresses;
                            synchronized (this.bindAddresses) {
                                this.bindAddresses.add(inetAddress);
                            }
                        }
                        else {
                            NetworkAddressFactoryImpl.log.finer("Ignoring non-usable network interface address: " + inetAddress.getHostAddress());
                        }
                    }
                    if (usableAddresses == 0) {
                        NetworkAddressFactoryImpl.log.finer("Network interface has no usable addresses, removing: " + networkInterface.getDisplayName());
                        it.remove();
                    }
                }
            }
        }
        catch (Exception ex) {
            throw new InitializationException("Could not not analyze local network interfaces: " + ex, ex);
        }
    }
    
    protected boolean isUsableAddress(final NetworkInterface networkInterface, final InetAddress address) {
        if (!(address instanceof Inet4Address)) {
            NetworkAddressFactoryImpl.log.finer("Skipping unsupported non-IPv4 address: " + address);
            return false;
        }
        if (address.isLoopbackAddress()) {
            NetworkAddressFactoryImpl.log.finer("Skipping loopback address: " + address);
            return false;
        }
        if (this.useAddresses.size() > 0 && !this.useAddresses.contains(address.getHostAddress())) {
            NetworkAddressFactoryImpl.log.finer("Skipping unwanted address: " + address);
            return false;
        }
        return true;
    }
    
    protected void logInterfaceInformation(final NetworkInterface networkInterface) throws SocketException {
        NetworkAddressFactoryImpl.log.info("---------------------------------------------------------------------------------");
        NetworkAddressFactoryImpl.log.info(String.format("Interface display name: %s", networkInterface.getDisplayName()));
        if (networkInterface.getParent() != null) {
            NetworkAddressFactoryImpl.log.info(String.format("Parent Info: %s", networkInterface.getParent()));
        }
        NetworkAddressFactoryImpl.log.info(String.format("Name: %s", networkInterface.getName()));
        final Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
        for (final InetAddress inetAddress : Collections.list(inetAddresses)) {
            NetworkAddressFactoryImpl.log.info(String.format("InetAddress: %s", inetAddress));
        }
        final List<InterfaceAddress> interfaceAddresses = networkInterface.getInterfaceAddresses();
        for (final InterfaceAddress interfaceAddress : interfaceAddresses) {
            if (interfaceAddress == null) {
                NetworkAddressFactoryImpl.log.warning("Skipping null InterfaceAddress!");
            }
            else {
                NetworkAddressFactoryImpl.log.info(" Interface Address");
                NetworkAddressFactoryImpl.log.info("  Address: " + interfaceAddress.getAddress());
                NetworkAddressFactoryImpl.log.info("  Broadcast: " + interfaceAddress.getBroadcast());
                NetworkAddressFactoryImpl.log.info("  Prefix length: " + interfaceAddress.getNetworkPrefixLength());
            }
        }
        final Enumeration<NetworkInterface> subIfs = networkInterface.getSubInterfaces();
        for (final NetworkInterface subIf : Collections.list(subIfs)) {
            if (subIf == null) {
                NetworkAddressFactoryImpl.log.warning("Skipping null NetworkInterface sub-interface");
            }
            else {
                NetworkAddressFactoryImpl.log.info(String.format("\tSub Interface Display name: %s", subIf.getDisplayName()));
                NetworkAddressFactoryImpl.log.info(String.format("\tSub Interface Name: %s", subIf.getName()));
            }
        }
        NetworkAddressFactoryImpl.log.info(String.format("Up? %s", networkInterface.isUp()));
        NetworkAddressFactoryImpl.log.info(String.format("Loopback? %s", networkInterface.isLoopback()));
        NetworkAddressFactoryImpl.log.info(String.format("PointToPoint? %s", networkInterface.isPointToPoint()));
        NetworkAddressFactoryImpl.log.info(String.format("Supports multicast? %s", networkInterface.supportsMulticast()));
        NetworkAddressFactoryImpl.log.info(String.format("Virtual? %s", networkInterface.isVirtual()));
        NetworkAddressFactoryImpl.log.info(String.format("Hardware address: %s", Arrays.toString(networkInterface.getHardwareAddress())));
        NetworkAddressFactoryImpl.log.info(String.format("MTU: %s", networkInterface.getMTU()));
    }
    
    static {
        NetworkAddressFactoryImpl.log = Logger.getLogger(NetworkAddressFactoryImpl.class.getName());
    }
}
