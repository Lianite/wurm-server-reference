// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.intra;

import com.wurmonline.communication.SimpleConnectionListener;
import com.wurmonline.communication.SocketConnection;
import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.Level;
import com.wurmonline.server.ServerMonitoring;
import com.wurmonline.communication.SocketServer;
import java.util.logging.Logger;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.shared.constants.CounterTypes;
import com.wurmonline.communication.ServerListener;

public final class IntraServer implements ServerListener, CounterTypes, MiscConstants, TimeConstants
{
    private static final Logger logger;
    public SocketServer socketServer;
    private final ServerMonitoring wurmserver;
    
    public IntraServer(final ServerMonitoring server) throws IOException {
        this.wurmserver = server;
        this.socketServer = new SocketServer(server.getInternalIp(), server.getIntraServerPort(), server.getIntraServerPort() + 1, this);
        this.socketServer.intraServer = true;
        IntraServer.logger.log(Level.INFO, "Intraserver listening on " + InetAddress.getByAddress(server.getInternalIp()) + ':' + server.getIntraServerPort());
    }
    
    @Override
    public void clientConnected(final SocketConnection serverConnection) {
        try {
            final IntraServerConnection conn = new IntraServerConnection(serverConnection, this.wurmserver);
            serverConnection.setConnectionListener(conn);
            if (IntraServer.logger.isLoggable(Level.FINE)) {
                IntraServer.logger.fine("IntraServer client connected from IP " + serverConnection.getIp());
            }
        }
        catch (Exception ex) {
            IntraServer.logger.log(Level.SEVERE, "Failed to create intraserver connection: " + serverConnection + '.', ex);
        }
    }
    
    @Override
    public void clientException(final SocketConnection conn, final Exception ex) {
        if (IntraServer.logger.isLoggable(Level.FINE)) {
            IntraServer.logger.log(Level.FINE, "Remote server lost link on connection: " + conn + " - cause:" + ex.getMessage(), ex);
        }
        if (conn != null) {
            try {
                conn.flush();
            }
            catch (Exception ex2) {}
            conn.sendShutdown();
            try {
                conn.disconnect();
            }
            catch (Exception ex3) {}
            conn.closeChannel();
        }
    }
    
    static {
        logger = Logger.getLogger(IntraServer.class.getName());
    }
}
