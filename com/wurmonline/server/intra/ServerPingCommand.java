// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.intra;

import java.nio.ByteBuffer;
import java.io.IOException;
import java.util.logging.Level;
import com.wurmonline.server.Servers;
import com.wurmonline.server.ServerEntry;
import java.util.logging.Logger;

public final class ServerPingCommand extends IntraCommand
{
    private static final Logger logger;
    private final ServerEntry server;
    private boolean done;
    private IntraClient client;
    private boolean pinging;
    
    ServerPingCommand(final ServerEntry serverEntry) {
        this.done = false;
        this.pinging = false;
        this.server = serverEntry;
    }
    
    @Override
    public boolean poll() {
        if (this.server.id == Servers.localServer.id) {
            return true;
        }
        if (this.client == null) {
            if (ServerPingCommand.logger.isLoggable(Level.FINER)) {
                ServerPingCommand.logger.finer("1 " + this.getLoginServerIntraServerAddress() + ", " + this.getLoginServerIntraServerPort() + ", " + this.getLoginServerIntraServerPassword());
            }
            try {
                (this.client = new IntraClient(this.getLoginServerIntraServerAddress(), Integer.parseInt(this.getLoginServerIntraServerPort()), this)).login(this.server.INTRASERVERPASSWORD, true);
                ServerPingCommand.logger.log(Level.INFO, "connecting to " + this.server.id);
            }
            catch (IOException iox) {
                this.server.setAvailable(false, false, 0, 0, 0, 10);
                this.client.disconnect("Failed.");
                this.client = null;
                ServerPingCommand.logger.log(Level.INFO, "Failed");
                this.done = true;
            }
        }
        if (this.client != null && !this.done) {
            if (System.currentTimeMillis() > this.timeOutAt) {
                this.done = true;
            }
            if (!this.done) {
                try {
                    if (this.client.loggedIn && !this.pinging) {
                        this.client.executePingCommand();
                        this.pinging = true;
                        this.timeOutAt = System.currentTimeMillis() + this.timeOutTime;
                    }
                    if (!this.done) {
                        this.client.update();
                    }
                }
                catch (IOException iox) {
                    this.server.setAvailable(false, false, 0, 0, 0, 10);
                    this.done = true;
                }
            }
            if (this.done && this.client != null) {
                this.client.disconnect("Done");
                this.client = null;
            }
        }
        return this.done;
    }
    
    @Override
    public void commandExecuted(final IntraClient aClient) {
        this.done = true;
    }
    
    @Override
    public void commandFailed(final IntraClient aClient) {
        this.server.setAvailable(false, false, 0, 0, 0, 10);
        this.done = true;
    }
    
    @Override
    public void dataReceived(final IntraClient aClient) {
        this.done = true;
    }
    
    @Override
    public void reschedule(final IntraClient aClient) {
        this.server.setAvailable(false, false, 0, 0, 0, 10);
        this.done = true;
    }
    
    @Override
    public void remove(final IntraClient aClient) {
        this.done = true;
    }
    
    @Override
    public void receivingData(final ByteBuffer buffer) {
        final boolean maintaining = (buffer.get() & 0x1) == 0x1;
        final int numsPlaying = buffer.getInt();
        final int maxLimit = buffer.getInt();
        final int secsToShutdown = buffer.getInt();
        final int meshSize = buffer.getInt();
        this.server.setAvailable(true, maintaining, numsPlaying, maxLimit, secsToShutdown, meshSize);
        this.timeOutAt = System.currentTimeMillis() + this.timeOutTime;
        this.done = true;
    }
    
    static {
        logger = Logger.getLogger(ServerPingCommand.class.getName());
    }
}
