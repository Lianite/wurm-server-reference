// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import com.wurmonline.shared.util.StreamUtilities;
import java.util.logging.Level;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import com.wurmonline.server.ServerEntry;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.Players;
import com.wurmonline.server.Servers;
import java.io.IOException;
import com.wurmonline.shared.constants.PlayerOnlineStatus;
import java.util.logging.Logger;

public class PlayerState
{
    private static final Logger logger;
    private int serverId;
    private long playerId;
    private String playerName;
    private long lastLogin;
    private long lastLogout;
    private PlayerOnlineStatus state;
    
    public PlayerState(final long aWurmId) {
        final PlayerInfo pInfo = PlayerInfoFactory.getPlayerInfoWithWurmId(aWurmId);
        this.playerId = aWurmId;
        if (pInfo != null) {
            try {
                pInfo.load();
            }
            catch (IOException ex) {}
        }
        if (pInfo != null && pInfo.loaded) {
            this.serverId = pInfo.currentServer;
            this.lastLogin = pInfo.getLastLogin();
            this.lastLogout = pInfo.lastLogout;
            this.playerName = pInfo.getName();
            if (pInfo.currentServer != Servers.getLocalServerId()) {
                this.state = PlayerOnlineStatus.OTHER_SERVER;
            }
            else {
                PlayerOnlineStatus onoff;
                try {
                    final Player p = Players.getInstance().getPlayer(pInfo.wurmId);
                    onoff = PlayerOnlineStatus.ONLINE;
                }
                catch (NoSuchPlayerException e) {
                    onoff = PlayerOnlineStatus.OFFLINE;
                }
                this.state = onoff;
            }
        }
        else if (pInfo != null) {
            this.serverId = -1;
            this.lastLogin = 0L;
            this.lastLogout = 0L;
            this.playerName = "Error Loading";
            this.state = PlayerOnlineStatus.UNKNOWN;
        }
        else {
            this.serverId = -1;
            this.lastLogin = 0L;
            this.lastLogout = 0L;
            this.playerName = "Deleted";
            this.state = PlayerOnlineStatus.DELETE_ME;
        }
    }
    
    public PlayerState(final Player player, final long aWhenStateChanged, final PlayerOnlineStatus aState) {
        this(Servers.getLocalServerId(), player.getWurmId(), player.getName(), aWhenStateChanged, aState);
    }
    
    public PlayerState(final long aPlayerId, final String aPlayerName, final long aWhenStateChanged, final PlayerOnlineStatus aState) {
        this(Servers.getLocalServerId(), aPlayerId, aPlayerName, aWhenStateChanged, aState);
    }
    
    public PlayerState(final int aServerId, final long aPlayerId, final String aPlayerName, final long aWhenStateChanged, final PlayerOnlineStatus aState) {
        this.serverId = aServerId;
        this.playerId = aPlayerId;
        this.playerName = aPlayerName;
        this.state = aState;
        if (aState == PlayerOnlineStatus.ONLINE) {
            this.lastLogin = aWhenStateChanged;
            this.lastLogout = 0L;
        }
        else {
            this.lastLogin = 0L;
            this.lastLogout = aWhenStateChanged;
        }
    }
    
    public PlayerState(final long aPlayerId, final String aPlayerName, final long aLastLogin, final long aLastLogout, final PlayerOnlineStatus aState) {
        this(Servers.getLocalServerId(), aPlayerId, aPlayerName, aLastLogin, aLastLogout, aState);
    }
    
    public PlayerState(final int aServerId, final long aPlayerId, final String aPlayerName, final long aLastLogin, final long aLastLogout, final PlayerOnlineStatus aState) {
        this.serverId = aServerId;
        this.playerId = aPlayerId;
        this.playerName = aPlayerName;
        this.lastLogin = aLastLogin;
        this.lastLogout = aLastLogout;
        this.state = aState;
    }
    
    public PlayerState(final byte[] aData) {
        this.decode(aData);
    }
    
    public int getServerId() {
        return this.serverId;
    }
    
    public String getServerName() {
        final ServerEntry server = Servers.getServerWithId(this.serverId);
        if (server == null) {
            return "Unknown";
        }
        return server.getName();
    }
    
    public long getPlayerId() {
        return this.playerId;
    }
    
    public String getPlayerName() {
        return this.playerName;
    }
    
    public long getLastLogin() {
        return this.lastLogin;
    }
    
    public long getLastLogout() {
        return this.lastLogout;
    }
    
    public long getWhenStateChanged() {
        return Math.max(this.lastLogin, this.lastLogout);
    }
    
    public PlayerOnlineStatus getState() {
        return this.state;
    }
    
    public void setState(final PlayerOnlineStatus aState) {
        this.state = aState;
    }
    
    public void setStatus(final int aServerId, final PlayerOnlineStatus aState, final long aWhenStateChanged) {
        this.serverId = aServerId;
        this.state = aState;
        if (aState == PlayerOnlineStatus.ONLINE) {
            this.lastLogin = aWhenStateChanged;
        }
        else {
            this.lastLogout = aWhenStateChanged;
        }
    }
    
    final void decode(final byte[] aData) {
        DataInputStream dis = null;
        try {
            dis = new DataInputStream(new ByteArrayInputStream(aData));
            this.serverId = dis.readInt();
            this.playerId = dis.readLong();
            this.playerName = dis.readUTF();
            this.lastLogin = dis.readLong();
            this.lastLogout = dis.readLong();
            this.state = PlayerOnlineStatus.playerOnlineStatusFromId(dis.readByte());
        }
        catch (IOException ex) {
            PlayerState.logger.log(Level.WARNING, "Unpack exception " + ex.getMessage(), ex);
        }
        finally {
            StreamUtilities.closeInputStreamIgnoreExceptions(dis);
        }
    }
    
    public final byte[] encode() {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = null;
        byte[] barr = null;
        try {
            dos = new DataOutputStream(bos);
            dos.writeInt(this.serverId);
            dos.writeLong(this.playerId);
            dos.writeUTF(this.playerName);
            dos.writeLong(this.lastLogin);
            dos.writeLong(this.lastLogout);
            dos.writeByte(this.state.getId());
            dos.flush();
            dos.close();
        }
        catch (Exception ex) {
            PlayerState.logger.log(Level.WARNING, ex.getMessage(), ex);
        }
        finally {
            StreamUtilities.closeOutputStreamIgnoreExceptions(dos);
            barr = bos.toByteArray();
            StreamUtilities.closeOutputStreamIgnoreExceptions(bos);
        }
        return barr;
    }
    
    @Override
    public String toString() {
        return "PlayerState [ServerId=" + this.serverId + ", playerId=" + this.playerId + ", playerName=" + this.playerName + ", whenStateChanged=" + this.getWhenStateChanged() + ", state=" + this.state + "]";
    }
    
    static {
        logger = Logger.getLogger(PlayerState.class.getName());
    }
}
