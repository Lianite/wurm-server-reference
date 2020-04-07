// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.webinterface;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.Servers;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.creatures.Creatures;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import com.wurmonline.shared.util.StreamUtilities;
import java.util.logging.Level;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public final class WcKillCommand extends WebCommand implements MiscConstants
{
    private static final Logger logger;
    private long wurmID;
    
    public WcKillCommand(final long _id, final long _wurmID) {
        super(_id, (short)36);
        this.wurmID = _wurmID;
    }
    
    WcKillCommand(final long _id, final byte[] _data) {
        super(_id, (short)36, _data);
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
            dos.writeLong(this.wurmID);
            dos.flush();
            dos.close();
        }
        catch (Exception ex) {
            WcKillCommand.logger.log(Level.WARNING, ex.getMessage(), ex);
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
        DataInputStream dis;
        final DataInputStream dataInputStream;
        Creature animal;
        final WcKillCommand wkc;
        new Thread(() -> {
            dis = null;
            try {
                new DataInputStream(new ByteArrayInputStream(this.getData()));
                dis = dataInputStream;
                this.wurmID = dis.readLong();
            }
            catch (IOException ex) {
                WcKillCommand.logger.log(Level.WARNING, ex.getMessage(), ex);
                return;
            }
            finally {
                StreamUtilities.closeInputStreamIgnoreExceptions(dis);
            }
            try {
                animal = Creatures.getInstance().getCreature(this.wurmID);
                animal.die(true, "Died on another server.", true);
            }
            catch (NoSuchCreatureException ex2) {}
            if (Servers.isThisLoginServer()) {
                wkc = new WcKillCommand(this.getWurmId(), this.wurmID);
                wkc.sendFromLoginServer();
            }
        }).start();
    }
    
    static {
        logger = Logger.getLogger(WcKillCommand.class.getName());
    }
}
