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

public final class WcMgmtMessage extends WebCommand implements MiscConstants
{
    private static final Logger logger;
    private String sender;
    private String message;
    private boolean emote;
    private boolean logit;
    private int colourR;
    private int colourG;
    private int colourB;
    
    WcMgmtMessage(final long aId, final byte[] _data) {
        super(aId, (short)24, _data);
        this.sender = "unknown";
        this.message = "";
        this.emote = false;
        this.logit = false;
        this.colourR = -1;
        this.colourG = -1;
        this.colourB = -1;
    }
    
    public WcMgmtMessage(final long aId, final String _sender, final String _message, final boolean _emote, final boolean logIt, final int red, final int green, final int blue) {
        super(aId, (short)24);
        this.sender = "unknown";
        this.message = "";
        this.emote = false;
        this.logit = false;
        this.colourR = -1;
        this.colourG = -1;
        this.colourB = -1;
        this.sender = _sender;
        this.message = _message;
        this.emote = _emote;
        this.logit = logIt;
        this.colourR = red;
        this.colourG = green;
        this.colourB = blue;
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
            dos.writeUTF(this.message);
            dos.writeBoolean(this.emote);
            dos.writeBoolean(this.logit);
            dos.writeInt(this.colourR);
            dos.writeInt(this.colourG);
            dos.writeInt(this.colourB);
            dos.flush();
            dos.close();
        }
        catch (Exception ex) {
            WcMgmtMessage.logger.log(Level.WARNING, ex.getMessage(), ex);
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
        DataInputStream dis = null;
        try {
            dis = new DataInputStream(new ByteArrayInputStream(this.getData()));
            this.sender = dis.readUTF();
            this.message = dis.readUTF();
            this.emote = dis.readBoolean();
            this.logit = dis.readBoolean();
            this.colourR = dis.readInt();
            this.colourG = dis.readInt();
            this.colourB = dis.readInt();
            Players.getInstance().sendMgmtMessage(null, this.sender, this.message, this.emote, this.logit, this.colourR, this.colourG, this.colourB);
        }
        catch (IOException ex) {
            WcMgmtMessage.logger.log(Level.WARNING, "Unpack exception " + ex.getMessage(), ex);
        }
        finally {
            StreamUtilities.closeInputStreamIgnoreExceptions(dis);
        }
    }
    
    static {
        logger = Logger.getLogger(WcMgmtMessage.class.getName());
    }
}
