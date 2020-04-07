// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.webinterface;

import com.wurmonline.server.ServerEntry;
import java.util.Set;
import java.io.IOException;
import com.wurmonline.server.Servers;
import com.wurmonline.server.WurmId;
import java.util.HashSet;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import com.wurmonline.shared.util.StreamUtilities;
import java.util.logging.Level;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import com.wurmonline.server.players.Spawnpoint;
import java.util.logging.Logger;

public final class WcSpawnPoints extends WebCommand
{
    private static final Logger logger;
    private Spawnpoint[] spawns;
    
    public WcSpawnPoints(final long _id) {
        super(_id, (short)21);
    }
    
    public final void setSpawns(final Spawnpoint[] spawnpoints) {
        this.spawns = spawnpoints;
    }
    
    public WcSpawnPoints(final long _id, final byte[] _data) {
        super(_id, (short)21, _data);
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
            if (this.spawns == null) {
                dos.writeInt(0);
            }
            else {
                dos.writeInt(this.spawns.length);
                for (final Spawnpoint spawn : this.spawns) {
                    dos.writeShort(spawn.tilex);
                    dos.writeShort(spawn.tiley);
                    dos.writeUTF(spawn.name);
                    dos.writeUTF(spawn.description);
                    dos.writeByte(spawn.kingdom);
                }
            }
            dos.flush();
            dos.close();
        }
        catch (Exception ex) {
            WcSpawnPoints.logger.log(Level.WARNING, ex.getMessage(), ex);
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
            final int nums = dis.readInt();
            if (nums > 0) {
                final Set<Spawnpoint> lspawns = new HashSet<Spawnpoint>();
                for (int x = 0; x < nums; ++x) {
                    final short tilex = dis.readShort();
                    final short tiley = dis.readShort();
                    final String name = dis.readUTF();
                    final String desc = dis.readUTF();
                    final byte kingdom = dis.readByte();
                    lspawns.add(new Spawnpoint(name, (byte)x, desc, tilex, tiley, true, kingdom));
                }
                if (lspawns.size() > 0) {
                    final ServerEntry entry = Servers.getServerWithId(WurmId.getOrigin(this.getWurmId()));
                    if (entry != null) {
                        entry.setSpawns(lspawns.toArray(new Spawnpoint[lspawns.size()]));
                    }
                }
            }
        }
        catch (IOException ex) {
            WcSpawnPoints.logger.log(Level.WARNING, "Unpack exception " + ex.getMessage(), ex);
        }
        finally {
            StreamUtilities.closeInputStreamIgnoreExceptions(dis);
        }
    }
    
    static {
        logger = Logger.getLogger(WcSpawnPoints.class.getName());
    }
}
