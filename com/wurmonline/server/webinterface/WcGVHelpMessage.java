// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.webinterface;

import com.wurmonline.server.ServerEntry;
import java.io.IOException;
import com.wurmonline.server.Players;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.Message;
import com.wurmonline.server.Server;
import com.wurmonline.server.Servers;
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

public final class WcGVHelpMessage extends WebCommand implements MiscConstants
{
    private static final Logger logger;
    private String name;
    private String msg;
    private boolean emote;
    private int colourR;
    private int colourG;
    private int colourB;
    
    public WcGVHelpMessage(final String playerName, final String message, final boolean aEmote, final int red, final int green, final int blue) {
        super(WurmId.getNextWCCommandId(), (short)29);
        this.name = "";
        this.msg = "";
        this.emote = false;
        this.colourR = -1;
        this.colourG = -1;
        this.colourB = -1;
        this.name = playerName;
        this.msg = message;
        this.emote = aEmote;
        this.colourR = red;
        this.colourG = green;
        this.colourB = blue;
    }
    
    WcGVHelpMessage(final long aId, final byte[] _data) {
        super(aId, (short)29, _data);
        this.name = "";
        this.msg = "";
        this.emote = false;
        this.colourR = -1;
        this.colourG = -1;
        this.colourB = -1;
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
            dos.writeUTF(this.name);
            dos.writeUTF(this.msg);
            dos.writeBoolean(this.emote);
            dos.write((byte)this.colourR);
            dos.write((byte)this.colourG);
            dos.write((byte)this.colourB);
            dos.flush();
            dos.close();
        }
        catch (Exception ex) {
            WcGVHelpMessage.logger.log(Level.WARNING, ex.getMessage(), ex);
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
            this.name = dis.readUTF();
            this.msg = dis.readUTF();
            this.emote = dis.readBoolean();
            this.colourR = dis.read();
            this.colourG = dis.read();
            this.colourB = dis.read();
            if (Servers.isThisLoginServer() && !Server.getInstance().isPS()) {
                for (final ServerEntry se : Servers.getAllServers()) {
                    if (se.getId() != Servers.getLocalServerId() && se.getId() != WurmId.getOrigin(this.getWurmId())) {
                        final WcGVHelpMessage wchgm = new WcGVHelpMessage(this.name, this.msg, this.emote, this.colourR, this.colourG, this.colourB);
                        wchgm.sendToServer(se.getId());
                    }
                }
            }
            if (Servers.isThisLoginServer()) {
                Message mess;
                if (this.emote) {
                    mess = new Message(null, (byte)6, "CA HELP", this.msg, this.colourR, this.colourG, this.colourB);
                }
                else {
                    mess = new Message(null, (byte)12, "CA HELP", "<" + this.name + "> " + this.msg, this.colourR, this.colourG, this.colourB);
                }
                Players.getInstance().sendPaMessage(mess);
            }
            else {
                Message mess;
                if (this.emote) {
                    mess = new Message(null, (byte)6, "GV HELP", this.msg, this.colourR, this.colourG, this.colourB);
                }
                else {
                    mess = new Message(null, (byte)12, "GV HELP", "<" + this.name + "> " + this.msg, this.colourR, this.colourG, this.colourB);
                }
                Players.getInstance().sendGVMessage(mess);
            }
        }
        catch (IOException ex) {
            WcGVHelpMessage.logger.log(Level.WARNING, "Unpack exception " + ex.getMessage(), ex);
        }
        finally {
            StreamUtilities.closeInputStreamIgnoreExceptions(dis);
        }
    }
    
    static {
        logger = Logger.getLogger(WcGVHelpMessage.class.getName());
    }
}
