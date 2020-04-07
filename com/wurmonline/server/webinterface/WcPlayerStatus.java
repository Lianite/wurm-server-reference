// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.webinterface;

import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.Servers;
import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import com.wurmonline.shared.util.StreamUtilities;
import java.util.logging.Level;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import com.wurmonline.server.players.PlayerState;
import com.wurmonline.server.WurmId;
import com.wurmonline.shared.constants.PlayerOnlineStatus;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public final class WcPlayerStatus extends WebCommand implements MiscConstants
{
    private static final Logger logger;
    public static final byte DO_NOTHING = 0;
    public static final byte WHOS_ONLINE = 1;
    public static final byte STATUS_CHANGE = 2;
    private byte type;
    private String playerName;
    private long playerWurmId;
    private long lastLogin;
    private long lastLogout;
    private int currentServerId;
    private PlayerOnlineStatus status;
    
    public WcPlayerStatus() {
        super(WurmId.getNextWCCommandId(), (short)19);
        this.type = 0;
        this.type = 1;
    }
    
    public WcPlayerStatus(final PlayerState pState) {
        super(WurmId.getNextWCCommandId(), (short)19);
        this.type = 0;
        this.type = 2;
        this.playerName = pState.getPlayerName();
        this.playerWurmId = pState.getPlayerId();
        this.lastLogin = pState.getLastLogin();
        this.lastLogout = pState.getLastLogout();
        this.currentServerId = pState.getServerId();
        this.status = pState.getState();
    }
    
    public WcPlayerStatus(final String aPlayerName, final long aPlayerWurmId, final long aLastLogin, final long aLastLogout, final int aCurrentServerId, final PlayerOnlineStatus aStatus) {
        super(WurmId.getNextWCCommandId(), (short)19);
        this.type = 0;
        this.type = 2;
        this.playerName = aPlayerName;
        this.playerWurmId = aPlayerWurmId;
        this.lastLogin = aLastLogin;
        this.lastLogout = aLastLogout;
        this.currentServerId = aCurrentServerId;
        this.status = aStatus;
    }
    
    public WcPlayerStatus(final long aId, final byte[] aData) {
        super(aId, (short)19, aData);
        this.type = 0;
    }
    
    @Override
    public boolean autoForward() {
        return false;
    }
    
    @Override
    byte[] encode() {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = null;
        byte[] barr = null;
        try {
            dos = new DataOutputStream(bos);
            dos.writeByte(this.type);
            switch (this.type) {
                case 2: {
                    dos.writeUTF(this.playerName);
                    dos.writeLong(this.playerWurmId);
                    dos.writeLong(this.lastLogin);
                    dos.writeLong(this.lastLogout);
                    dos.writeInt(this.currentServerId);
                    dos.writeByte(this.status.getId());
                    break;
                }
            }
            dos.flush();
            dos.close();
        }
        catch (Exception ex) {
            WcPlayerStatus.logger.log(Level.WARNING, ex.getMessage(), ex);
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
                    dis = new DataInputStream(new ByteArrayInputStream(WcPlayerStatus.this.getData()));
                    WcPlayerStatus.this.type = dis.readByte();
                    switch (WcPlayerStatus.this.type) {
                        case 2: {
                            WcPlayerStatus.this.playerName = dis.readUTF();
                            WcPlayerStatus.this.playerWurmId = dis.readLong();
                            WcPlayerStatus.this.lastLogin = dis.readLong();
                            WcPlayerStatus.this.lastLogout = dis.readLong();
                            WcPlayerStatus.this.currentServerId = dis.readInt();
                            WcPlayerStatus.this.status = PlayerOnlineStatus.playerOnlineStatusFromId(dis.readByte());
                            break;
                        }
                    }
                }
                catch (IOException ex) {
                    WcPlayerStatus.logger.log(Level.WARNING, "Unpack exception " + ex.getMessage(), ex);
                    return;
                }
                finally {
                    StreamUtilities.closeInputStreamIgnoreExceptions(dis);
                }
                if (WcPlayerStatus.this.type == 1) {
                    if (!Servers.isThisLoginServer()) {
                        PlayerInfoFactory.whosOnline();
                    }
                }
                else if (WcPlayerStatus.this.type == 2) {
                    final PlayerState pState = new PlayerState(WcPlayerStatus.this.currentServerId, WcPlayerStatus.this.playerWurmId, WcPlayerStatus.this.playerName, WcPlayerStatus.this.lastLogin, WcPlayerStatus.this.lastLogout, WcPlayerStatus.this.status);
                    PlayerInfoFactory.updatePlayerState(pState);
                    if (Servers.isThisLoginServer()) {
                        WcPlayerStatus.this.sendFromLoginServer();
                    }
                }
            }
        }.start();
    }
    
    static {
        logger = Logger.getLogger(WcPlayerStatus.class.getName());
    }
}
