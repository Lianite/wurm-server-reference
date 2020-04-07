// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.webinterface;

import java.io.IOException;
import com.wurmonline.server.creatures.Creature;
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
import com.wurmonline.server.MiscConstants;

public class WcKingdomChat extends WebCommand implements MiscConstants
{
    private static final Logger logger;
    private String sender;
    private long senderId;
    private String message;
    private byte kingdom;
    private boolean emote;
    private int colorR;
    private int colorG;
    private int colorB;
    
    public WcKingdomChat(final long aId, final long _senderId, final String _sender, final String _message, final boolean _emote, final byte _kingdom, final int r, final int g, final int b) {
        super(aId, (short)13);
        this.sender = "unknown";
        this.senderId = -10L;
        this.message = "";
        this.kingdom = 0;
        this.emote = false;
        this.colorR = 0;
        this.colorG = 0;
        this.colorB = 0;
        this.sender = _sender;
        this.senderId = _senderId;
        this.message = _message;
        this.emote = _emote;
        this.kingdom = _kingdom;
        this.colorR = r;
        this.colorG = g;
        this.colorB = b;
    }
    
    public WcKingdomChat(final long _id, final byte[] _data) {
        super(_id, (short)13, _data);
        this.sender = "unknown";
        this.senderId = -10L;
        this.message = "";
        this.kingdom = 0;
        this.emote = false;
        this.colorR = 0;
        this.colorG = 0;
        this.colorB = 0;
    }
    
    @Override
    public boolean autoForward() {
        return true;
    }
    
    public byte[] encode() {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = null;
        byte[] barr = null;
        try {
            dos = new DataOutputStream(bos);
            dos.writeUTF(this.sender);
            dos.writeLong(this.senderId);
            dos.writeUTF(this.message);
            dos.writeBoolean(this.emote);
            dos.writeByte(this.kingdom);
            dos.writeInt(this.colorR);
            dos.writeInt(this.colorG);
            dos.writeInt(this.colorB);
            dos.flush();
            dos.close();
        }
        catch (Exception ex) {
            WcKingdomChat.logger.log(Level.WARNING, ex.getMessage(), ex);
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
                    dis = new DataInputStream(new ByteArrayInputStream(WcKingdomChat.this.getData()));
                    WcKingdomChat.this.sender = dis.readUTF();
                    WcKingdomChat.this.senderId = dis.readLong();
                    WcKingdomChat.this.message = dis.readUTF();
                    WcKingdomChat.this.emote = dis.readBoolean();
                    WcKingdomChat.this.kingdom = dis.readByte();
                    WcKingdomChat.this.colorR = dis.readInt();
                    WcKingdomChat.this.colorG = dis.readInt();
                    WcKingdomChat.this.colorB = dis.readInt();
                    Players.getInstance().sendGlobalKingdomMessage(null, WcKingdomChat.this.senderId, WcKingdomChat.this.sender, WcKingdomChat.this.message, WcKingdomChat.this.emote, WcKingdomChat.this.kingdom, WcKingdomChat.this.colorR, WcKingdomChat.this.colorG, WcKingdomChat.this.colorB);
                }
                catch (IOException ex) {
                    WcKingdomChat.logger.log(Level.WARNING, "Unpack exception " + ex.getMessage(), ex);
                }
                finally {
                    StreamUtilities.closeInputStreamIgnoreExceptions(dis);
                }
            }
        }.start();
    }
    
    static {
        logger = Logger.getLogger(WcKingdomChat.class.getName());
    }
}
