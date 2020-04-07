// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.intra;

import com.wurmonline.server.Constants;
import com.wurmonline.server.Server;
import com.wurmonline.server.Servers;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.io.IOException;
import java.util.logging.Logger;

public final class TimeSync extends IntraCommand
{
    private static final Logger logger;
    private boolean done;
    private IntraClient client;
    private boolean sentCommand;
    private boolean started;
    
    public TimeSync() {
        this.done = false;
        this.sentCommand = false;
        this.started = false;
    }
    
    @Override
    public boolean poll() {
        if (this.isThisLoginServer()) {
            return true;
        }
        Label_0095: {
            if (this.client == null) {
                if (System.currentTimeMillis() <= this.timeOutAt) {
                    if (this.started) {
                        break Label_0095;
                    }
                }
                try {
                    this.timeOutAt = System.currentTimeMillis() + this.timeOutTime;
                    (this.client = new IntraClient(this.getLoginServerIntraServerAddress(), Integer.parseInt(this.getLoginServerIntraServerPort()), this)).login(this.getLoginServerIntraServerPassword(), true);
                    this.started = true;
                }
                catch (IOException iox) {
                    this.done = true;
                }
            }
        }
        if (this.client != null && !this.done) {
            if (System.currentTimeMillis() > this.timeOutAt) {
                this.done = true;
            }
            else if (this.client != null && this.client.loggedIn) {
                if (TimeSync.logger.isLoggable(Level.FINER)) {
                    TimeSync.logger.finer("3.5 sentcommand=" + this.sentCommand);
                }
                if (!this.sentCommand) {
                    try {
                        this.client.executeSyncCommand();
                        this.timeOutAt = System.currentTimeMillis() + this.timeOutTime;
                        this.sentCommand = true;
                    }
                    catch (IOException iox) {
                        TimeSync.logger.log(Level.WARNING, iox.getMessage(), iox);
                        this.done = true;
                    }
                }
            }
            if (!this.done) {
                try {
                    this.client.update();
                }
                catch (Exception ex) {
                    TimeSync.logger.log(Level.WARNING, ex.getMessage(), ex);
                    this.done = true;
                }
            }
        }
        if (this.client != null && this.done) {
            this.client.disconnect("Done");
            this.client = null;
        }
        return this.done;
    }
    
    @Override
    public void commandExecuted(final IntraClient aClient) {
        this.done = true;
    }
    
    @Override
    public void commandFailed(final IntraClient aClient) {
        TimeSync.logger.warning("Command failed for Client: " + aClient);
        this.done = true;
    }
    
    @Override
    public void dataReceived(final IntraClient aClient) {
        this.done = true;
    }
    
    @Override
    public void reschedule(final IntraClient aClient) {
        this.done = true;
    }
    
    @Override
    public void remove(final IntraClient aClient) {
        this.done = true;
    }
    
    @Override
    public void receivingData(final ByteBuffer buffer) {
        this.done = true;
    }
    
    static {
        logger = Logger.getLogger(TimeSync.class.getName());
    }
    
    public static class TimeSyncSender implements Runnable
    {
        public TimeSyncSender() {
            if (Servers.isThisLoginServer()) {
                throw new IllegalArgumentException("Do not send TimeSync commands from the LoginServer");
            }
        }
        
        @Override
        public void run() {
            if (TimeSync.logger.isLoggable(Level.FINER)) {
                TimeSync.logger.finer("Running newSingleThreadScheduledExecutor for sending TimeSync commands");
            }
            try {
                final long now = System.currentTimeMillis();
                final TimeSync synch = new TimeSync();
                Server.getInstance().addIntraCommand(synch);
                final long lElapsedTime = System.currentTimeMillis() - now;
                if (lElapsedTime > Constants.lagThreshold) {
                    TimeSync.logger.info("Finished sending TimeSync command, which took " + lElapsedTime + " millis.");
                }
            }
            catch (RuntimeException e) {
                TimeSync.logger.log(Level.WARNING, "Caught exception in ScheduledExecutorService while sending TimeSync command", e);
                throw e;
            }
        }
    }
}
