// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.webinterface;

import com.wurmonline.server.players.PlayerInfo;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.NoSuchPlayerException;
import java.io.IOException;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.Players;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import com.wurmonline.shared.util.StreamUtilities;
import java.util.logging.Level;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import com.wurmonline.server.WurmId;
import java.util.logging.Logger;

public class WcSetPower extends WebCommand
{
    private static final Logger logger;
    private String playerName;
    private int newPower;
    private String senderName;
    private int senderPower;
    private String response;
    
    public WcSetPower(final String playerName, final int newPower, final String senderName, final int senderPower, final String response) {
        this();
        this.playerName = playerName;
        this.newPower = newPower;
        this.senderName = senderName;
        this.senderPower = senderPower;
        this.response = response;
    }
    
    WcSetPower(final WcSetPower copy) {
        this();
        this.playerName = copy.playerName;
        this.newPower = copy.newPower;
        this.senderName = copy.senderName;
        this.senderPower = copy.senderPower;
        this.response = copy.response;
    }
    
    WcSetPower() {
        super(WurmId.getNextWCCommandId(), (short)33);
    }
    
    public WcSetPower(final long aId, final byte[] _data) {
        super(aId, (short)33, _data);
    }
    
    @Override
    byte[] encode() {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = null;
        byte[] byteArr = null;
        try {
            dos = new DataOutputStream(bos);
            dos.writeUTF(this.playerName);
            dos.writeInt(this.newPower);
            dos.writeUTF(this.senderName);
            dos.writeInt(this.senderPower);
            dos.writeUTF(this.response);
            dos.flush();
            dos.close();
        }
        catch (Exception ex) {
            WcSetPower.logger.log(Level.WARNING, ex.getMessage(), ex);
        }
        finally {
            StreamUtilities.closeOutputStreamIgnoreExceptions(dos);
            byteArr = bos.toByteArray();
            StreamUtilities.closeOutputStreamIgnoreExceptions(bos);
            this.setData(byteArr);
        }
        return byteArr;
    }
    
    @Override
    public boolean autoForward() {
        return false;
    }
    
    @Override
    public void execute() {
        new Thread() {
            @Override
            public void run() {
                DataInputStream dis = null;
                try {
                    dis = new DataInputStream(new ByteArrayInputStream(WcSetPower.this.getData()));
                    WcSetPower.this.playerName = dis.readUTF();
                    WcSetPower.this.newPower = dis.readInt();
                    WcSetPower.this.senderName = dis.readUTF();
                    WcSetPower.this.senderPower = dis.readInt();
                    WcSetPower.this.response = dis.readUTF();
                    Label_0436: {
                        if (!WcSetPower.this.response.equals("")) {
                            try {
                                final Player sender = Players.getInstance().getPlayer(WcSetPower.this.senderName);
                                sender.getCommunicator().sendSafeServerMessage(WcSetPower.this.response);
                                return;
                            }
                            catch (Exception ex2) {
                                break Label_0436;
                            }
                        }
                        try {
                            final Player p = Players.getInstance().getPlayer(WcSetPower.this.playerName);
                            if (p.getPower() > WcSetPower.this.senderPower) {
                                WcSetPower.this.response = "They are more powerful than you. You cannot set their power.";
                            }
                            else {
                                p.setPower((byte)WcSetPower.this.newPower);
                                final String powerName = this.getPowerName(WcSetPower.this.newPower);
                                p.getCommunicator().sendSafeServerMessage("Your status has been set by " + WcSetPower.this.senderName + " to " + powerName + "!");
                                WcSetPower.this.response = "You set the power of " + WcSetPower.this.playerName + " to the status of " + powerName;
                            }
                        }
                        catch (NoSuchPlayerException playerEx) {
                            final PlayerInfo pinf = PlayerInfoFactory.createPlayerInfo(WcSetPower.this.playerName);
                            try {
                                pinf.load();
                                if (pinf.getPower() > WcSetPower.this.senderPower) {
                                    WcSetPower.this.response = "They are more powerful than you. You cannot set their power.";
                                }
                                else {
                                    pinf.setPower((byte)WcSetPower.this.newPower);
                                    pinf.save();
                                    final String powerName2 = this.getPowerName(WcSetPower.this.newPower);
                                    WcSetPower.this.response = "You set the power of " + WcSetPower.this.playerName + " to the power of " + powerName2;
                                }
                            }
                            catch (IOException ioEx) {
                                WcSetPower.this.response = "Error trying load or save player information who is currently offline.";
                            }
                        }
                        catch (IOException ioEx2) {
                            WcSetPower.this.response = "Error trying to set the power on the player who is currently online.";
                        }
                    }
                }
                catch (IOException ex) {
                    WcSetPower.logger.log(Level.WARNING, "Unpack exception " + ex.getMessage(), ex);
                    WcSetPower.this.response = "Something went terribly wrong trying to set the power.";
                }
                finally {
                    StreamUtilities.closeInputStreamIgnoreExceptions(dis);
                }
                if (WcSetPower.this.response.equals("")) {
                    return;
                }
                try {
                    final WcSetPower wsp = new WcSetPower(WcSetPower.this);
                    wsp.sendToServer(WurmId.getOrigin(WcSetPower.this.getWurmId()));
                }
                catch (Exception e) {
                    WcSetPower.logger.log(Level.WARNING, "Could not send response back after setting power", e);
                }
            }
            
            private String getPowerName(final int power) {
                String powString = "normal adventurer";
                if (WcSetPower.this.newPower == 1) {
                    powString = "hero";
                }
                else if (WcSetPower.this.newPower == 2) {
                    powString = "demigod";
                }
                else if (WcSetPower.this.newPower == 3) {
                    powString = "high god";
                }
                else if (WcSetPower.this.newPower == 4) {
                    powString = "arch angel";
                }
                else if (WcSetPower.this.newPower == 5) {
                    powString = "implementor";
                }
                return powString;
            }
        }.start();
    }
    
    static {
        logger = Logger.getLogger(WcSetPower.class.getName());
    }
}
