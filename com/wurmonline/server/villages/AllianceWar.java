// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.villages;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import com.wurmonline.server.DbConnector;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.wurmonline.server.TimeConstants;

public class AllianceWar implements TimeConstants
{
    private static final Logger logger;
    private final int aggressor;
    private final int defender;
    private final long declarationTime;
    private long warStartedTime;
    private long peaceDeclaredTime;
    private static final String LOAD_ALL = "SELECT * FROM ALLIANCEWARS";
    private static final String INSERTWAR = "INSERT INTO ALLIANCEWARS (ALLIANCEONE,ALLIANCETWO,TIMEDECLARED) VALUES (?,?,?)";
    private static final String DELETEWAR = "DELETE FROM ALLIANCEWARS WHERE ALLIANCEONE=? AND ALLIANCETWO=?";
    private static final String SETWARSTARTED = "UPDATE ALLIANCEWARS SET TIMESTARTED=? WHERE ALLIANCEONE=? AND ALLIANCETWO=?";
    private static final String SETPEACEWANTED = "UPDATE ALLIANCEWARS SET TIMEPEACE=? WHERE ALLIANCEONE=? AND ALLIANCETWO=?";
    public static final long TIME_UNTIL_PEACE = 345600000L;
    
    public AllianceWar(final int aggressorId, final int defenderId) {
        this.aggressor = aggressorId;
        this.defender = defenderId;
        this.declarationTime = System.currentTimeMillis();
        final PvPAlliance one = PvPAlliance.getPvPAlliance(this.aggressor);
        if (one != null) {
            final PvPAlliance two = PvPAlliance.getPvPAlliance(this.defender);
            if (two != null) {
                one.addAllianceWar(this);
                two.addAllianceWar(this);
                this.create();
            }
        }
    }
    
    public AllianceWar(final int aggressorId, final int defenderId, final long declared, final long started, final long peacetime) {
        this.aggressor = aggressorId;
        this.defender = defenderId;
        this.declarationTime = declared;
        this.warStartedTime = started;
        this.peaceDeclaredTime = peacetime;
    }
    
    public static final void loadAll() {
        AllianceWar.logger.log(Level.INFO, "Loading all alliance wars.");
        final long start = System.nanoTime();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM ALLIANCEWARS");
            rs = ps.executeQuery();
            while (rs.next()) {
                final int allianceOne = rs.getInt("ALLIANCEONE");
                final int allianceTwo = rs.getInt("ALLIANCETWO");
                final long declared = rs.getLong("TIMEDECLARED");
                final long started = rs.getLong("TIMESTARTED");
                final long peace = rs.getLong("TIMEPEACE");
                final PvPAlliance one = PvPAlliance.getPvPAlliance(allianceOne);
                if (one != null) {
                    final PvPAlliance two = PvPAlliance.getPvPAlliance(allianceTwo);
                    if (two == null) {
                        continue;
                    }
                    final AllianceWar war = new AllianceWar(allianceOne, allianceTwo, declared, started, peace);
                    one.addAllianceWar(war);
                    two.addAllianceWar(war);
                }
            }
        }
        catch (SQLException sqx) {
            AllianceWar.logger.log(Level.WARNING, "Failed to load pvp alliance wars " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final long end = System.nanoTime();
            AllianceWar.logger.info("Loaded alliance wars from database took " + (end - start) / 1000000.0f + " ms");
        }
    }
    
    public final void delete() {
        final PvPAlliance one = PvPAlliance.getPvPAlliance(this.aggressor);
        if (one != null) {
            one.removeWar(this);
        }
        final PvPAlliance two = PvPAlliance.getPvPAlliance(this.defender);
        if (two != null) {
            two.removeWar(this);
        }
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("DELETE FROM ALLIANCEWARS WHERE ALLIANCEONE=? AND ALLIANCETWO=?");
            ps.setInt(1, this.aggressor);
            ps.setInt(2, this.defender);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            AllianceWar.logger.log(Level.WARNING, "Failed to insert pvp alliance war " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private final void create() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("INSERT INTO ALLIANCEWARS (ALLIANCEONE,ALLIANCETWO,TIMEDECLARED) VALUES (?,?,?)");
            ps.setInt(1, this.aggressor);
            ps.setInt(2, this.defender);
            ps.setLong(3, this.declarationTime);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            AllianceWar.logger.log(Level.WARNING, "Failed to insert pvp alliance war " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public final boolean hasBeenAccepted() {
        return this.warStartedTime > 0L;
    }
    
    public final boolean isActive() {
        return this.hasBeenAccepted() && !this.hasEnded();
    }
    
    public final boolean hasEnded() {
        return System.currentTimeMillis() > this.peaceDeclaredTime;
    }
    
    public int getAggressor() {
        return this.aggressor;
    }
    
    public int getDefender() {
        return this.defender;
    }
    
    public long getDeclarationTime() {
        return this.declarationTime;
    }
    
    public long getWarStartedTime() {
        return this.warStartedTime;
    }
    
    public void setWarStartedTime() {
        this.warStartedTime = System.currentTimeMillis();
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("UPDATE ALLIANCEWARS SET TIMESTARTED=? WHERE ALLIANCEONE=? AND ALLIANCETWO=?");
            ps.setLong(1, this.warStartedTime);
            ps.setInt(2, this.aggressor);
            ps.setInt(3, this.defender);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            AllianceWar.logger.log(Level.WARNING, "Failed to set war started in pvp alliance war " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public long getPeaceDeclaredTime() {
        return this.peaceDeclaredTime;
    }
    
    public void setPeaceDeclaredTime() {
        this.peaceDeclaredTime = System.currentTimeMillis() + 345600000L;
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("UPDATE ALLIANCEWARS SET TIMEPEACE=? WHERE ALLIANCEONE=? AND ALLIANCETWO=?");
            ps.setLong(1, this.peaceDeclaredTime);
            ps.setInt(2, this.aggressor);
            ps.setInt(3, this.defender);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            AllianceWar.logger.log(Level.WARNING, "Failed to set peace declared in pvp alliance war " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    static {
        logger = Logger.getLogger(AllianceWar.class.getName());
    }
}
