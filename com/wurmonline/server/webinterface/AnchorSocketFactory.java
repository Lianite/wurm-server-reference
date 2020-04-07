// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.webinterface;

import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.io.Serializable;
import java.rmi.server.RMISocketFactory;

final class AnchorSocketFactory extends RMISocketFactory implements Serializable
{
    private static final long serialVersionUID = 720394327635467676L;
    private final InetAddress ipInterface;
    
    AnchorSocketFactory(final InetAddress aIpInterface) {
        this.ipInterface = aIpInterface;
    }
    
    @Override
    public ServerSocket createServerSocket(final int port) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port, 50, this.ipInterface);
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return serverSocket;
    }
    
    @Override
    public Socket createSocket(final String dummy, final int port) throws IOException {
        return new Socket(this.ipInterface, port);
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that.getClass() == this.getClass();
    }
}
