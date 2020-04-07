// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import com.wurmonline.server.TimeConstants;

public final class Cooldowns implements TimeConstants
{
    private static final Logger logger;
    private static final String loadCooldowns = "SELECT * FROM COOLDOWNS";
    private static final String deleteCooldownsFor = "DELETE FROM COOLDOWNS WHERE OWNERID=?";
    private static final String createCooldown = "INSERT INTO COOLDOWNS (OWNERID,SPELLID,AVAILABLE) VALUES(?,?,?)";
    private static final String updateCooldown = "UPDATE COOLDOWNS SET AVAILABLE=? WHERE OWNERID=? AND SPELLID=?";
    public final Map<Integer, Long> cooldowns;
    private static final Map<Long, Cooldowns> allCooldowns;
    private final long ownerid;
    
    private Cooldowns(final long _ownerid) {
        this.cooldowns = new HashMap<Integer, Long>();
        this.ownerid = _ownerid;
    }
    
    public static final Cooldowns getCooldownsFor(final long creatureId, final boolean create) {
        Cooldowns cd = Cooldowns.allCooldowns.get(creatureId);
        if (create && cd == null) {
            cd = new Cooldowns(creatureId);
            Cooldowns.allCooldowns.put(creatureId, cd);
        }
        return cd;
    }
    
    public void addCooldown(final int spellid, final long availableAt, final boolean loading) {
        final boolean update = this.cooldowns.containsKey(spellid);
        this.cooldowns.put(spellid, availableAt);
        if (!loading && System.currentTimeMillis() - availableAt > 600000L) {
            if (update) {
                this.updateToDisk(spellid, availableAt);
            }
            else {
                this.saveToDisk(spellid, availableAt);
            }
        }
    }
    
    public long isAvaibleAt(final int spellid) {
        final Integer tocheck = spellid;
        if (this.cooldowns.containsKey(tocheck)) {
            return this.cooldowns.get(tocheck);
        }
        return 0L;
    }
    
    private void saveToDisk(final int spellid, final long availableAt) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("INSERT INTO COOLDOWNS (OWNERID,SPELLID,AVAILABLE) VALUES(?,?,?)");
            ps.setLong(1, this.ownerid);
            ps.setInt(2, spellid);
            ps.setLong(3, availableAt);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            Cooldowns.logger.log(Level.WARNING, sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private void updateToDisk(final int spellid, final long availableAt) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("UPDATE COOLDOWNS SET AVAILABLE=? WHERE OWNERID=? AND SPELLID=?");
            ps.setLong(1, availableAt);
            ps.setLong(2, this.ownerid);
            ps.setInt(3, spellid);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            Cooldowns.logger.log(Level.WARNING, sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static final void deleteCooldownsFor(final long ownerId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("DELETE FROM COOLDOWNS WHERE OWNERID=?");
            ps.setLong(1, ownerId);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            Cooldowns.logger.log(Level.WARNING, sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
        Cooldowns.allCooldowns.remove(ownerId);
    }
    
    public static final void loadAllCooldowns() {
        Cooldowns.logger.log(Level.INFO, "Loading all cooldowns.");
        final long start = System.nanoTime();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM COOLDOWNS");
            rs = ps.executeQuery();
            while (rs.next()) {
                final long ownerId = rs.getLong("OWNERID");
                Cooldowns cd = getCooldownsFor(ownerId, false);
                if (cd == null) {
                    cd = new Cooldowns(ownerId);
                }
                cd.addCooldown(rs.getInt("SPELLID"), rs.getLong("AVAILABLE"), true);
                Cooldowns.allCooldowns.put(ownerId, cd);
            }
        }
        catch (SQLException sqx) {
            Cooldowns.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final long end = System.nanoTime();
            Cooldowns.logger.info("Loaded cooldowns from database took " + (end - start) / 1000000.0f + " ms");
        }
    }
    
    static {
        logger = Logger.getLogger(Cooldowns.class.getName());
        allCooldowns = new HashMap<Long, Cooldowns>();
    }
}
