// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.webinterface;

import com.wurmonline.server.ServerEntry;
import java.io.IOException;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.Message;
import com.wurmonline.server.Players;
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

public final class WcCAHelpGroupMessage extends WebCommand implements MiscConstants
{
    private static final Logger logger;
    private byte groupId;
    private byte kingdom;
    private String name;
    private String msg;
    private boolean emote;
    private int colourR;
    private int colourG;
    private int colourB;
    
    public WcCAHelpGroupMessage(final byte caHelpGroup, final byte kingdom, final String playerName, final String message, final boolean aEmote, final int red, final int green, final int blue) {
        super(WurmId.getNextWCCommandId(), (short)23);
        this.groupId = -1;
        this.kingdom = 4;
        this.name = "";
        this.msg = "";
        this.emote = false;
        this.colourR = -1;
        this.colourG = -1;
        this.colourB = -1;
        this.groupId = caHelpGroup;
        this.kingdom = kingdom;
        this.name = playerName;
        this.msg = message;
        this.emote = aEmote;
        this.colourR = red;
        this.colourG = green;
        this.colourB = blue;
    }
    
    public WcCAHelpGroupMessage(final byte caHelpGroup) {
        super(WurmId.getNextWCCommandId(), (short)23);
        this.groupId = -1;
        this.kingdom = 4;
        this.name = "";
        this.msg = "";
        this.emote = false;
        this.colourR = -1;
        this.colourG = -1;
        this.colourB = -1;
        this.groupId = caHelpGroup;
    }
    
    WcCAHelpGroupMessage(final long aId, final byte[] _data) {
        super(aId, (short)23, _data);
        this.groupId = -1;
        this.kingdom = 4;
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
            dos.writeByte(this.groupId);
            dos.writeByte(this.kingdom);
            dos.writeUTF(this.name);
            dos.writeUTF(this.msg);
            dos.writeBoolean(this.emote);
            dos.writeInt(this.colourR);
            dos.writeInt(this.colourG);
            dos.writeInt(this.colourB);
            dos.flush();
            dos.close();
        }
        catch (Exception ex) {
            WcCAHelpGroupMessage.logger.log(Level.WARNING, ex.getMessage(), ex);
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
            this.groupId = dis.readByte();
            this.kingdom = dis.readByte();
            this.name = dis.readUTF();
            this.msg = dis.readUTF();
            this.emote = dis.readBoolean();
            this.colourR = dis.readInt();
            this.colourG = dis.readInt();
            this.colourB = dis.readInt();
            if (this.groupId != -1 && this.msg.length() > 0 && Servers.isThisLoginServer()) {
                final WcCAHelpGroupMessage wchgm = new WcCAHelpGroupMessage(this.groupId, this.kingdom, this.name, this.msg, this.emote, this.colourR, this.colourG, this.colourB);
                for (final ServerEntry se : Servers.getAllServers()) {
                    if (se.getCAHelpGroup() == this.groupId && se.getId() != Servers.getLocalServerId() && se.getId() != WurmId.getOrigin(this.getWurmId())) {
                        wchgm.sendToServer(se.getId());
                    }
                }
            }
            if (this.msg.length() == 0) {
                Servers.localServer.updateCAHelpGroup(this.groupId);
            }
            else if (Servers.localServer.getCAHelpGroup() == this.groupId) {
                final String chan = Players.getKingdomHelpChannelName(this.kingdom);
                if (chan.length() > 0) {
                    Message mess;
                    if (this.emote) {
                        mess = new Message(null, (byte)6, chan, this.msg, this.colourR, this.colourG, this.colourB);
                    }
                    else {
                        mess = new Message(null, (byte)12, chan, "<" + this.name + "> " + this.msg, this.colourR, this.colourG, this.colourB);
                    }
                    if (this.kingdom == 4) {
                        Players.getInstance().sendPaMessage(mess);
                    }
                    else {
                        Players.getInstance().sendCaMessage(this.kingdom, mess);
                    }
                }
            }
        }
        catch (IOException ex) {
            WcCAHelpGroupMessage.logger.log(Level.WARNING, "Unpack exception " + ex.getMessage(), ex);
        }
        finally {
            StreamUtilities.closeInputStreamIgnoreExceptions(dis);
        }
    }
    
    static {
        logger = Logger.getLogger(WcCAHelpGroupMessage.class.getName());
    }
}
