// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import java.nio.ByteBuffer;
import com.wurmonline.communication.SocketConnection;
import com.wurmonline.server.Server;
import java.io.IOException;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.Players;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class PlayerCommunicatorSender implements Runnable
{
    private static Logger logger;
    
    public PlayerCommunicatorSender() {
        PlayerCommunicatorSender.logger.info("Creating");
    }
    
    @Override
    public void run() {
        PlayerCommunicatorSender.logger.info("Starting on " + Thread.currentThread());
        try {
            Player lPlayer = null;
            while (true) {
                final PlayerMessage lMessage = PlayerCommunicatorQueued.getMessageQueue().take();
                if (lMessage != null) {
                    if (PlayerCommunicatorSender.logger.isLoggable(Level.FINEST)) {
                        PlayerCommunicatorSender.logger.finest("Removed " + lMessage);
                    }
                    try {
                        lPlayer = Players.getInstance().getPlayer(lMessage.getPlayerId());
                        final SocketConnection lConnection = lPlayer.getCommunicator().getConnection();
                        if (lPlayer.hasLink() && lConnection.isConnected()) {
                            final ByteBuffer lBuffer = lConnection.getBuffer();
                            lBuffer.put(lMessage.getMessageBytes());
                            lConnection.flush();
                            if (!lConnection.tickWriting(1000000L)) {
                                PlayerCommunicatorSender.logger.warning("Could not get a lock within 1ms to send message: " + lMessage);
                            }
                            else if (PlayerCommunicatorSender.logger.isLoggable(Level.FINEST)) {
                                PlayerCommunicatorSender.logger.finest("Sent message through connection: " + lMessage);
                            }
                        }
                        else if (PlayerCommunicatorSender.logger.isLoggable(Level.FINEST)) {
                            PlayerCommunicatorSender.logger.finest("Player is not connected so cannot send message: " + lMessage);
                        }
                    }
                    catch (NoSuchPlayerException e) {
                        PlayerCommunicatorSender.logger.log(Level.WARNING, "Could not find Player for Message: " + lMessage + " - " + e.getMessage(), e);
                    }
                    catch (IOException e2) {
                        PlayerCommunicatorSender.logger.log(Level.WARNING, lPlayer.getName() + ": Message: " + lMessage + " - " + e2.getMessage(), e2);
                        lPlayer.setLink(false);
                    }
                    Thread.yield();
                }
                else {
                    PlayerCommunicatorSender.logger.warning("Removed null message from Queue");
                }
            }
        }
        catch (RuntimeException e3) {
            PlayerCommunicatorSender.logger.log(Level.WARNING, "Problem running - " + e3.getMessage(), e3);
            Server.getInstance().initialisePlayerCommunicatorSender();
        }
        catch (InterruptedException e4) {
            PlayerCommunicatorSender.logger.log(Level.WARNING, e4.getMessage(), e4);
            Server.getInstance().initialisePlayerCommunicatorSender();
        }
        finally {
            PlayerCommunicatorSender.logger.info("Finished");
        }
    }
    
    static {
        PlayerCommunicatorSender.logger = Logger.getLogger(PlayerCommunicatorSender.class.getName());
    }
}
