// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.webinterface;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import com.wurmonline.server.DbConnector;
import com.wurmonline.server.deities.Deity;
import java.io.IOException;
import com.wurmonline.server.deities.Deities;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import com.wurmonline.shared.util.StreamUtilities;
import java.util.logging.Level;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.logging.Logger;

public final class WcEpicKarmaCommand extends WebCommand
{
    private static final Logger logger;
    private long[] pids;
    private int[] karmas;
    private int deity;
    private static final String CLEAR_KARMA = "DELETE FROM HELPERS";
    
    public WcEpicKarmaCommand(final long _id, final long[] playerids, final int[] karmaValues, final int _deity) {
        super(_id, (short)16);
        this.pids = playerids;
        this.karmas = karmaValues;
        this.deity = _deity;
    }
    
    public WcEpicKarmaCommand(final long _id, final byte[] _data) {
        super(_id, (short)16, _data);
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
            dos.writeInt(this.pids.length);
            dos.writeInt(this.deity);
            for (int x = 0; x < this.pids.length; ++x) {
                dos.writeLong(this.pids[x]);
                dos.writeInt(this.karmas[x]);
            }
            dos.flush();
            dos.close();
        }
        catch (Exception ex) {
            WcEpicKarmaCommand.logger.log(Level.WARNING, "Problem encoding for Deity " + this.deity + " - " + ex.getMessage(), ex);
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
                    dis = new DataInputStream(new ByteArrayInputStream(WcEpicKarmaCommand.this.getData()));
                    final int nums = dis.readInt();
                    final int lDeity = dis.readInt();
                    final Deity d = Deities.getDeity((lDeity == 3) ? 1 : lDeity);
                    for (int x = 0; x < nums; ++x) {
                        final long pid = dis.readLong();
                        final int val = dis.readInt();
                        if (d != null) {
                            d.setPlayerKarma(pid, val);
                        }
                    }
                }
                catch (IOException ex) {
                    WcEpicKarmaCommand.logger.log(Level.WARNING, "Unpack exception " + ex.getMessage(), ex);
                }
                finally {
                    StreamUtilities.closeInputStreamIgnoreExceptions(dis);
                }
            }
        }.start();
    }
    
    public static void clearKarma() {
        for (final Deity deity : Deities.getDeities()) {
            deity.clearKarma();
        }
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getDeityDbCon();
            ps = dbcon.prepareStatement("DELETE FROM HELPERS");
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException sqx) {
            WcEpicKarmaCommand.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static void loadAllKarmaHelpers() {
        for (final Deity deity : Deities.getDeities()) {
            deity.loadAllKarmaHelpers();
        }
    }
    
    static {
        logger = Logger.getLogger(WcEpicKarmaCommand.class.getName());
    }
}
