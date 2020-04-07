// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.villages;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.io.IOException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

public final class DbCitizen extends Citizen
{
    private static final Logger logger;
    private static final String SET_ROLEID = "UPDATE CITIZENS SET ROLEID=? WHERE WURMID=?";
    private static final String CREATE_CITIZEN = "INSERT INTO CITIZENS (WURMID, VILLAGEID, ROLEID, VOTEDATE, VOTEDFOR) VALUES (?,?,?,?,?)";
    private static final String GET_CITIZEN = "SELECT * FROM CITIZENS WHERE WURMID=?";
    private static final String SET_VOTEDATE = "UPDATE CITIZENS SET VOTEDATE=? WHERE WURMID=?";
    private static final String SET_VOTEDFOR = "UPDATE CITIZENS SET VOTEDFOR=? WHERE WURMID=?";
    
    DbCitizen(final long aWurmId, final String aName, final VillageRole aRole, final long aVotedate, final long aVotedfor) {
        super(aWurmId, aName, aRole, aVotedate, aVotedfor);
    }
    
    @Override
    void create(final Creature creature, final int villageId) throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            final int num = this.exists(dbcon);
            if (num >= 0) {
                try {
                    final Village village = Villages.getVillage(num);
                    creature.getCommunicator().sendSafeServerMessage("Removing your " + village.getName() + " citizenship.");
                    village.removeCitizen(creature);
                }
                catch (NoSuchVillageException nsv) {
                    DbCitizen.logger.log(Level.WARNING, "Citizens have village id " + num + " but it can't be found.", nsv);
                }
            }
            ps = dbcon.prepareStatement("INSERT INTO CITIZENS (WURMID, VILLAGEID, ROLEID, VOTEDATE, VOTEDFOR) VALUES (?,?,?,?,?)");
            ps.setLong(1, this.wurmId);
            ps.setInt(2, villageId);
            ps.setInt(3, this.role.getId());
            ps.setLong(4, this.voteDate);
            ps.setLong(5, this.votedFor);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbCitizen.logger.log(Level.WARNING, "Failed to set status for citizen " + this.name + ": " + sqx.getMessage(), sqx);
            throw new IOException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public void setRole(final VillageRole aRole) throws IOException {
        if (this.role != aRole) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                this.role = aRole;
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE CITIZENS SET ROLEID=? WHERE WURMID=?");
                ps.setInt(1, aRole.getId());
                ps.setLong(2, this.wurmId);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbCitizen.logger.log(Level.WARNING, "Failed to set status for citizen " + this.name + ": " + sqx.getMessage(), sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    void setVoteDate(final long votedate) throws IOException {
        if (this.voteDate != votedate) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                this.voteDate = votedate;
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE CITIZENS SET VOTEDATE=? WHERE WURMID=?");
                ps.setLong(1, this.voteDate);
                ps.setLong(2, this.wurmId);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbCitizen.logger.log(Level.WARNING, "Failed to set vote date for citizen " + this.name + ": " + sqx.getMessage(), sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    void setVotedFor(final long votedfor) throws IOException {
        if (this.votedFor != votedfor) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                this.votedFor = votedfor;
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE CITIZENS SET VOTEDFOR=? WHERE WURMID=?");
                ps.setLong(1, this.votedFor);
                ps.setLong(2, this.wurmId);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbCitizen.logger.log(Level.WARNING, "Failed to set votedFor for citizen " + this.name + ": " + sqx.getMessage(), sqx);
                throw new IOException(sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    private int exists(final Connection dbcon) throws IOException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = dbcon.prepareStatement("SELECT * FROM CITIZENS WHERE WURMID=?");
            ps.setLong(1, this.wurmId);
            rs = ps.executeQuery();
            if (rs.next()) {
                final int village = rs.getInt("VILLAGEID");
                return village;
            }
            return -1;
        }
        catch (SQLException sqx) {
            DbCitizen.logger.log(Level.WARNING, "Failed to check if citizen " + this.name + " exists: " + sqx.getMessage(), sqx);
            throw new IOException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
        }
    }
    
    static {
        logger = Logger.getLogger(DbCitizen.class.getName());
    }
}
