// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.webinterface;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.Remote;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.registry.LocateRegistry;
import java.net.InetAddress;

public final class RegistryStarter
{
    public static final String namingInterface = "wuinterface";
    
    public static void startRegistry(final WebInterfaceImpl webInterface, final InetAddress inetaddress, final int rmiPort) throws RemoteException, AlreadyBoundException {
        final AnchorSocketFactory sf = new AnchorSocketFactory(inetaddress);
        final Registry registry = LocateRegistry.createRegistry(rmiPort, null, sf);
        registry.bind("wuinterface", webInterface);
    }
}
