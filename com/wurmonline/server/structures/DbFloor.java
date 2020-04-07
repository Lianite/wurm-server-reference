// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.structures;

import com.wurmonline.server.highways.MethodsHighways;
import com.wurmonline.server.tutorial.MissionTargets;
import com.wurmonline.server.zones.VolaTile;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import com.wurmonline.shared.constants.StructureConstants;
import java.util.logging.Logger;

public class DbFloor extends Floor
{
    private static final String CREATE_FLOOR = "INSERT INTO FLOORS(TYPE, LASTMAINTAINED , CURRENTQL, ORIGINALQL, DAMAGE, STRUCTURE, TILEX, TILEY, STATE,COLOR, MATERIAL,HEIGHTOFFSET,LAYER,DIR) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String UPDATE_FLOOR = "UPDATE FLOORS SET TYPE=?, LASTMAINTAINED =?, CURRENTQL=?, ORIGINALQL=?,DAMAGE=?, STRUCTURE=?, STATE=?,MATERIAL=?,HEIGHTOFFSET=?,DIR=? WHERE ID=?";
    private static final String GET_FLOOR = "SELECT * FROM FLOORS WHERE ID=?";
    private static final String DELETE_FLOOR = "DELETE FROM FLOORS WHERE ID=?";
    private static final String SET_DAMAGE = "UPDATE FLOORS SET DAMAGE=? WHERE ID=?";
    private static final String SET_QUALITY_LEVEL = "UPDATE FLOORS SET CURRENTQL=? WHERE ID=?";
    private static final String SET_STATE = "UPDATE FLOORS SET STATE=?,MATERIAL=? WHERE ID=?";
    private static final String SET_LAST_USED = "UPDATE FLOORS SET LASTMAINTAINED=? WHERE ID=?";
    private static final String SET_SETTINGS = "UPDATE FLOORS SET SETTINGS=? WHERE ID=?";
    private static final Logger logger;
    
    @Override
    public boolean isFence() {
        return false;
    }
    
    @Override
    public boolean isWall() {
        return false;
    }
    
    public DbFloor(final int id, final StructureConstants.FloorType floorType, final int tilex, final int tiley, final byte aDbState, final int heightOffset, final float currentQl, final long structureId, final StructureConstants.FloorMaterial floorMaterial, final int layer, final float origQL, final float aDamage, final long lastmaintained, final byte dir) {
        super(id, floorType, tilex, tiley, aDbState, heightOffset, currentQl, structureId, floorMaterial, layer, origQL, aDamage, lastmaintained, dir);
    }
    
    public DbFloor(final StructureConstants.FloorType floorType, final int tilex, final int tiley, final int heightOffset, final float qualityLevel, final long structure, final StructureConstants.FloorMaterial material, final int layer) {
        super(floorType, tilex, tiley, heightOffset, qualityLevel, structure, material, layer);
    }
    
    @Override
    protected void setState(final byte newState) {
        if (this.dbState != newState) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                this.dbState = newState;
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE FLOORS SET STATE=?,MATERIAL=? WHERE ID=?");
                ps.setByte(1, this.dbState);
                ps.setByte(2, this.getMaterial().getCode());
                ps.setInt(3, this.getNumber());
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbFloor.logger.log(Level.WARNING, "Failed to set state to " + newState + " for floor with id " + this.getNumber(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public void save() throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            if (this.exists(dbcon)) {
                ps = dbcon.prepareStatement("UPDATE FLOORS SET TYPE=?, LASTMAINTAINED =?, CURRENTQL=?, ORIGINALQL=?,DAMAGE=?, STRUCTURE=?, STATE=?,MATERIAL=?,HEIGHTOFFSET=?,DIR=? WHERE ID=?");
                ps.setByte(1, this.getType().getCode());
                ps.setLong(2, this.getLastUsed());
                ps.setFloat(3, this.getCurrentQL());
                ps.setFloat(4, this.getOriginalQL());
                ps.setFloat(5, this.getDamage());
                ps.setLong(6, this.getStructureId());
                ps.setByte(7, this.getState());
                ps.setByte(8, this.getMaterial().getCode());
                ps.setInt(9, this.getHeightOffset());
                ps.setByte(10, this.getDir());
                ps.setInt(11, this.getNumber());
                ps.executeUpdate();
            }
            else {
                ps = dbcon.prepareStatement("INSERT INTO FLOORS(TYPE, LASTMAINTAINED , CURRENTQL, ORIGINALQL, DAMAGE, STRUCTURE, TILEX, TILEY, STATE,COLOR, MATERIAL,HEIGHTOFFSET,LAYER,DIR) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)", 1);
                ps.setByte(1, this.getType().getCode());
                ps.setLong(2, this.getLastUsed());
                ps.setFloat(3, this.getCurrentQL());
                ps.setFloat(4, this.getOriginalQL());
                ps.setFloat(5, this.getDamage());
                ps.setLong(6, this.getStructureId());
                ps.setInt(7, this.getTileX());
                ps.setInt(8, this.getTileY());
                ps.setByte(9, this.getState());
                ps.setInt(10, this.getColor());
                ps.setByte(11, this.getMaterial().getCode());
                ps.setInt(12, this.getHeightOffset());
                ps.setByte(13, this.getLayer());
                ps.setByte(14, this.getDir());
                ps.executeUpdate();
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    this.setNumber(rs.getInt(1));
                }
            }
        }
        catch (SQLException sqx) {
            throw new IOException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public boolean setDamage(final float aDamage) {
        boolean forcePlan = false;
        if (this.isIndestructible()) {
            return false;
        }
        if (aDamage >= 100.0f) {
            final VolaTile tile = this.getTile();
            if (tile != null) {
                final Structure struct = tile.getStructure();
                if (struct != null && struct.wouldCreateFlyingStructureIfRemoved(this)) {
                    forcePlan = true;
                }
            }
            if (forcePlan) {
                this.setFloorState(StructureConstants.FloorState.PLANNING);
                this.setQualityLevel(1.0f);
                if (tile != null) {
                    tile.updateFloor(this);
                }
            }
        }
        if (this.damage != aDamage) {
            boolean updateState = false;
            if ((this.damage >= 60.0f && aDamage < 60.0f) || (this.damage < 60.0f && aDamage >= 60.0f)) {
                updateState = true;
            }
            this.damage = aDamage;
            if (forcePlan) {
                this.damage = 0.0f;
            }
            if (this.damage < 100.0f) {
                Connection dbcon = null;
                PreparedStatement ps = null;
                try {
                    dbcon = DbConnector.getZonesDbCon();
                    ps = dbcon.prepareStatement("UPDATE FLOORS SET DAMAGE=? WHERE ID=?");
                    ps.setFloat(1, this.getDamage());
                    ps.setInt(2, this.getNumber());
                    ps.executeUpdate();
                }
                catch (SQLException sqx) {
                    DbFloor.logger.log(Level.WARNING, this.getName() + ", " + this.getNumber() + " " + sqx.getMessage(), sqx);
                }
                finally {
                    DbUtilities.closeDatabaseObjects(ps, null);
                    DbConnector.returnConnection(dbcon);
                }
                if (updateState) {
                    final VolaTile tile2 = this.getTile();
                    if (tile2 != null) {
                        this.getTile().updateFloorDamageState(this);
                    }
                }
            }
            else {
                final VolaTile t = this.getTile();
                if (t != null) {
                    t.removeFloor(this);
                }
                this.delete();
            }
        }
        return this.damage >= 100.0f;
    }
    
    @Override
    public void setLastUsed(final long now) {
        if (this.lastUsed != now) {
            this.lastUsed = now;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE FLOORS SET LASTMAINTAINED=? WHERE ID=?");
                ps.setLong(1, this.lastUsed);
                ps.setInt(2, this.getNumber());
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbFloor.logger.log(Level.WARNING, this.getName() + ", " + this.getNumber() + " " + sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    public boolean setQualityLevel(float ql) {
        if (ql > 100.0f) {
            ql = 100.0f;
        }
        if (this.currentQL != ql) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                this.currentQL = ql;
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE FLOORS SET CURRENTQL=? WHERE ID=?");
                ps.setFloat(1, this.currentQL);
                ps.setInt(2, this.getNumber());
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbFloor.logger.log(Level.WARNING, this.getName() + ", " + this.getNumber() + " " + sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
        return ql >= 100.0f;
    }
    
    private boolean exists(final Connection dbcon) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = dbcon.prepareStatement("SELECT * FROM FLOORS WHERE ID=?");
            ps.setInt(1, this.getNumber());
            rs = ps.executeQuery();
            return rs.next();
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
        }
    }
    
    @Override
    public void delete() {
        MissionTargets.destroyMissionTarget(this.getId(), true);
        MethodsHighways.removeNearbyMarkers(this);
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("DELETE FROM FLOORS WHERE ID=?");
            ps.setInt(1, this.getNumber());
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbFloor.logger.log(Level.WARNING, "Failed to delete floor with id " + this.getNumber(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public void savePermissions() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("UPDATE FLOORS SET SETTINGS=? WHERE ID=?");
            ps.setLong(1, this.permissions.getPermissions());
            ps.setLong(2, this.getNumber());
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbFloor.logger.log(Level.WARNING, "Failed to save settings for floor with id " + this.getNumber(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    static {
        logger = Logger.getLogger(DbWall.class.getName());
    }
}
