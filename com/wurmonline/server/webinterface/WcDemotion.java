// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.webinterface;

import com.wurmonline.server.players.PlayerInfo;
import java.io.IOException;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.Message;
import com.wurmonline.server.WurmId;
import com.wurmonline.server.Servers;
import com.wurmonline.server.players.PlayerInfoFactory;
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

public class WcDemotion extends WebCommand implements MiscConstants
{
    private static final Logger logger;
    private long senderWurmId;
    private long targetWurmId;
    private String responseMsg;
    private short demoteType;
    public static final short CA = 1;
    public static final short CM = 2;
    public static final short GM = 3;
    
    public WcDemotion(final long _id, final long senderId, final long targetId, final short demotionType) {
        super(_id, (short)3);
        this.senderWurmId = senderId;
        this.targetWurmId = targetId;
        this.demoteType = demotionType;
        this.responseMsg = "";
    }
    
    public WcDemotion(final long _id, final long senderId, final long targetId, final short demotionType, final String response) {
        super(_id, (short)3);
        this.senderWurmId = senderId;
        this.targetWurmId = targetId;
        this.demoteType = demotionType;
        this.responseMsg = response;
    }
    
    public WcDemotion(final long _id, final byte[] _data) {
        super(_id, (short)3, _data);
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
            dos.writeShort(this.demoteType);
            dos.writeLong(this.senderWurmId);
            dos.writeLong(this.targetWurmId);
            dos.writeUTF(this.responseMsg);
            dos.flush();
            dos.close();
        }
        catch (Exception ex) {
            WcDemotion.logger.log(Level.WARNING, ex.getMessage(), ex);
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
                WcDemotion.logger.log(Level.INFO, "Demoting Player.");
                DataInputStream dis = null;
                try {
                    dis = new DataInputStream(new ByteArrayInputStream(WcDemotion.this.getData()));
                    WcDemotion.this.demoteType = dis.readShort();
                    WcDemotion.this.senderWurmId = dis.readLong();
                    WcDemotion.this.targetWurmId = dis.readLong();
                    WcDemotion.this.responseMsg = dis.readUTF();
                    WcDemotion.logger.log(Level.INFO, WcDemotion.this.senderWurmId + " attempting demotion of " + WcDemotion.this.targetWurmId + ", response=" + WcDemotion.this.responseMsg);
                    if (WcDemotion.this.responseMsg.length() == 0) {
                        final PlayerInfo pinf = PlayerInfoFactory.getPlayerInfoWithWurmId(WcDemotion.this.targetWurmId);
                        if (pinf != null && pinf.loaded) {
                            WcDemotion.logger.log(Level.INFO, WcDemotion.this.senderWurmId + " triggered demotion for " + ((WcDemotion.this.demoteType == 1) ? "CA" : "CM") + " id " + WcDemotion.this.targetWurmId);
                            String msg = "[" + Servers.getLocalServerName() + "] " + pinf.getName();
                            if (WcDemotion.this.demoteType == 1) {
                                pinf.setIsPlayerAssistant(false);
                                msg += " no longer have the duties of being a community assistant.";
                            }
                            else if (WcDemotion.this.demoteType == 2) {
                                pinf.setMayMute(false);
                                msg += " may no longer mute other players.";
                            }
                            else if (WcDemotion.this.demoteType == 3) {
                                pinf.setDevTalk(false);
                                msg += " may no longer see GM tab.";
                            }
                            else {
                                msg += " unknown demotion.";
                            }
                            final WcDemotion wgi = new WcDemotion(WurmId.getNextWCCommandId(), WcDemotion.this.senderWurmId, pinf.wurmId, WcDemotion.this.demoteType, msg);
                            wgi.sendToServer(WurmId.getOrigin(WcDemotion.this.getWurmId()));
                            WcDemotion.logger.log(Level.INFO, WcDemotion.this.senderWurmId + " sending response back to server " + WurmId.getOrigin(WcDemotion.this.getWurmId()));
                        }
                    }
                    else {
                        WcDemotion.logger.log(Level.INFO, WcDemotion.this.senderWurmId + " receiving demotion response for " + WcDemotion.this.targetWurmId);
                        final Message mess = new Message(null, (byte)3, ":Event", WcDemotion.this.responseMsg);
                        mess.setReceiver(WcDemotion.this.senderWurmId);
                        Server.getInstance().addMessage(mess);
                    }
                }
                catch (IOException ex) {
                    WcDemotion.logger.log(Level.WARNING, "Unpack exception " + ex.getMessage(), ex);
                }
                finally {
                    StreamUtilities.closeInputStreamIgnoreExceptions(dis);
                }
            }
        }.start();
    }
    
    static {
        logger = Logger.getLogger(WcDemotion.class.getName());
    }
}
