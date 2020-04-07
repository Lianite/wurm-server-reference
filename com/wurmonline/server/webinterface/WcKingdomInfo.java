// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.webinterface;

import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import com.wurmonline.server.kingdom.Kingdom;
import com.wurmonline.shared.util.StreamUtilities;
import java.util.logging.Level;
import com.wurmonline.server.kingdom.Kingdoms;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public class WcKingdomInfo extends WebCommand implements MiscConstants
{
    private static final Logger logger;
    private boolean sendSingleKingdom;
    private byte singleKingdomId;
    
    public WcKingdomInfo(final long aId, final boolean singleKingdom, final byte kingdomId) {
        super(aId, (short)7);
        this.sendSingleKingdom = false;
        this.sendSingleKingdom = singleKingdom;
        this.singleKingdomId = kingdomId;
    }
    
    public WcKingdomInfo(final long aId, final byte[] aData) {
        super(aId, (short)7, aData);
        this.sendSingleKingdom = false;
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
            dos.writeBoolean(this.sendSingleKingdom);
            if (this.sendSingleKingdom) {
                dos.writeInt(1);
                final Kingdom k = Kingdoms.getKingdom(this.singleKingdomId);
                dos.writeByte(k.getId());
                dos.writeUTF(k.getName());
                dos.writeUTF(k.getPassword());
                dos.writeUTF(k.getChatName());
                dos.writeUTF(k.getSuffix());
                dos.writeUTF(k.getFirstMotto());
                dos.writeUTF(k.getSecondMotto());
                dos.writeByte(k.getTemplate());
                dos.writeBoolean(k.acceptsTransfers());
            }
            else {
                final Kingdom[] kingdoms = Kingdoms.getAllKingdoms();
                dos.writeInt(kingdoms.length);
                for (final Kingdom i : kingdoms) {
                    dos.writeByte(i.getId());
                    dos.writeUTF(i.getName());
                    dos.writeUTF(i.getPassword());
                    dos.writeUTF(i.getChatName());
                    dos.writeUTF(i.getSuffix());
                    dos.writeUTF(i.getFirstMotto());
                    dos.writeUTF(i.getSecondMotto());
                    dos.writeByte(i.getTemplate());
                    dos.writeBoolean(i.acceptsTransfers());
                }
            }
            dos.flush();
            dos.close();
        }
        catch (Exception ex) {
            WcKingdomInfo.logger.log(Level.WARNING, ex.getMessage(), ex);
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
                    dis = new DataInputStream(new ByteArrayInputStream(WcKingdomInfo.this.getData()));
                    WcKingdomInfo.this.sendSingleKingdom = dis.readBoolean();
                    final int numKingdoms = dis.readInt();
                    if (!WcKingdomInfo.this.sendSingleKingdom) {
                        Kingdoms.markAllKingdomsForDeletion();
                    }
                    for (int x = 0; x < numKingdoms; ++x) {
                        final byte id = dis.readByte();
                        final String name = dis.readUTF();
                        final String password = dis.readUTF();
                        final String chatName = dis.readUTF();
                        final String suffix = dis.readUTF();
                        final String firstMotto = dis.readUTF();
                        final String secondMotto = dis.readUTF();
                        final byte templateKingdom = dis.readByte();
                        final boolean acceptsTransfers = dis.readBoolean();
                        final Kingdom kingdom = new Kingdom(id, templateKingdom, name, password, chatName, suffix, firstMotto, secondMotto, acceptsTransfers);
                        if (Kingdoms.addKingdom(kingdom)) {
                            WcKingdomInfo.logger.log(Level.INFO, "Received " + name + " in WcKingdomInfo.");
                        }
                    }
                    if (!WcKingdomInfo.this.sendSingleKingdom) {
                        Kingdoms.trimKingdoms();
                    }
                }
                catch (IOException ex) {
                    WcKingdomInfo.logger.log(Level.WARNING, "Unpack exception " + ex.getMessage(), ex);
                }
                finally {
                    StreamUtilities.closeInputStreamIgnoreExceptions(dis);
                }
            }
        }.start();
    }
    
    static {
        logger = Logger.getLogger(WcKingdomInfo.class.getName());
    }
}
