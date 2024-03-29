// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.webinterface;

import com.wurmonline.server.players.PlayerInfo;
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
import com.wurmonline.server.WurmId;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public final class WcRemoveFriendship extends WebCommand implements MiscConstants
{
    private static final Logger logger;
    private String playerName;
    private long playerWurmId;
    private String friendName;
    private long friendWurmId;
    
    public WcRemoveFriendship(final String aPlayerName, final long aPlayerWurmId, final String aFriendName, final long aFriendWurmId) {
        super(WurmId.getNextWCCommandId(), (short)4);
        this.playerName = aPlayerName;
        this.playerWurmId = aPlayerWurmId;
        this.friendName = aFriendName;
        this.friendWurmId = aFriendWurmId;
    }
    
    public WcRemoveFriendship(final long aId, final byte[] aData) {
        super(aId, (short)4, aData);
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
            dos.writeUTF(this.playerName);
            dos.writeLong(this.playerWurmId);
            dos.writeUTF(this.friendName);
            dos.writeLong(this.friendWurmId);
            dos.flush();
            dos.close();
        }
        catch (Exception ex) {
            WcRemoveFriendship.logger.log(Level.WARNING, ex.getMessage(), ex);
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
                    dis = new DataInputStream(new ByteArrayInputStream(WcRemoveFriendship.this.getData()));
                    WcRemoveFriendship.this.playerName = dis.readUTF();
                    WcRemoveFriendship.this.playerWurmId = dis.readLong();
                    WcRemoveFriendship.this.friendName = dis.readUTF();
                    WcRemoveFriendship.this.friendWurmId = dis.readLong();
                }
                catch (IOException ex) {
                    WcRemoveFriendship.logger.log(Level.WARNING, "Unpack exception " + ex.getMessage(), ex);
                    return;
                }
                finally {
                    StreamUtilities.closeInputStreamIgnoreExceptions(dis);
                }
                if (Servers.isThisLoginServer()) {
                    if (WcRemoveFriendship.this.friendWurmId == -10L) {
                        final PlayerInfo fInfo = PlayerInfoFactory.getPlayerInfoWithName(WcRemoveFriendship.this.friendName);
                        if (fInfo != null) {
                            WcRemoveFriendship.this.friendWurmId = fInfo.wurmId;
                        }
                    }
                    WcRemoveFriendship.this.sendFromLoginServer();
                }
                PlayerInfoFactory.breakFriendship(WcRemoveFriendship.this.playerName, WcRemoveFriendship.this.playerWurmId, WcRemoveFriendship.this.friendName, WcRemoveFriendship.this.friendWurmId);
            }
        }.start();
    }
    
    static {
        logger = Logger.getLogger(WcRemoveFriendship.class.getName());
    }
}
