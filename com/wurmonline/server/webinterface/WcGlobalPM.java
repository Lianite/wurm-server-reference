// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.webinterface;

import com.wurmonline.server.players.Player;
import com.wurmonline.server.players.PlayerInfo;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.Players;
import com.wurmonline.server.Servers;
import com.wurmonline.server.WurmId;
import com.wurmonline.server.players.PlayerInfoFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import com.wurmonline.shared.util.StreamUtilities;
import java.util.logging.Level;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public class WcGlobalPM extends WebCommand implements MiscConstants
{
    private static final Logger logger;
    public static final byte GETID = 0;
    public static final byte THE_ID = 2;
    public static final byte SEND = 3;
    public static final byte IGNORED = 5;
    public static final byte NA = 6;
    public static final byte AFK = 7;
    private byte action;
    private byte power;
    private long senderId;
    private String senderName;
    private byte kingdom;
    private int targetServerId;
    private long targetId;
    private String targetName;
    private boolean friend;
    private String message;
    private boolean emote;
    private boolean override;
    
    public WcGlobalPM(final long aId, final byte _action, final byte _power, final long _senderId, final String _senderName, final byte _kingdom, final int _targetServerId, final long _targetId, final String _targetName, final boolean _friend, final String _message, final boolean _emote, final boolean aOverride) {
        super(aId, (short)17);
        this.action = 3;
        this.power = 0;
        this.senderId = -10L;
        this.senderName = "unknown";
        this.kingdom = 0;
        this.targetServerId = 0;
        this.targetId = -10L;
        this.targetName = "unknown";
        this.friend = false;
        this.message = "";
        this.emote = false;
        this.override = false;
        this.action = _action;
        this.power = _power;
        this.senderId = _senderId;
        this.senderName = _senderName;
        this.kingdom = _kingdom;
        this.targetServerId = _targetServerId;
        this.targetId = _targetId;
        this.targetName = _targetName;
        this.friend = _friend;
        this.message = _message;
        this.emote = _emote;
        this.override = aOverride;
    }
    
    public WcGlobalPM(final long _id, final byte[] _data) {
        super(_id, (short)17, _data);
        this.action = 3;
        this.power = 0;
        this.senderId = -10L;
        this.senderName = "unknown";
        this.kingdom = 0;
        this.targetServerId = 0;
        this.targetId = -10L;
        this.targetName = "unknown";
        this.friend = false;
        this.message = "";
        this.emote = false;
        this.override = false;
    }
    
    @Override
    public boolean autoForward() {
        return false;
    }
    
    public byte[] encode() {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = null;
        byte[] barr = null;
        try {
            dos = new DataOutputStream(bos);
            dos.writeByte(this.action);
            dos.writeByte(this.power);
            dos.writeLong(this.senderId);
            dos.writeUTF(this.senderName);
            dos.writeByte(this.kingdom);
            dos.writeInt(this.targetServerId);
            dos.writeLong(this.targetId);
            dos.writeUTF(this.targetName);
            dos.writeBoolean(this.friend);
            dos.writeUTF(this.message);
            dos.writeBoolean(this.emote);
            dos.writeBoolean(this.override);
            dos.flush();
            dos.close();
        }
        catch (Exception ex) {
            WcGlobalPM.logger.log(Level.WARNING, ex.getMessage(), ex);
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
                    dis = new DataInputStream(new ByteArrayInputStream(WcGlobalPM.this.getData()));
                    WcGlobalPM.this.action = dis.readByte();
                    WcGlobalPM.this.power = dis.readByte();
                    WcGlobalPM.this.senderId = dis.readLong();
                    WcGlobalPM.this.senderName = dis.readUTF();
                    WcGlobalPM.this.kingdom = dis.readByte();
                    WcGlobalPM.this.targetServerId = dis.readInt();
                    WcGlobalPM.this.targetId = dis.readLong();
                    WcGlobalPM.this.targetName = dis.readUTF();
                    WcGlobalPM.this.friend = dis.readBoolean();
                    WcGlobalPM.this.message = dis.readUTF();
                    WcGlobalPM.this.emote = dis.readBoolean();
                    WcGlobalPM.this.override = dis.readBoolean();
                }
                catch (IOException ex) {
                    WcGlobalPM.logger.log(Level.WARNING, "Unpack exception " + ex.getMessage(), ex);
                    return;
                }
                finally {
                    StreamUtilities.closeInputStreamIgnoreExceptions(dis);
                }
                if (WcGlobalPM.this.action == 0) {
                    final PlayerInfo pInfo = PlayerInfoFactory.createPlayerInfo(WcGlobalPM.this.targetName);
                    if (pInfo != null) {
                        try {
                            pInfo.load();
                            WcGlobalPM.this.targetId = pInfo.wurmId;
                            WcGlobalPM.this.targetServerId = pInfo.currentServer;
                        }
                        catch (IOException ex2) {
                            WcGlobalPM.this.targetId = -10L;
                        }
                    }
                    final WcGlobalPM wgi = new WcGlobalPM(WurmId.getNextWCCommandId(), (byte)2, WcGlobalPM.this.power, WcGlobalPM.this.senderId, WcGlobalPM.this.senderName, WcGlobalPM.this.kingdom, WcGlobalPM.this.targetServerId, WcGlobalPM.this.targetId, WcGlobalPM.this.targetName, WcGlobalPM.this.friend, WcGlobalPM.this.message, WcGlobalPM.this.emote, WcGlobalPM.this.override);
                    wgi.sendToServer(WurmId.getOrigin(WcGlobalPM.this.getWurmId()));
                }
                else if (WcGlobalPM.this.action == 3) {
                    final PlayerInfo pInfo = PlayerInfoFactory.createPlayerInfo(WcGlobalPM.this.targetName);
                    if (pInfo == null) {
                        WcGlobalPM.logger.log(Level.WARNING, "no player '" + WcGlobalPM.this.targetName + "' Info?");
                        return;
                    }
                    WcGlobalPM.this.targetServerId = pInfo.currentServer;
                    if (pInfo.currentServer == Servers.getLocalServerId()) {
                        try {
                            final Player p = Players.getInstance().getPlayer(WcGlobalPM.this.targetName);
                            if (!p.sendPM(WcGlobalPM.this.power, WcGlobalPM.this.senderName, WcGlobalPM.this.senderId, WcGlobalPM.this.friend, WcGlobalPM.this.message, WcGlobalPM.this.emote, WcGlobalPM.this.kingdom, WurmId.getOrigin(WcGlobalPM.this.getWurmId()), WcGlobalPM.this.override)) {
                                final WcGlobalPM wgi2 = new WcGlobalPM(WurmId.getNextWCCommandId(), (byte)6, WcGlobalPM.this.power, WcGlobalPM.this.senderId, WcGlobalPM.this.senderName, WcGlobalPM.this.kingdom, WcGlobalPM.this.targetServerId, WcGlobalPM.this.targetId, WcGlobalPM.this.targetName, WcGlobalPM.this.friend, WcGlobalPM.this.message, WcGlobalPM.this.emote, WcGlobalPM.this.override);
                                wgi2.sendToServer(WurmId.getOrigin(WcGlobalPM.this.getWurmId()));
                            }
                            else if (p.isAFK()) {
                                final WcGlobalPM wgi2 = new WcGlobalPM(WurmId.getNextWCCommandId(), (byte)7, WcGlobalPM.this.power, WcGlobalPM.this.senderId, WcGlobalPM.this.senderName, WcGlobalPM.this.kingdom, WcGlobalPM.this.targetServerId, WcGlobalPM.this.targetId, WcGlobalPM.this.targetName, WcGlobalPM.this.friend, p.getAFKMessage(), true, WcGlobalPM.this.override);
                                wgi2.sendToServer(WurmId.getOrigin(WcGlobalPM.this.getWurmId()));
                            }
                        }
                        catch (NoSuchPlayerException e) {
                            final WcGlobalPM wgi2 = new WcGlobalPM(WurmId.getNextWCCommandId(), (byte)6, WcGlobalPM.this.power, WcGlobalPM.this.senderId, WcGlobalPM.this.senderName, WcGlobalPM.this.kingdom, WcGlobalPM.this.targetServerId, WcGlobalPM.this.targetId, WcGlobalPM.this.targetName, WcGlobalPM.this.friend, WcGlobalPM.this.message, WcGlobalPM.this.emote, WcGlobalPM.this.override);
                            wgi2.sendToServer(WurmId.getOrigin(WcGlobalPM.this.getWurmId()));
                        }
                    }
                    else if (Servers.isThisLoginServer()) {
                        final WcGlobalPM wgi = new WcGlobalPM(WcGlobalPM.this.getWurmId(), WcGlobalPM.this.action, WcGlobalPM.this.power, WcGlobalPM.this.senderId, WcGlobalPM.this.senderName, WcGlobalPM.this.kingdom, WcGlobalPM.this.targetServerId, WcGlobalPM.this.targetId, WcGlobalPM.this.targetName, WcGlobalPM.this.friend, WcGlobalPM.this.message, WcGlobalPM.this.emote, WcGlobalPM.this.override);
                        wgi.sendToServer(pInfo.currentServer);
                    }
                    else {
                        WcGlobalPM.logger.log(Level.WARNING, "not on login or " + WcGlobalPM.this.targetName + "'s server!");
                    }
                }
                else {
                    try {
                        final Player p2 = Players.getInstance().getPlayer(WcGlobalPM.this.senderName);
                        p2.sendPM(WcGlobalPM.this.action, WcGlobalPM.this.targetName, WcGlobalPM.this.targetId, WcGlobalPM.this.message, WcGlobalPM.this.emote, WcGlobalPM.this.override);
                    }
                    catch (NoSuchPlayerException ex3) {}
                }
            }
        }.start();
    }
    
    static {
        logger = Logger.getLogger(WcGlobalPM.class.getName());
    }
}
