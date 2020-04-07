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
import com.wurmonline.server.WurmId;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public final class WcExpelMember extends WebCommand implements MiscConstants
{
    private static final Logger logger;
    private long playerId;
    private byte fromKingdomId;
    private byte toKingdomId;
    private int originServer;
    
    public WcExpelMember(final long aPlayerId, final byte aFromKingdomId, final byte aToKingdomId, final int aOriginServer) {
        super(WurmId.getNextWCCommandId(), (short)30);
        this.playerId = aPlayerId;
        this.fromKingdomId = aFromKingdomId;
        this.toKingdomId = aToKingdomId;
        this.originServer = aOriginServer;
    }
    
    public WcExpelMember(final long aId, final byte[] aData) {
        super(aId, (short)30, aData);
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
            dos.writeLong(this.playerId);
            dos.writeByte(this.fromKingdomId);
            dos.writeByte(this.toKingdomId);
            dos.writeInt(this.originServer);
            dos.flush();
            dos.close();
        }
        catch (Exception ex) {
            WcExpelMember.logger.log(Level.WARNING, ex.getMessage(), ex);
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
                    dis = new DataInputStream(new ByteArrayInputStream(WcExpelMember.this.getData()));
                    WcExpelMember.this.playerId = dis.readLong();
                    WcExpelMember.this.fromKingdomId = dis.readByte();
                    WcExpelMember.this.toKingdomId = dis.readByte();
                    WcExpelMember.this.originServer = dis.readInt();
                }
                catch (IOException ex) {
                    WcExpelMember.logger.log(Level.WARNING, "Unpack exception " + ex.getMessage(), ex);
                    return;
                }
                finally {
                    StreamUtilities.closeInputStreamIgnoreExceptions(dis);
                }
                if (Servers.isThisLoginServer()) {
                    WcExpelMember.this.sendFromLoginServer();
                }
                PlayerInfoFactory.expelMember(WcExpelMember.this.playerId, WcExpelMember.this.fromKingdomId, WcExpelMember.this.toKingdomId, WcExpelMember.this.originServer);
            }
        }.start();
    }
    
    static {
        logger = Logger.getLogger(WcRemoveFriendship.class.getName());
    }
}
