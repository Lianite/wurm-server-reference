// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.webinterface;

import com.wurmonline.server.kingdom.Kingdom;
import java.io.IOException;
import com.wurmonline.server.HistoryManager;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.Servers;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import com.wurmonline.shared.util.StreamUtilities;
import java.util.logging.Level;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.logging.Logger;

public class WcDeleteKingdom extends WebCommand
{
    private static final Logger logger;
    private byte kingdomId;
    
    public WcDeleteKingdom(final long aId, final byte kingdomToDelete) {
        super(aId, (short)8);
        this.kingdomId = kingdomToDelete;
    }
    
    public WcDeleteKingdom(final long aId, final byte[] aData) {
        super(aId, (short)8, aData);
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
            dos.writeByte(this.kingdomId);
            dos.flush();
            dos.close();
        }
        catch (Exception ex) {
            WcDeleteKingdom.logger.log(Level.WARNING, ex.getMessage(), ex);
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
            Servers.removeKingdomInfo(this.kingdomId = dis.readByte());
            final Kingdom k = Kingdoms.getKingdomOrNull(this.kingdomId);
            if (k != null && k.isCustomKingdom()) {
                k.delete();
                Kingdoms.removeKingdom(this.kingdomId);
                HistoryManager.addHistory(k.getName(), "has faded and is no more.");
            }
        }
        catch (IOException ex) {
            WcDeleteKingdom.logger.log(Level.WARNING, "Unpack exception " + ex.getMessage(), ex);
        }
        finally {
            StreamUtilities.closeInputStreamIgnoreExceptions(dis);
        }
    }
    
    static {
        logger = Logger.getLogger(WcDeleteKingdom.class.getName());
    }
}
