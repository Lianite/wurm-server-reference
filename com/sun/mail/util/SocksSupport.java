// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.mail.util;

import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;

class SocksSupport
{
    public static Socket getSocket(final String host, final int port) {
        if (host == null || host.length() == 0) {
            return new Socket(Proxy.NO_PROXY);
        }
        return new Socket(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(host, port)));
    }
}
