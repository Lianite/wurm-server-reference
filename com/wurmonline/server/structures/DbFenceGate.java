// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.structures;

import com.wurmonline.server.items.DbStrings;
import com.wurmonline.server.Items;
import com.wurmonline.server.players.PermissionsHistories;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import java.util.logging.Logger;

public final class DbFenceGate extends FenceGate
{
    private static final Logger logger;
    private static final String GET_GATE = "SELECT * FROM GATES WHERE ID=?";
    private static final String EXISTS_GATE = "SELECT 1 FROM GATES WHERE ID=?";
    private static final String CREATE_GATE = "INSERT INTO GATES (NAME,OPENTIME,CLOSETIME,LOCKID,VILLAGE,ID) VALUES(?,?,?,?,?,?)";
    private static final String UPDATE_GATE = "UPDATE GATES SET NAME=?,OPENTIME=?,CLOSETIME=?,LOCKID=?,VILLAGE=? WHERE ID=?";
    private static final String DELETE_GATE = "DELETE FROM GATES WHERE ID=?";
    private static final String SET_NAME = "UPDATE GATES SET NAME=? WHERE ID=?";
    private static final String SET_OPEN_TIME = "UPDATE GATES SET OPENTIME=? WHERE ID=?";
    private static final String SET_CLOSE_TIME = "UPDATE GATES SET CLOSETIME=? WHERE ID=?";
    private static final String SET_LOCKID = "UPDATE GATES SET LOCKID=? WHERE ID=?";
    private static final String SET_VILLAGEID = "UPDATE GATES SET VILLAGE=? WHERE ID=?";
    
    public DbFenceGate(final Fence aFence) {
        super(aFence);
    }
    
    @Override
    public void save() throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            String string = "INSERT INTO GATES (NAME,OPENTIME,CLOSETIME,LOCKID,VILLAGE,ID) VALUES(?,?,?,?,?,?)";
            if (this.exists(dbcon)) {
                string = "UPDATE GATES SET NAME=?,OPENTIME=?,CLOSETIME=?,LOCKID=?,VILLAGE=? WHERE ID=?";
            }
            ps = dbcon.prepareStatement(string);
            ps.setString(1, this.getName());
            ps.setByte(2, (byte)this.getOpenTime());
            ps.setByte(3, (byte)this.getCloseTime());
            ps.setLong(4, this.lock);
            ps.setInt(5, this.getVillageId());
            ps.setLong(6, this.getFence().getId());
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            DbFenceGate.logger.log(Level.WARNING, "Failed to save gate with id " + this.getFence().getId(), ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private boolean exists(final Connection dbcon) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = dbcon.prepareStatement("SELECT 1 FROM GATES WHERE ID=?");
            ps.setFetchSize(1);
            ps.setLong(1, this.getFence().getId());
            rs = ps.executeQuery();
            return rs.next();
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
        }
    }
    
    @Override
    void load() throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM GATES WHERE ID=?");
            ps.setFetchSize(1);
            ps.setLong(1, this.getFence().getId());
            rs = ps.executeQuery();
            if (rs.next()) {
                this.openTime = rs.getByte("OPENTIME");
                this.closeTime = rs.getByte("CLOSETIME");
                this.name = rs.getString("NAME");
                this.lock = rs.getLong("LOCKID");
                this.villageId = rs.getInt("VILLAGE");
            }
            else {
                this.save();
            }
        }
        catch (SQLException ex) {
            DbFenceGate.logger.log(Level.WARNING, "Failed to load gate with id " + this.getFence().getId(), ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public void delete() {
        DbFenceGate.gates.remove(new Long(this.getFence().getId()));
        DoorSettings.remove(this.getFence().getId());
        PermissionsHistories.remove(this.getFence().getId());
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("DELETE FROM GATES WHERE ID=?");
            ps.setLong(1, this.getFence().getId());
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbFenceGate.logger.log(Level.WARNING, "Failed to delete fencegate with id " + this.getFence().getId(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
        if (this.lock != -10L) {
            Items.decay(this.lock, null);
            this.lock = -10L;
        }
    }
    
    @Override
    public void setOpenTime(final int time) {
        if (this.getOpenTime() != time) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                this.openTime = time;
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE GATES SET OPENTIME=? WHERE ID=?");
                ps.setByte(1, (byte)this.getOpenTime());
                ps.setLong(2, this.getFence().getId());
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbFenceGate.logger.log(Level.WARNING, "Failed to set opentime to " + this.getOpenTime() + " for fencegate with id " + this.getFence().getId(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void setCloseTime(final int time) {
        if (this.getCloseTime() != time) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                this.closeTime = time;
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE GATES SET CLOSETIME=? WHERE ID=?");
                ps.setByte(1, (byte)this.getCloseTime());
                ps.setLong(2, this.getFence().getId());
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbFenceGate.logger.log(Level.WARNING, "Failed to set closetime to " + this.getCloseTime() + " fencegate with id " + this.getFence().getId(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void setName(final String aName) {
        final String newname = aName.substring(0, Math.min(39, aName.length()));
        if (!this.getName().equals(newname)) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                this.name = newname;
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE GATES SET NAME=? WHERE ID=?");
                ps.setString(1, this.getName());
                ps.setLong(2, this.getFence().getId());
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbFenceGate.logger.log(Level.WARNING, "Failed to set name to " + this.getName() + " for fencegate with id " + this.getFence().getId(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void setLock(final long lockid) {
        if (this.lock != lockid) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                this.lock = lockid;
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE GATES SET LOCKID=? WHERE ID=?");
                ps.setLong(1, lockid);
                ps.setLong(2, this.getFence().getId());
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbFenceGate.logger.log(Level.WARNING, "Failed to set lock for fencegate with id " + this.getFence().getId(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void setVillageId(final int newVillageId) {
        if (this.getVillageId() != newVillageId) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                this.villageId = newVillageId;
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE GATES SET VILLAGE=? WHERE ID=?");
                ps.setString(1, this.getName());
                ps.setLong(2, this.getVillageId());
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbFenceGate.logger.log(Level.WARNING, "Failed to set villageId to " + this.getVillageId() + " for fencegate with id " + this.getFence().getId(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public boolean isItem() {
        return false;
    }
    
    static {
        logger = Logger.getLogger(DbFenceGate.class.getName());
    }
}
