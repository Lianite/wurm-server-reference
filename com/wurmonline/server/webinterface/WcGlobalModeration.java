// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.webinterface;

import com.wurmonline.server.players.PlayerInfo;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.support.Trello;
import com.wurmonline.server.NoSuchPlayerException;
import java.io.IOException;
import com.wurmonline.server.Servers;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.Message;
import com.wurmonline.server.Players;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import com.wurmonline.shared.util.StreamUtilities;
import java.util.logging.Level;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.logging.Logger;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.server.MiscConstants;

public final class WcGlobalModeration extends WebCommand implements MiscConstants, TimeConstants
{
    private static final Logger logger;
    private boolean warning;
    private boolean ban;
    private boolean mute;
    private boolean unmute;
    private boolean muteWarn;
    private int hours;
    private int days;
    private String sender;
    private String reason;
    private String playerName;
    private byte senderPower;
    
    public WcGlobalModeration(final long id, final String _sender, final byte _senderPower, final boolean _mute, final boolean _unmute, final boolean _mutewarn, final boolean _ban, final boolean _warning, final int _hours, final int _days, final String _playerName, final String _reason) {
        super(id, (short)14);
        this.sender = "";
        this.reason = "";
        this.playerName = "";
        this.senderPower = 0;
        this.sender = _sender;
        this.warning = _warning;
        this.ban = _ban;
        this.mute = _mute;
        this.unmute = _unmute;
        this.muteWarn = _mutewarn;
        this.hours = _hours;
        this.days = _days;
        this.reason = _reason;
        this.playerName = _playerName;
        this.senderPower = _senderPower;
    }
    
    public WcGlobalModeration(final long id, final byte[] data) {
        super(id, (short)14, data);
        this.sender = "";
        this.reason = "";
        this.playerName = "";
        this.senderPower = 0;
    }
    
    @Override
    public boolean autoForward() {
        return true;
    }
    
    @Override
    byte[] encode() {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = null;
        byte[] barr = null;
        try {
            dos = new DataOutputStream(bos);
            dos.writeUTF(this.sender);
            dos.writeBoolean(this.ban);
            dos.writeBoolean(this.mute);
            dos.writeBoolean(this.unmute);
            dos.writeBoolean(this.muteWarn);
            dos.writeBoolean(this.warning);
            dos.writeUTF(this.playerName);
            dos.writeUTF(this.reason);
            dos.writeInt(this.days);
            dos.writeInt(this.hours);
            dos.writeByte(this.senderPower);
            dos.flush();
            dos.close();
        }
        catch (Exception ex) {
            WcGlobalModeration.logger.log(Level.WARNING, ex.getMessage(), ex);
        }
        finally {
            StreamUtilities.closeOutputStreamIgnoreExceptions(dos);
            barr = bos.toByteArray();
            StreamUtilities.closeOutputStreamIgnoreExceptions(bos);
            this.setData(barr);
        }
        return barr;
    }
    
    @Override
    public void execute() {
        new Thread() {
            @Override
            public void run() {
                DataInputStream dis = null;
                try {
                    dis = new DataInputStream(new ByteArrayInputStream(WcGlobalModeration.this.getData()));
                    WcGlobalModeration.this.sender = dis.readUTF();
                    WcGlobalModeration.this.ban = dis.readBoolean();
                    WcGlobalModeration.this.mute = dis.readBoolean();
                    WcGlobalModeration.this.unmute = dis.readBoolean();
                    WcGlobalModeration.this.muteWarn = dis.readBoolean();
                    WcGlobalModeration.this.warning = dis.readBoolean();
                    WcGlobalModeration.this.playerName = dis.readUTF();
                    WcGlobalModeration.this.reason = dis.readUTF();
                    WcGlobalModeration.this.days = dis.readInt();
                    WcGlobalModeration.this.hours = dis.readInt();
                    WcGlobalModeration.this.senderPower = dis.readByte();
                    Label_0814: {
                        try {
                            final Player p = Players.getInstance().getPlayer(WcGlobalModeration.this.playerName);
                            if (WcGlobalModeration.this.ban && p.getPower() < WcGlobalModeration.this.senderPower) {
                                try {
                                    final Message mess = new Message(null, (byte)3, ":Event", "You have been banned for " + WcGlobalModeration.this.days + " days and thrown out from the game.");
                                    mess.setReceiver(p.getWurmId());
                                    Server.getInstance().addMessage(mess);
                                    p.ban(WcGlobalModeration.this.reason, System.currentTimeMillis() + WcGlobalModeration.this.days * 86400000L + WcGlobalModeration.this.hours * 3600000L);
                                }
                                catch (Exception ex) {
                                    WcGlobalModeration.logger.log(Level.WARNING, ex.getMessage());
                                }
                            }
                            if (WcGlobalModeration.this.mute && p.getPower() <= WcGlobalModeration.this.senderPower) {
                                p.mute(true, WcGlobalModeration.this.reason, System.currentTimeMillis() + WcGlobalModeration.this.days * 86400000L + WcGlobalModeration.this.hours * 3600000L);
                                final Message mess = new Message(null, (byte)3, ":Event", "You have been muted by " + WcGlobalModeration.this.sender + " for " + WcGlobalModeration.this.hours + " hours and cannot shout anymore. Reason: " + WcGlobalModeration.this.reason);
                                mess.setReceiver(p.getWurmId());
                                Server.getInstance().addMessage(mess);
                            }
                            if (WcGlobalModeration.this.unmute) {
                                p.mute(false, "", 0L);
                                final Message mess = new Message(null, (byte)3, ":Event", "You have been given your voice back and can shout again.");
                                mess.setReceiver(p.getWurmId());
                                Server.getInstance().addMessage(mess);
                            }
                            if (WcGlobalModeration.this.muteWarn && p.getPower() <= WcGlobalModeration.this.senderPower) {
                                final Message mess = new Message(null, (byte)3, ":Event", WcGlobalModeration.this.sender + " issues a warning that you may be muted. Be silent for a while and try to understand why or change the subject of your conversation please.");
                                mess.setReceiver(p.getWurmId());
                                Server.getInstance().addMessage(mess);
                                if (WcGlobalModeration.this.reason.length() > 0) {
                                    final Message mess2 = new Message(null, (byte)3, ":Event", "The reason for this is '" + WcGlobalModeration.this.reason + "'");
                                    mess2.setReceiver(p.getWurmId());
                                    Server.getInstance().addMessage(mess2);
                                }
                            }
                            if (WcGlobalModeration.this.warning && p.getPower() < WcGlobalModeration.this.senderPower) {
                                p.getSaveFile().warn();
                                final Message mess = new Message(null, (byte)3, ":Event", "You have just received an official warning. Too many of these will get you banned from the game.");
                                mess.setReceiver(p.getWurmId());
                                Server.getInstance().addMessage(mess);
                            }
                        }
                        catch (NoSuchPlayerException nsp) {
                            if (WcGlobalModeration.this.unmute) {
                                try {
                                    final PlayerInfo pinf = PlayerInfoFactory.createPlayerInfo(WcGlobalModeration.this.playerName);
                                    pinf.load();
                                    if (pinf.wurmId > 0L) {
                                        pinf.setMuted(false, "", 0L);
                                    }
                                }
                                catch (IOException ex2) {
                                    if (!Servers.isThisATestServer()) {
                                        break Label_0814;
                                    }
                                    WcGlobalModeration.logger.log(Level.WARNING, "Unable to find player:" + WcGlobalModeration.this.playerName + "." + ex2.getMessage(), ex2);
                                }
                            }
                        }
                    }
                    if (WcGlobalModeration.this.mute) {
                        Players.addMgmtMessage(WcGlobalModeration.this.sender, "mutes " + WcGlobalModeration.this.playerName + " for " + WcGlobalModeration.this.hours + " hours. Reason: " + WcGlobalModeration.this.reason);
                        final Message mess3 = new Message(null, (byte)9, "MGMT", "<" + WcGlobalModeration.this.sender + "> mutes " + WcGlobalModeration.this.playerName + " for " + WcGlobalModeration.this.hours + " hours. Reason: " + WcGlobalModeration.this.reason);
                        Server.getInstance().addMessage(mess3);
                    }
                    if (WcGlobalModeration.this.unmute) {
                        Players.addMgmtMessage(WcGlobalModeration.this.sender, "unmutes " + WcGlobalModeration.this.playerName);
                        final Message mess3 = new Message(null, (byte)9, "MGMT", "<" + WcGlobalModeration.this.sender + "> unmutes " + WcGlobalModeration.this.playerName);
                        Server.getInstance().addMessage(mess3);
                    }
                    if (WcGlobalModeration.this.muteWarn) {
                        Players.addMgmtMessage(WcGlobalModeration.this.sender, "mutewarns " + WcGlobalModeration.this.playerName + " (" + WcGlobalModeration.this.reason + ")");
                        final Message mess3 = new Message(null, (byte)9, "MGMT", "<" + WcGlobalModeration.this.sender + "> mutewarns " + WcGlobalModeration.this.playerName + " (" + WcGlobalModeration.this.reason + ")");
                        Server.getInstance().addMessage(mess3);
                    }
                    if (Servers.isThisLoginServer() && (WcGlobalModeration.this.mute || WcGlobalModeration.this.muteWarn || WcGlobalModeration.this.unmute)) {
                        Trello.addMessage(WcGlobalModeration.this.sender, WcGlobalModeration.this.playerName, WcGlobalModeration.this.reason, WcGlobalModeration.this.hours);
                    }
                }
                catch (IOException ex3) {
                    WcGlobalModeration.logger.log(Level.WARNING, "Unpack exception " + ex3.getMessage(), ex3);
                }
                finally {
                    StreamUtilities.closeInputStreamIgnoreExceptions(dis);
                }
            }
        }.start();
    }
    
    static {
        logger = Logger.getLogger(WcGlobalModeration.class.getName());
    }
}
